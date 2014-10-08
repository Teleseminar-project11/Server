package io.distributed.videodirector;

import com.google.gson.*;
import java.io.*;
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
    
    
    private static int copyInputStream(
            InputStream  in, 
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
        }
        return total;
    }
    
    public static void main(String[] args)
    {
        server.addEvent(new Event("The International 2014"));
        setPort(1235);
        
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
            
            String name = obj.get("name").getAsString();
            
            Event event = new Event(name);
            server.addEvent(event);
            
            response.type("application/json");
            return event;
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
            String sid = request.params("id");
            long id = Long.parseLong(sid);
            
            Event e = server.eventById(id);
            if (e != null)
            {
                // parse JSON data
                // TODO: move me to Event
                JsonElement req = new JsonParser().parse(request.body());
                JsonObject  obj = req.getAsJsonObject();
                
                String name = obj.get("name").getAsString();
                Video v = new Video(name);
                
                e.addVideo(v);
                return v;
            }
            
            response.status(404);
            return "No such event";
        }, 
        new JsonTransformer());
        
        /// GET /event/id/video_id
        /// Retrieve video (@video) from Event @id
        get("/event/:id/:video",
        (request, response) ->
        {
            // parse request
            String sid = request.params("id");
            long id = Long.parseLong(sid);
            
            Event e = server.eventById(id);
            if (e != null)
            {
                String svid = request.params("video");
                long vid = Long.parseLong(svid);
                
                Video v = e.videoById(vid);
                if (v != null)
                {
                    return v;
                }
                
                response.status(404);
                return "No such video for event " + e.getId();
            }
            response.status(404);
            return "No such event";
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
        
    }
}
