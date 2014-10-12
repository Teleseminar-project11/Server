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
    
    // (1): database
    // create database videodirector;
    
    // (2): user
    // CREATE USER 'uname'@'localhost' IDENTIFIED BY 'passw';
    // GRANT ALL PRIVILEGES ON videodirector.* TO 'uname'@'localhost';
    
    // (3): tables
    // create table event (id int not null auto_increment, name varchar(255) not null, primary key(id));
    // create table video (id int not null auto_increment, name varchar(255) not null, primary key(id));
    // create table event_videos (event_id int not null, video_id int not null, primary key(event_id, video_id));
    
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
    
    public String eventById(long id)
    {
        return database.getEvent(id);
    }
    
    public String getEvents()
    {
        return database.getEvents();
    }
    
    /**
     *
     * @param obj Event to be created as JSON object
     */
    public void addEvent(JsonObject obj)
    {
        database.addEvent(obj);
    }
    
    /**
     *
     * @return Returns an open database wrapper instance
     */
    protected static DatabaseHandler getDatabase()
    {
        return database;
    }
    
    int addEventVideo(long event_id, JsonObject obj)
    {
        int video_id = database.saveVideo(obj);
        database.addEventVideo(event_id, video_id);
        
        return video_id;
    }

    String videoById(int video_id)
    {
        return database.getVideo(video_id);
    }
    
}
