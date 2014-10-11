package io.distributed.videodirector;

import com.google.gson.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

//class for setting up and running SQL queries. Accessed through the class DatabaseController 
//
public class VideoStorageQueries
{
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;


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
    
    private String createInsertQuery(String tableName, JsonObject data)
    {
        int numItems = data.entrySet().size();
        
        ArrayList<String> keys = new ArrayList<>();
        for(Map.Entry<String, JsonElement> entry : data.entrySet())
        {
            keys.add(entry.getKey());
        }
        
        String query = "INSERT INTO " + tableName + " (";
        
        System.out.println(keys.get(2));

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
                
            }
        }
        query += ")";
        return query;
    }
    
    private void executeInsertQuery(String query)
    {
        try
        {
            connect = getConnection();
            // statements allow to issue SQL queries to the database
            statement = connect.createStatement();
            // resultSet gets the result of the SQL query
            statement.executeUpdate(query);
        }
        catch(Exception e)
        {
            System.out.println("Error occured in saveNewVideo: " + e);
        }
    }
    
    private ResultSet executeSelectQuery(String query)
    {
        ResultSet rs = null;
        try
        {
            // setup the connection with the DB.
            connect = getConnection();
            statement = connect.createStatement();
            rs = statement.executeQuery(query);
        }
        catch(Exception e)
        {
            
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

            for(int i = 0; i < numColumns; i++) {
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
        }catch(Exception e) {
                System.out.println("Error occured in getJsonFromResultSet: " + e);
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
            connect = getConnection();
            statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT count(entry_id) from VideoStorage");

            if (resultSet.next())
            {
                numItems = resultSet.getInt(1);
            }

            // statements allow to issue SQL queries to the database
            statement = connect.createStatement();
            // resultSet gets the result of the SQL query
            resultSet = statement.executeQuery("select * from VideoStorage");

            json = getJsonFromResultSet(resultSet);
        }
        catch (Exception e)
        {
            System.out.println("Error occured in getAllData: " + e);
        }
        return json;
    }
    
    //this method will get a json object
    //it will use the data in the object to store a new tuple in the database
    public void saveNewVideo(JsonObject data)
    {
        String query = createInsertQuery("videoStorage", data);
        System.out.println(query);
        executeInsertQuery(query);
    }

    public void addClient(JsonObject data)
    {
        String query = createInsertQuery("client", data);
        System.out.println(query);
        executeInsertQuery(query);
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
    
    public String getEvent(int event)
    {
        String query = "select * from event where event_id = " + event;
        return getSelectQueryAsJsonString(query);
    }
}
