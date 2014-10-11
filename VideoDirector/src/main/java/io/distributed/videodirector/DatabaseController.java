package io.distributed.videodirector;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class DatabaseController
{
    //class that directs the messages from the server to the model

    VideoStorageQueries v;

    DatabaseController() {

            v = new VideoStorageQueries();
    }

    /*
     * Primary function for dealing with requests on the server. 
     * 
     * 
     * PARAM: int request: This is a int value for identifying what you want to do on the server.
     * 		  The values are as follows: 1 means you want to add a new video tuple
     * 									 2 means you want to update a existing video tuple
     * 									 9 means you want to get all items from the database
     * PARAM: 
     * RETURN: This function returns a JSONObject or a string on json format.
     */
    public void request_handler(int request, String msg)
    {
        //if tests for what kind of database query we need to handle.
        switch(request)
        {
        //1 == Insert video query. Here the string have to be a json type string.
        case 1: JSONObject j = handleJsonStringToObject(msg);
                v.saveNewVideo(j);
                break;
        //return j;
        //2 == Update video query.
        case 2:
                break;
        //3 == Delete video query.
        case 3:
                break;
        //4 == Insert client query.
        case 4:
                break;
        //5 == Update client query.
        case 5:
                break;
        //6 == Delete client query.
        case 6:
                break;
        //7 == Add event
        case 7:
                break;
        //8 == 
        case 8:
                break;
        //9 == GetAllData. Here the string doesn't have to be anything.
        case 9: v.getAllData();
        }
    }

    public JsonObject handleJsonStringToObject(String s)
    {
        System.out.println(s);

        try
        {
            return new JsonParser().parse(s).getAsJsonObject();
        }
        catch(JsonParseException e)
        {
            System.out.println("Error occured in handleJsonStringToObject: " + e);
        }

        return null;
    }
}
