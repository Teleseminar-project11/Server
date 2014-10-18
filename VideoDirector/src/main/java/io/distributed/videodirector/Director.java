/*
 * The MIT License
 *
 * Copyright 2014 gonzo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.distributed.videodirector;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gonzo
 */
public class Director
{
    static DatabaseHandler database;
    
    private ArrayList<Client> clients;
    
    public Director()
    {
        try
        {
            database = new DatabaseHandler();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(Director.class.getName()).log(Level.SEVERE, null, ex);
        }
        clients = new ArrayList<>();
    }
    
    /**
     *
     * @return Returns an open database wrapper instance
     */
    protected static DatabaseHandler getDatabase()
    {
        return database;
    }
    
    public Client getClient(int id)
    {
        for (Client c : clients)
        {
            if (c.getSessionId() == id) return c;
        }
        Client c = new Client(id);
        clients.add(c);
        return c;
    }
    
    public JsonObject eventById(int id)
    {
        return database.getEvent(id);
    }
    public JsonObject getEventAndVideos(int id)
    {
        return database.getEventVideos(id);
    }
    
    public JsonObject getEvents()
    {
        return database.getEvents();
    }
    
    /**
     *
     * @param obj Event (as JSON) to be added to database
     * @return Returns event (insertion) id
     */
    public int addEvent(JsonObject obj)
    {
        return database.addEvent(obj);
    }
    
    public int addEventVideo(long event_id, JsonObject obj)
    {
        int video_id = database.saveVideo(obj);
        database.addEventVideo(event_id, video_id);
        
        return video_id;
    }

    public JsonObject videoById(int video_id)
    {
        return database.getVideo(video_id);
    }

    public ArrayList<Video> calculateCandidates(Client c)
    {
        HashSet<Integer> events = new HashSet<>();
        
        // for each video...
        c.getVideos().stream().forEach((v) ->
        {
            // add event to events list
            events.add(v.getEventId());
            System.out.println("EVENT: " + v.getEventId());
        });
        
    	ArrayList<Video> res = new ArrayList<>();
        
        // for each event...
        events.forEach((e) ->
        {
            // get top rated video for that event
            ArrayList<Integer> vids
                    = database.getEventTopRatedVideo(e);
            
            if (!vids.isEmpty())
            {
                // the only uploadable video in this case is the top-rated one
                // if client has video that is top-rated -->
                Video v = c.getVideo(vids.get(0));
                if (v != null && !v.isReceived())
                {
                    // add video to list client should upload
                    res.add(v);
                }
            }
        });
        return res;
    }
}
