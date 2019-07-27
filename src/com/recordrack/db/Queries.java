package com.recordrack.db;

import com.recordrack.interfaces.QueryResultListener;
import com.recordrack.logic.Properties;
import com.recordrack.ui.RecordRackAlert;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class contains methods for interar=cting with the database. Internally, all method calls to the DatabaseManager
 * class are on the database thread
 */
public class Queries
{
    /**
     * method to retrieve current quantities from the backend tables
     * @param listener the listener where the result would be sent to
     */
    public void getCurrentQuantityForItem(String item,QueryResultListener listener)
    {
        DatabaseThread.getInstance().postTask(new Runnable()
        {
            @Override
            public void run()
            {
                JSONArray result = null;
                try
                {
                    String like=(item==null||item.isEmpty())?"":" and item like '"+item+"%'";
                    String query = "select a.*,u.unit as base_unit from\n" +
                            "                            (select c.id as category_id,category,item,quantity,unit as stored_unit,i.id as item_id,old_unit_quantity as base_unit_eq,old_unit_id as base_unit_id,unit_id,cp.cost\n" +
                            "                            from current_quantity cq\n" +
                            "                            inner join item i on cq.item_id=i.id\n" +
                            "                            inner join unit u on u.id=cq.unit_id\n" +
                            "                            inner join category c on c.id =i.category_id\n" +
                            "                            ##where quantity>0\n" +
                            "                            left join unit_relation ur on cq.unit_id=new_unit_id\n" +"" +
                            "                            left join cost_price cp on cp.item_id=i.id\n"+
                            "                            where i.archived=0"+like+") as a\n" +
                            "                            left join unit u on a.base_unit_id=u.id\n" +
                            "                            order by category asc, item asc;";
                    System.out.println("data fetcheddd");
                    result = DatabaseManager.fetchData(query);
                    System.out.println(result.toString());
                    listener.doAction(result);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    RecordRackAlert.showInformationAlert(e.getMessage(),e.getCause().toString());
                }
            }
        });
    }

    public void getCurrentQuantity(QueryResultListener listener)
    {
        //getCurrentQuantityForItem(null,listener);
        DatabaseThread.getInstance().postTask(new Runnable()
        {
            @Override
            public void run()
            {
                JSONArray result = null;
                try
                {
                    //String like=item==null?"":" and item like '"+item+"%'";
                    String query = "select a.*,u.unit as base_unit from\n" +
                            "                            (select c.id as category_id,category,item,quantity,unit as stored_unit,i.id as item_id,old_unit_quantity as base_unit_eq,old_unit_id as base_unit_id,unit_id\n" +
                            "                            from current_quantity cq\n" +
                            "                            inner join item i on cq.item_id=i.id\n" +
                            "                            inner join unit u on u.id=cq.unit_id\n" +
                            "                            inner join category c on c.id =i.category_id\n" +
                            "                            ##where quantity>0\n" +
                            "                            left join unit_relation ur on cq.unit_id=new_unit_id\n" +
                            "                            where quantity>0 and i.archived=0) as a\n" +
                            "                            left join unit u on a.base_unit_id=u.id\n" +
                            "                            order by category asc, item asc;";
                    System.out.println("data fetcheddd");
                    result = DatabaseManager.fetchData(query);
                    System.out.println(result.toString());
                    listener.doAction(result);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    RecordRackAlert.showInformationAlert(e.getMessage(),e.getCause().toString());
                }
            }
        });
    }

    /**
     * method to check if a table exists
     * @param listener the listener to which the result of the query is communicated to
     * @param table the table to check for
     */
    public void checkIfTableExists(String table,QueryResultListener listener)
    {
        System.out.println("Checking if cost price table exists");
        DatabaseThread.getInstance().postTask(new Runnable() {
            @Override
            public void run()
            {
                String query="SELECT COUNT(*) as count_ FROM information_schema.tables where table_name='"+table+"' " +
                        "and table_schema='"+ Properties.getDbName()+"'";
                JSONArray array = DatabaseManager.fetchData(query);
                System.out.println("Array length "+array.length()+" "+array.toString());
                listener.doAction(array.getJSONObject(0).getInt("count_"));
            }
        });
    }

    /**
     * method called to create the cost price table
     * @param listener the query listener object to report the result to
     */
    public void createCostPriceTable(QueryResultListener listener)
    {
        DatabaseThread.getInstance().postTask(new Runnable() {
            @Override
            public void run()
            {
                String sql = "CREATE TABLE IF NOT EXISTS cost_price(category_id INTEGER, " +
                        "item_id INTEGER, cost decimal(19,2),created DATETIME NOT NULL,last_edited DATETIME, PRIMARY KEY(item_id))";
                Boolean result = DatabaseManager.createCostPriceTable(sql);
                listener.doAction(result);
            }
        });
    }

