package com.recordrack.db;

import com.recordrack.interfaces.RowItem;
import javafx.beans.property.StringProperty;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfitLossRowItem implements RowItem
{
    private String storedUnit;
    private String item;
    private String quantity;
    private String itemID;
    private String baseUnitID;
    private String baseUnitEq;
    private String category;
    private String unitID;
    private String unit;
    private StringProperty unitCost;
    private String totalCost;
    private String revenue;
    private String profit;
    private String categoryID;

    public ProfitLossRowItem(JSONObject object)
    {
       try
       {
           quantity = object.getString("quantity");
           item = object.getString("item");
           category = object.getString("category");
           revenue = object.getString("revenue");
           totalCost = object.getString("total_cost");
           unit = object.getString("unit");
           profit = Double.toString(Double.parseDouble(revenue)-Double.parseDouble(totalCost));
       }
       catch (JSONException e)
       {
           e.printStackTrace();
       }
    }

    @Override
    public void setQuantity(String quantity)
    {
        this.quantity = quantity;
    }

    @Override
    public void setNewQuantity(String quantity)
    {
        this.quantity = quantity;
    }

    public void setItem(String item)
    {
        this.item = item;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public void setRevenue(String revenue)
    {
        this.revenue= revenue;
    }

    public void setTotalCost(String totalCost)
    {
        this.totalCost = totalCost;
    }

    public void setProfit(String profit)
    {
        this.profit = profit;
    }

    public String getUnit()
    {
        return unit;
    }
    public String getQuantity()
    {
        return quantity;
    }

    public String getItem()
    {
        return item;
    }

    public String getCategory()
    {
        return category;
    }

    public String getRevenue()
    {
        return revenue;
    }

    public String getTotalCost()
    {
        return totalCost;
    }

    public String getProfit()
    {
        return profit;
    }
}
