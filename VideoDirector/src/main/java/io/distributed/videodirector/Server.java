package io.distributed.videodirector;

import com.google.gson.*;
import java.io.*;
import static spark.Spark.*;

/**
 * @author gonzo
 * 
 * 
**/
public class Server
{
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
        
        get("/conf", 
        (request, response) ->
        {
            if (request.ip().equals("127.0.0.1"))
            {
                return "Configuration page";
            }
            response.status(401);
            return "Access denied";
        });
        
        post("/hello", 
        (request, response) ->
        {
            return "Hello World: " + request.body();
        });
        
        post("/login",
        (request, response) ->
        {
            // parse request
            JsonElement req = new JsonParser().parse(request.body());
            JsonObject  obj = req.getAsJsonObject();
            
            String user = obj.get("username").getAsString();
            String pass = obj.get("password").getAsString();
            
            response.type("application/json");
            return new Credentials(user, pass);
        }, 
        new JsonTransformer());
        
        post("/upload",
        (request, response) ->
        {
            try
            {
                InputStream content = request.raw().getInputStream();
                
                File file = new File("output.pdf");
                
                // if file doesnt exists, then create it
                //if (!file.exists())
                //{
                    file.createNewFile();
                //}
                
                FileOutputStream fw = new FileOutputStream(file.getAbsoluteFile());
                int len = copyInputStream(content, fw);
                fw.close();
                
                return "Received: " + len + " bytes\n";
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
        
        get("/", 
        (request, response) ->
        {
            response.redirect("/news/world");
            return null;
        });
        
    }
}
