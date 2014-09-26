package io.distributed.videodirector;

import static spark.Spark.*;
import spark.*;

/**
 *
 * @author gonzo
 */
public class Server
{
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
