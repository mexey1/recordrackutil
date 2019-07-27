package com.recordrack.db;

import org.json.JSONException;
import org.json.JSONObject;

public class IncomeRowItem
{
    private String name;
    private String purpose;
    private String amountPaid;

    public IncomeRowItem(JSONObject object)
    {
        try
        {
            name = object.getString("name");
            purpose = object.getString("purpose");
            amountPaid = object.getString("amount_paid");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public String getName()
    {
        return name;
    }

    public String getPurpose()
    {
        return purpose;
    }

    public String getAmountPaid()
    {
        return amountPaid;
    }
}
