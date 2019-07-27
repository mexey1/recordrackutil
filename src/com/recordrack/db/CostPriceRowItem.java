package com.recordrack.db;

import com.recordrack.interfaces.RowItem;
import javafx.beans.property.*;
import org.json.JSONException;
import org.json.JSONObject;


public class CostPriceRowItem implements RowItem
{
    private String storedUnit;
    private String item;
    private String quantity;
    private String newQuantity;
    private String itemID;
    private String baseUnitID;
    private String baseUnitEq;
    private String category;
    private String unitID;
    private String baseUnit;
    private String newUnitCost;
    private StringProperty unitCost;
    private StringProperty totalCost;
    private String categoryID;
    private double costValue;
    //private String unitCost="";
    //private String totalCost="";

    public CostPriceRowItem(JSONObject object)
    {
        try
        {
            double q = 0,bue=0;
            storedUnit = object.getString("stored_unit");
            item = object.getString("item");
            itemID = object.getString("item_id");
            baseUnitID = object.getString("base_unit_id");
            baseUnitEq = object.getString("base_unit_eq");
            quantity = object.getString("quantity");
            bue = Double.parseDouble(baseUnitEq.length()==0?"1":baseUnitEq);
            q = Double.parseDouble(quantity.length()==0?"1":quantity);
            quantity = Double.toString((bue*q));
            newQuantity = quantity;
            category = object.getString("category");
            unitID = object.getString("unit_id");
            baseUnit = object.getString("base_unit");
            categoryID = object.getString("category_id");
            if(object.has("cost") && !object.getString("cost").isEmpty())
                unitCost = new SimpleStringProperty(object.getString("cost"));
            else
                unitCost = new SimpleStringProperty("0");
            newUnitCost = ((SimpleStringProperty) unitCost).get();
            costValue = Double.parseDouble(newUnitCost);
            totalCost = computeTotalCost();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public String getNewUnitCost()
    {
        return newUnitCost;
    }

    private SimpleStringProperty computeTotalCost()
    {
        return new SimpleStringProperty(Double.toString(Double.parseDouble(newQuantity)*Double.parseDouble(newUnitCost)));
    }

    private double computeCostValue()
    {
        Double nuc = Double.parseDouble(newUnitCost);
        Double uc = Double.parseDouble(unitCost.get());
        double nq = Double.parseDouble(newQuantity);
        double q = Math.abs(Double.parseDouble(quantity)-nq);
        double c1 = nuc*nq;
        double c2 = uc*q;

        double val = 0;
        if(c2==0)
            val = c1/nq;
        else if((nq+q) ==0)
            val = 0;
        else
            val = (c1+c2)/(nq+q);
        return val;
    }

    public void setNewUnitCost(String newUnitCost)
    {
        /*Double nuc = Double.parseDouble(newUnitCost);
        Double uc = Double.parseDouble(unitCost.get());
        if(uc==0)
            costValue = nuc;
        else
            costValue = (nuc+uc)/2;*/
        this.newUnitCost = newUnitCost;
        costValue = computeCostValue();
        totalCost = computeTotalCost();
    }

    public double getCostValue()
    {
        return costValue;
    }

    public String getCategoryID()
    {
        return categoryID;
    }

    public void setQuantity(String quantity)
    {
        this.quantity = quantity;
    }
    public String getNewQuantity()
    {
        return newQuantity;
    }

    public void setNewQuantity(String newQuantity)
    {
        this.newQuantity = newQuantity;
        costValue = computeCostValue();
        totalCost = computeTotalCost();
    }
    public String getStoredUnit()
    {
        return storedUnit;
    }

    public String getItem()
    {
        return item;
    }

    public String getQuantity()
    {
        return quantity;
    }

    public String getItemID()
    {
        return itemID;
    }

    public String getBaseUnitID()
    {
        return baseUnitID;
    }

    public String getBaseUnitEq()
    {
        return baseUnitEq;
    }

    public String getCategory()
    {
        return category;
    }

    public String getUnitID()
    {
        return unitID;
    }

    public String getBaseUnit()
    {
        if(baseUnit!=null && baseUnit.length()>0)
            return baseUnit;
        return storedUnit;
    }

    public void setUnitCost(String unitCost)
    {
        if(this.unitCost == null)
            this.unitCost = new SimpleStringProperty(unitCost);
        else
            this.unitCost.setValue(unitCost);
        //this.unitCost = unitCost;
    }

    public void setTotalCost(String totalCost)
    {
        if(this.totalCost == null)
            this.totalCost = new SimpleStringProperty(totalCost);
        else
            this.totalCost.setValue(totalCost);
        //this.totalCost.set(totalCost);// = totalCost;
    }

    public String getUnitCost()
    {
        /*if(unitCost==null)
            unitCost = new SimpleStringProperty("0");
        return unitCost.getValue();*/
        return newUnitCost;
    }

    public DoubleProperty getUnitCostProperty()
    {
        //if(unitCost == null)
        //System.out.println("This is unit "+unitCost);
        return new SimpleDoubleProperty(Double.parseDouble((unitCost.getValue()==null) || unitCost.getValue().length()==0?"0":unitCost.getValue()));
    }

    public String getTotalCost()
    {
        if(totalCost==null)
            totalCost = new SimpleStringProperty();

        return totalCost.getValue();
    }
}

