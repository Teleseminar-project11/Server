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

import com.google.gson.*;
import java.sql.*;
import java.util.ArrayList;

//class for setting up and running SQL queries. Accessed through the class DatabaseController 
//
public class VideoStorageQueries
{
    final Connection connection;
    
    public static Connection getConnection() throws Exception
    {
        String driver = "org.gjt.mm.mysql.Driver";
        String url = "jdbc:mysql://localhost/videodirector";
        String username = "uname";
        String password = "passw";
        
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }
    
    public VideoStorageQueries() throws Exception
    {
        connection = getConnection();
    }
    
    private String createInsertQuery(String tableName, JsonObject data)
    {
        int numItems = data.entrySet().size();
        
        ArrayList<String> keys = new ArrayList<>();
        data.entrySet().stream().forEach((entry) ->
        {
            keys.add(entry.getKey());
        });
        
        String query = "INSERT INTO " + tableName + " (";

        for(int i = 0; i < numItems; i++)
        {
            query += keys.get(i);
            if(i != numItems - 1)
            {
                query += ", ";
            }
        }
        query += ") VALUES (";

        for(int i = 0; i < numItems; i++)
        {
            try
            {
                query += data.get(keys.get(i));
                if (i != numItems - 1) {
                    query += ", ";
                }
            }
            catch(JsonParseException e)
            {
                e.printStackTrace();
            }
        }
        query += ")";
        System.out.println("query: " + query);
        return query;
    }
    
    private void executeInsertQuery(String query)
    {
        try
        {
            Statement statement = connection.createStatement();
            // resultSet gets the result of the SQL query
            statement.executeUpdate(query);
        }
        catch(Exception e)
        {
            System.out.println("Error occured in saveNewVideo: " + e);
            e.printStackTrace();
        }
    }
    
    private ResultSet executeSelectQuery(String query)
    {
        ResultSet rs = null;
        try
        {
            Statement statement = connection.createStatement();
            rs = statement.executeQuery(query);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return rs;
    }
    
    private String getSelectQueryAsJsonString(String query)
    {
        ResultSet set = executeSelectQuery(query);
        return getJsonFromResultSet(set).getAsString();
    }

    private JsonObject getJsonFromResultSet(ResultSet resultSet)
    {
        JsonObject json = new JsonObject();
        try
        {
            //find the column name
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numColumns = metaData.getColumnCount();
            String[] columnNames = new String[numColumns];

            for(int i = 0; i < numColumns; i++)
            {
                columnNames[i] = metaData.getColumnName(i+1);
            }

            //turn answer from query into a string
            while(resultSet.next())
            {
                for(int columnIndex = 0; columnIndex < numColumns; columnIndex++)
                {
                    if(columnIndex == numColumns)
                    {
                        columnIndex = 0;
                    }
                    
                    String columnType = metaData.getColumnTypeName(columnIndex+1);
                    System.out.println(columnNames[columnIndex] + " " + columnType);

                    // add the column value to json as new property
                    if(columnType.equals("String"))
                    {
                        json.addProperty(columnNames[columnIndex], resultSet.getString(columnIndex+1));
                    }
                    else if(columnType.equals("INT"))
                    {
                        json.addProperty(columnNames[columnIndex], resultSet.getInt(columnIndex+1));
                    }
                    else if(columnType.equals("TIME"))
                    {
                        json.addProperty(columnNames[columnIndex], resultSet.getTime(columnIndex+1).toString());
                    }
                    else if(columnType.equals("DATE"))
                    {
                        json.addProperty(columnNames[columnIndex], resultSet.getDate(columnIndex+1).toString());
                    }
                }
                //add code to seperate tuples
            }
        }
        catch(Exception e)
        {
            System.out.println("Error occured in getJsonFromResultSet: " + e);
            e.printStackTrace();
        }
        return json;
    }
    
    public JsonObject getAllData()
    {
        int numItems = 0;
        String answer = "{";
        JsonObject json = new JsonObject();
        try
        {
            // setup the connection with the DB.
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT count(entry_id) from VideoStorage");

            if (resultSet.next())
            {
                numItems = resultSet.getInt(1);
            }

            // statements allow to issue SQL queries to the database
            statement = connection.createStatement();
            // resultSet gets the result of the SQL query
            resultSet = statement.executeQuery("select * from VideoStorage");

            json = getJsonFromResultSet(resultSet);
        }
        catch (Exception e)
        {
            System.out.println("Error occured in getAllData: " + e);
            e.printStackTrace();
        }
        return json;
    }
    
    public String  getEvents()
    {
        String query = "select * from event";
        return getSelectQueryAsJsonString(query);
    }
    public String getEvent(long event)
    {
        String query = "select * from event where event_id = " + event;
        return getSelectQueryAsJsonString(query);
    }
    public void addEvent(JsonObject data)
    {
        String query = createInsertQuery("event", data);
        System.out.println(query);
        executeInsertQuery(query);
    }
    
    public String getVideo(int video)
    {
        String query = "select * from videoStorage where video_id = " + video;
        return getSelectQueryAsJsonString(query);
    }
    
    public String getVideoMetadata(int video)
    {
        String query = "select ... from videoStorage where video_id = " + video;
        return getSelectQueryAsJsonString(query);
    }

    public String getEventVideos(int event)
    {
        String query = "select video_id from videoStorage where event_id = " + event;
        return getSelectQueryAsJsonString(query);
    }

    void addEventVideo(long event_id, JsonObject obj)
    {
        
    }
    
    /**
     *
     * @param data Video metadata as JSON object
     */
    public void saveNewVideo(JsonObject data)
    {
        String query = createInsertQuery("video", data);
        System.out.println(query);
        executeInsertQuery(query);
    }
}
