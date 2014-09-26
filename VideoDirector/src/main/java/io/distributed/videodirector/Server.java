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
        /*get("/hello",(request, response) ->
         {
         return "Hello World!";
         });*/
        setPort(1234);
        System.out.println("Testing");
        
        get("/hello", (request, response) -> {
            return "Hello World!";
        });
        post("/hello", (request, response) -> {
            return "Hello World: " + request.body();
        });
        get("/private", (request, response) -> {
            response.status(401);
            return "Go Away!!!";
        });
        get("/users/:name", (request, response) -> {
            return "Selected user: " + request.params(":name");
        });
        get("/news/:section", (request, response) -> {
            response.type("text/html");
            return "<!doctype html><news>" + request.params("section") + "</news>";
        });
        get("/protected", (request, response) -> {
            halt(403, "I don't think so!!!");
            return null;
        });
        get("/redirect", (request, response) -> {
            response.redirect("/news/world");
            return null;
        });
        get("/", (request, response) -> {
            return "root";
        });

    }
}
