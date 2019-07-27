package com.recordrack.db;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import com.recordrack.logic.DateUtil;
import com.recordrack.logic.Properties;
import com.recordrack.ui.RecordRackAlert;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;

public class DatabaseManager
{
    private static Connection con;
    private static DatabaseManager dbManager;
    private void init()
    {
        try
        {
            //System.out.println("DB name");
            //dbManager = new DatabaseManager();
            //System.out.println(Properties.getDbName());
            //Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/"+ Properties.getDbName()+"?" +
                    "user=root&password=hello&useLegacyDatetimeCode=false&serverTimezone=Africa/Lagos";
            con= DriverManager.getConnection
                    (url);
        }
        /*catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            RecordRackAlert.showInformationAlert(e.getMessage(),e.getCause().toString());
        }*/
        catch (SQLException e)
        {
            e.printStackTrace();
            RecordRackAlert.showInformationAlert(e.getMessage(),e.getCause().toString());
        }
    }

    public static JSONArray fetchData(String query)
    {
        //System.out.println(con);
        if(dbManager == null)
        {
            dbManager = new DatabaseManager();
            dbManager.init();
        }


        int columnCount = 0;
        JSONArray array = new JSONArray();
        JSONObject object = null;
        ResultSet result = null;
        String val = null;
        try
        {
            //object = new JSONObject();
            result = con.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY).executeQuery(query);
            columnCount = result.getMetaData().getColumnCount();
            while (result.next())
            {
                object = new JSONObject();
                for(int i=1;i<=columnCount;i++)
                {
                    val = result.getString(i);
                    object.put(result.getMetaData().getColumnLabel(i),val==null?"":val);
                }
                array.put(object);
            }
            result.close();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            RecordRackAlert.showInformationAlert(e.getMessage(),e.getCause().toString());
        }


        /*String columns[] = object.has("columns")?(String[])object.get("columns"):null;
        String whereCondition = object.getString("where");
        String extra = object.has("extra")?object.getString("extra"):"";
        String table = object.getString("table");
        String val = null;
        ResultSet result = null;
        JSONArray array = new JSONArray();
        int columnCount = 0;

        StringBuilder builder = new StringBuilder();


        builder.append("SELECT ");
        if(columns == null)
            builder.append("* ");
        else
        {
            int pos = 0;
            for (String col:columns)
            {
                if(pos++>0)
                    builder.append(", ");
                builder.append(col);
            }

        }

        builder.append(" FROM "+table+" "+extra+" "+whereCondition);
        try
        {
            //object = new JSONObject();
            result = con.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY).executeQuery(builder.toString());
            columnCount = columns==null?result.getMetaData().getColumnCount():columns.length;
            while (result.next())
            {
                object = new JSONObject();
                for(int i=1;i<=columnCount;i++)
                {
                    val = result.getString(i);
                    object.put(result.getMetaData().getColumnName(i),val==null?"":val);
                }
                array.put(object);
            }
            result.close();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            RecordRackAlert.showInformationAlert(e.getMessage(),e.getCause().toString());
        }*/
        return array;

    }

    public static boolean setCostPrice(String sql, CostPriceRowItem... costPriceRowItems)
    {
        boolean result= false;
        PreparedStatement statement = null;
        if (costPriceRowItems == null)
            return false;
        try
        {
            con.setAutoCommit(false);
            statement = con.prepareStatement(sql);
            for (CostPriceRowItem costPriceRowItem : costPriceRowItems)
            {
                //define category
                statement.setInt(1,Integer.parseInt(costPriceRowItem.getCategoryID()));
                //define item id
                statement.setInt(2,Integer.parseInt(costPriceRowItem.getItemID()));
                //define cost
                statement.setDouble(3,costPriceRowItem.getCostValue());
                //define created_ts
                statement.setTimestamp(4, DateUtil.getCurrentTimeStamp());
                //define modified time
                statement.setTimestamp(5,null);
                //set cost value if duplicate exists
                statement.setDouble(7,costPriceRowItem.getCostValue());
                //set modified time if it's an update
                statement.setTimestamp(6,DateUtil.getCurrentTimeStamp());
                System.out.println(statement.toString());
                statement.executeUpdate();
            }
            con.commit();
            result = true;

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            rollbackOnException();
            result = false;
        }
        finally
        {
            closeStatement(statement);
        }
        return result;
    }

    public static boolean updateCostPrice(String sql,CostPriceRowItem ...costPriceRowItems)
    {
        boolean result= false;
        PreparedStatement statement = null;
        if (costPriceRowItems == null)
            return false;
        try
        {
            con.setAutoCommit(false);
            statement = con.prepareStatement(sql);
            for (CostPriceRowItem costPriceRowItem : costPriceRowItems)
            {
                //define cost
                System.out.println(DatabaseManager.class+ " updating cost "+costPriceRowItem.getCostValue());
                statement.setDouble(1,costPriceRowItem.getCostValue());
                //define item id
                statement.setInt(2,Integer.parseInt(costPriceRowItem.getItemID()));
                statement.executeUpdate();
            }
            con.commit();
            result = true;

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            rollbackOnException();
            result = false;
        }
        finally
        {
            closeStatement(statement);
        }
        return result;

    }

    public static boolean createCostPriceTable(String sql)
    {
        Statement statement = null;
        boolean result = false;
        try
        {
            con.setAutoCommit(false);
            statement = con.createStatement();
            statement.execute(sql);
            con.commit();
            result = true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            rollbackOnException();
        }
        finally {
            closeStatement(statement);
        }
        return result;
    }

    private static void rollbackOnException()
    {
        try
        {
            con.rollback();
        }
        catch (SQLException e)
        {
            System.out.println("Exception occurred. Transaction was rolled back");
        }
    }

    private static void closeStatement(Statement statement)
    {
        try
        {
            statement.close();
        }
        catch (SQLException e)
        {
            System.out.println("Exception occurred while closing statement");
            e.printStackTrace();
        }
    }

    public static boolean executeQuery(String sql)
    {
        boolean result = false;
        Statement statement = null;
        try
        {
            statement = con.createStatement();
            statement.execute(sql);
            result = true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        closeStatement(statement);
        return result;
    }
}
