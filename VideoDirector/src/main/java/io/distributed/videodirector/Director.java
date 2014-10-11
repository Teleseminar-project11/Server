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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gonzo
 */
public class Director
{
    static VideoStorageQueries database;
    
    // (1): database
    // create database videodirector;
    
    // (2): user
    // CREATE USER 'uname'@'localhost' IDENTIFIED BY 'passw';
    // GRANT ALL PRIVILEGES ON videodirector.* TO 'uname'@'localhost';
    
    // (3): tables
    // create table Event (id int not null auto_increment, name varchar(255) not null, primary key(id));
    
    
    public Director()
    {
        try
        {
            database = new VideoStorageQueries();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(Director.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    protected static VideoStorageQueries getDatabase()
    {
        return database;
    }
    
    void addEventVideo(long event_id, JsonObject obj)
    {
        database.addEventVideo(event_id, obj);
        database.saveNewVideo(obj);
    }
    
}
