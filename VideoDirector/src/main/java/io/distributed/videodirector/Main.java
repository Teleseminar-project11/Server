package io.distributed.videodirector;

import com.google.gson.*;
import java.io.*;
import java.util.ArrayList;
import spark.Request;
import spark.Response;
import static spark.Spark.*;

/**
 * @author gonzo
 * 
 * 
**/
public class Main
{
    static Director server = new Director();
    static int id_counter  = 0;
    
    private static int copyInputStream(InputStream  in, 
                                       OutputStream out)
    throws IOException
    {
        byte[] buffer = new byte[1024];
        int len;
        int total = 0;
        
        while ((len = in.read(buffer)) >= 0)
        {
            out.write(buffer, 0, len);
            total += len;
            System.out.print(".");
        }
        return total;
    }
    
    private static int getSessionID(Request req)
    {
        Integer id = req.session().attribute("id");
        if (id == null)
        {
            req.session().attribute("id", ++id_counter);
            return id_counter;
        }
        return id;
    }
    
    public static void main(String[] args)
    {
        setPort(1234);
        //setIpAddress("192.168.137.1");
        
        /// GET /events
        /// Lists all events (including videos) in JSON ///
        get("/events",
        (request, response) ->
        {
            response.type("application/json");
            return server.getEvents();
        }, 
        new JsonTransformer());
        
        /// POST /event/new
        /// Create new event from JSON ///
        /// Attributes: name, ...
        post("/event/new",
        (request, response) ->
        {
            // parse JSON data
            JsonElement req = new JsonParser().parse(request.body());
            JsonObject  obj = req.getAsJsonObject();
            
            System.out.println(obj.toString());
            
            server.addEvent(obj);
            
            response.type("application/json");
            return obj.toString();
        }, 
        new JsonTransformer());
        
        /// GET /event/id
        /// Returns Event (including videos) as JSON ///
        get("/event/:id",
        (request, response) ->
        {
            // parse request
            String sid = request.params("id");
            long id = Long.parseLong(sid);
            
            response.type("application/json");
            return server.eventById(id);
        }, 
        new JsonTransformer());
        
        /// POST /event/event_id (JSON)
        /// Upload JSON metadata about a video for Event @id ///
        post("/event/:id",
        (Request request, Response response) ->
        {
            // parse request
            long id = Long.parseLong(request.params("id"));
            
            String e = server.eventById(id);
            if (e.length() != 0)
            {
                // parse JSON data
                // TODO: move me to Event
                JsonElement req = new JsonParser().parse(request.body());
                JsonObject  obj = req.getAsJsonObject();
                
                // add video to database (and get id)
                int video_id = server.addEventVideo(id, obj);
                
                // get client from session
                int client_id = getSessionID(request);
                Client c = server.getClient(client_id);
                
                // add video to clients list of candidates for upload
                c.addVideo(video_id);
                
                // respond with request for successful call
                return obj.toString();
            }
            
            response.status(404);
            return "No such event";
        }, 
        new JsonTransformer());
        
        /**
         * PUT /video/video_id
         * Upload @video_id candidate
        **/
        put("/video/:video_id",
        (Request request, Response response) ->
        {
            int video_id = Integer.parseInt(request.params("video_id"));
            
            /**
             * Check if video exists for client, or if we 
             * have already received this video
            **/
            int client_id = getSessionID(request);
            Client c = server.getClient(client_id);
            
            if (c.hasVideo(video_id) == false)
            {
                return "Video " + video_id + " has not been registered yet";
            }
            if (c.getVideo(video_id).isReceived())
            {
                return "Video " + video_id + " has already been received";
            }
            
            /**
             * Receive video from client
            **/
            String filename = request.params("id") + "-" + request.params("video");
            File file = new File("upload/" + filename);
            
            if (file.exists())
            {
                return "File already exists";
            }
            if (!file.mkdirs())
            {
                return "File path failure";
            }
            
            //file.createNewFile();
            System.out.println("Downloading: " + filename);

            try (FileOutputStream fw = new FileOutputStream(file.getAbsoluteFile()))
            {
                InputStream content = request.raw().getInputStream();

                int len;
                len = copyInputStream(content, fw);

                System.out.println("Received " + len + " bytes from file: " + filename);
                
                /**
                 * Register that we received video from client
                **/
                Video video = c.getVideo(video_id);
                
                System.out.println("Received video " + video_id);
                
                video.received();
                
                return "Upload successful";
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return "Some Ting Wong (IOException)";
        });
        
        /// GET /video/video_id
        /// Retrieve video (@video) from Event @id
        get("/video/:video",
        (request, response) ->
        {
            // parse request
            String svid = request.params("video");
            int vid = Integer.parseInt(svid);
            
            String v = server.videoById(vid);
            if (v != null)
            {
                return v;
            }
            
            response.status(404);
            return "No such video: " + svid;
            /*
            try
            {
                OutputStream output = response.raw().getOutputStream();
                
                File file = new File("output.pdf");
                
                FileInputStream fi = new FileInputStream(file.getAbsoluteFile());
                int len = copyInputStream(fi, output);
                fi.close();
                
                return "Sent " + len + " bytes\n";
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return "Something went wrong";*/
        });
        
        /// GET /selected
        /// Retrieve list of selected (video) candidates
        get("/selected", 
        (Request request, Response response) ->
        {
            int client_id = getSessionID(request);
            // TODO here a list of selected but not yet uploaded videos
            Client c = server.getClient(client_id);
            
            if (c.hasVideos() == false)
            {
                return "You have no video candidates to upload";
            }
            
            ArrayList<Video> candidates = 
                    server.calculateCandidates(c.getVideos());
            
        	// should be returned as a JSON string. 
            return new Gson().toJson(candidates);
        });
        
    }
}
