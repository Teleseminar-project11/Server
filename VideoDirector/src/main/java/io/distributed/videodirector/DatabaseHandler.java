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
import java.util.logging.Level;
import java.util.logging.Logger;

//class for setting up and running SQL queries. Accessed through the class DatabaseController 
//
public class DatabaseHandler
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
    
    public DatabaseHandler() throws Exception
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
    
    private int executeInsertQuery(String query)
    {
        Statement stm = null;
        try
        {
            stm = connection.createStatement();
            // resultSet gets the result of the SQL query
            stm.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next())
            {
                return rs.getInt(1);
            }
            return -1;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (stm != null) stm.close();
            } catch (SQLException ex)
            {
                ex.printStackTrace();
                Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return -1;
    }
    
    private ResultSet executeSelectQuery(Statement stm, String query)
            throws SQLException
    {
        return stm.executeQuery(query);
    }
    
    private String getSelectQueryAsJsonString(String query)
    {
        Statement stm = null;
        String result = null;
        try
        {
            stm = connection.createStatement();
            ResultSet set = executeSelectQuery(stm, query);
            
            /*if (set == null)
            {
                return "{\"error\": \"" + table + " empty result set\"}";
            }*/
            result = getJsonFromResultSet(set).toString();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (stm != null) stm.close();
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }
        }
        return result;
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
                columnNames[i] = metaData.getColumnName(i+1);
            
            //turn answer from query into a string
            while (resultSet.next())
            {
                JsonObject current = new JsonObject();
                
                for(int i = 0; i < numColumns; i++)
                {
                    String columnType = metaData.getColumnTypeName(i+1);
                    //System.out.println(columnNames[i] + " " + columnType);
                    // add the column value to json as new property
                    switch (columnType)
                    {
                    case "VARCHAR":
                        current.addProperty(
                                columnNames[i], resultSet.getString(i+1));
                        break;
                    case "INT":
                        current.addProperty(
                                columnNames[i], resultSet.getInt(i+1));
                        break;
                    case "TIME":
                        current.addProperty(
                                columnNames[i], resultSet.getTime(i+1).toString());
                        break;
                    case "DATE":
                        current.addProperty(
                                columnNames[i], resultSet.getDate(i+1).toString());
                        break;
                    default:
                        System.out.println("UNUSED COLUMN: " + columnNames[i] + " " + columnType);
                    }
                }
                json.add(resultSet.getString(1), current);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error occured in getJsonFromResultSet: " + e);
            e.printStackTrace();
        }
        System.out.println(json.toString());
        return json;
    }
    
    public String getEvents()
    {
        String query = "SELECT * FROM event";
        return getSelectQueryAsJsonString(query);
    }
    public String getEvent(long event)
    {
        String query = "SELECT * FROM event_videos WHERE event_id=" + event;
        return getSelectQueryAsJsonString(query);
    }
    public void addEvent(JsonObject data)
    {
        String query = createInsertQuery("event", data);
        System.out.println(query);
        executeInsertQuery(query);
    }
    
    public String getVideo(int video_id)
    {
        String query = "SELECT * FROM video WHERE id=" + video_id;
        return getSelectQueryAsJsonString(query);
    }
    
    /**
     *
     * @param data Video metadata as JSON object
     */
    public int saveVideo(JsonObject data)
    {
        String query = createInsertQuery("video", data);
        System.out.println(query);
        return executeInsertQuery(query);
    }
    
    public long addEventVideo(long event_id, long video_id)
    {
        JsonObject json = new JsonObject();
        json.addProperty("event_id", event_id);
        json.addProperty("video_id", video_id);
        
        String query = createInsertQuery("event_videos", json);
        return executeInsertQuery(query);
    }
    
}
