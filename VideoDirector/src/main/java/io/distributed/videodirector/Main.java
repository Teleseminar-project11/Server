package io.distributed.videodirector;

import com.google.gson.*;
import java.io.*;
import javax.servlet.ServletException;
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
    static Server server = new Server();
    
    
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
        setPort(1234);
        
        post("/hello", 
        (request, response) ->
        {
        	System.out.println("New POST received:");
        	System.out.println(request.body());
            return request.body();
        });
        
        post("/event/new",
        (request, response) ->
        {
            // parse request
            JsonElement req = new JsonParser().parse(request.body());
            JsonObject  obj = req.getAsJsonObject();
            
            String name = obj.get("name").getAsString();
            
            Event event = new Event(name);
            server.addEvent(event);
            
            response.type("application/json");
            return event;
        }, 
        new JsonTransformer());
        
        get("/events",
        (request, response) ->
        {
            response.type("application/json");
            return server.getEvents();
        }, 
        new JsonTransformer());
        
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
        
        put("/upload",
        (Request request, Response response) ->
        {
            File file = new File("output.pdf");
            //file.createNewFile();
            
            try (FileOutputStream fw = new FileOutputStream(file.getAbsoluteFile()))
            {
                InputStream content = request.raw().getInputStream();
                
                int len;
                len = copyInputStream(content, fw);
                
                return "Received " + len + " bytes from file: " + 
                        request.params("file") + "\n";
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return "Something went wrong";
        });
        get("/download",
        (request, response) ->
        {
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
            return "Something went wrong";
        });
        
        get("/private", 
        (request, response) ->
        {
            response.status(401);
            return "Go Away!!!";
        });
        
        get("/users/:name", 
        (request, response) ->
        {
            return "Selected user: " + request.params(":name");
        });
        
        get("/news/:section", 
        (request, response) ->
        {
            response.type("text/html");
            return "<!doctype html><news>" + request.params("section") + "</news>";
        });
        
        get("/protected", 
        (request, response) ->
        {
            halt(403, "I don't think so!!!");
            return null;
        });
        
        post("/multipart",
        (Request request, Response response) ->
        {
            try
            {
                final File dir = new File("upload");
                
                if (!dir.exists() && !dir.mkdirs())
                {
                    halt(500);
                    return "Some Ting Wong";
                }
                
                new MultipartRequest(request.raw(), dir.getAbsolutePath());
                halt(200);
            }
            catch (ServletException | IOException e)
            {
                e.printStackTrace();
            }
            return "Something went wrong";
        });
        
        get("/", 
        (request, response) ->
        {
            response.redirect("/news/world");
            return null;
        });
        
    }
}