    /**
     * method called to insert cost price data to the cost price table
     * @param listener query result listener for returning the result of the query
     * @param costPriceRowItems the cost price row items containing the data to be saved
     */
    public void insertRowItemsToCostPriceTable(QueryResultListener listener, CostPriceRowItem... costPriceRowItems)
    {
        DatabaseThread.getInstance().postTask(new Runnable()
        {
            @Override
            public void run() {
                String query = "INSERT INTO cost_price VALUES(?,?,?,?,?) ON DUPLICATE KEY UPDATE last_edited=?,cost=?";
                boolean result = DatabaseManager.setCostPrice(query, costPriceRowItems);
                listener.doAction(result);
            }
        });
    }

    /**
     * method called to update cost price data to the cost price table
     * @param listener query result listener for returning the result of the query
     * @param costPriceRowItems the cost price row items containing the data to be saved
     *
    public void updateCostPriceTable(QueryResultListener listener,CostPriceRowItem... costPriceRowItems)
    {
        DatabaseThread.getInstance().postTask(new Runnable()
        {
            @Override
            public void run()
            {
                String query = "UPDATE cost_price SET cost=? WHERE item_id=?";
                boolean result = DatabaseManager.updateCostPrice(query, costPriceRowItems);
                listener.doAction(result);
            }
        });
    }*/

    /**
     * method called to retrieve the cost price and profit data for a given date
     * @param start start date to fetch data from
     * @param end  end date for the query
     * @param listener listener for returning the results
     */
    public void getCostPriceAndProfitForDate(String start,String end,QueryResultListener listener)
    {
        DatabaseThread.getInstance().postTask(new Runnable()
        {
            @Override
            public void run()
            {
                String query="select p.*, p.quantity *(select case when p.item_id=cp.item_id then cost else 0 end) as total_cost,unit\n" +
                        "from(\n" +
                        "select category,item,item_id,SUM(multiplied) as quantity,sum(amount_paid) as revenue\n" +
                        "from\n" +
                        "(\n" +
                        "select category,item,s.item_id,quantity*\n" +
                        "(\n" +
                        "select case \n" +
                        "when unit_id=new_unit_id then old_unit_quantity\n" +
                        "            else 1\n" +
                        "   end \n" +
                        ") as multiplied,amount_paid\n" +
                        "from sales s\n" +
                        "left join unit_relation ur on ur.item_id=s.item_id\n" +
                        "inner join item i on i.id=s.item_id\n" +
                        "inner join category c on c.id=s.category_id\n" +
                        "where s.created between '"+start+"' and '" +end+"' and s.archived=0 \n" +
                        "order by i.id desc\n" +
                        ") as d\n" +
                        "group by item_id,item,category\n" +
                        ") as p\n" +
                        "left join cost_price cp on p.item_id=cp.item_id\n" +
                        "inner join unit u on p.item_id=u.item_id\n" +
                        "where base_unit_equivalent=1\n" +
                        "order by category asc, item asc";
                JSONArray array = DatabaseManager.fetchData(query);
                System.out.println(array);
                listener.doAction(array);
            }
        });
    }

    /**
     * method called to retrieve the income data for a given date
     * @param start start date to fetch data from
     * @param end  end date for the query
     * @param listener listener for returning the results
     */
    public void getIncomeForDate(String start,String end,QueryResultListener listener)
    {
        DatabaseThread.getInstance().postTask(new Runnable()
        {
            @Override
            public void run()
            {
                String query = "select n.name,purpose,amount_paid \n" +
                        "from income i\n" +
                        "inner join name n on i.name_id=n.id\n" +
                        "where i.archived=0 and i.created between '"+start+"' and '"+end+"'";
                JSONArray array = DatabaseManager.fetchData(query);
                listener.doAction(array);
            }
        });
    }

    /**
     * method called to retrieve the expense data for a given date
     * @param start start date to fetch data from
     * @param end  end date for the query
     * @param listener listener for returning the results
     */
    public void getExpenseForDate(String start,String end,QueryResultListener listener)
    {
        DatabaseThread.getInstance().postTask(new Runnable()
        {
            @Override
            public void run()
            {
                String query = "select n.name,purpose,amount_paid \n" +
                        "from expenses i\n" +
                        "inner join name n on i.name_id=n.id\n" +
                        "where i.archived=0 and i.created between '"+start+"' and '"+end+"'";
                JSONArray array = DatabaseManager.fetchData(query);
                listener.doAction(array);
            }
        });
    }

    /**
     * sets default timezone to UTC+1
     */
    public void setTimeZone()
    {
        String query = "set global time_zone='+01:00'";
        DatabaseThread.getInstance().postTask(new Runnable() {
            @Override
            public void run()
            {
                DatabaseManager.executeQuery(query);
            }
        });
    }
}
