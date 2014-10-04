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
        	System.out.println("New POST received:");
        	System.out.println(request.body());
            return request.body();
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
        
        put("/register",
        (Request request, Response response) ->
        {
            try
            {
                File file = new File("output.pdf");
                
                new MultipartRequest(request.raw(), file.getAbsolutePath());
                halt(200);
            }
            catch (ServletException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return "Something went wrong";
        });
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
        
        get("/", 
        (request, response) ->
        {
            response.redirect("/news/world");
            return null;
        });
        
    }
}
