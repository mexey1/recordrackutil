package com.recordrack.interfaces;

/**
 * interface implemented by all RowItems and contains base methods that they should all implement
 */
public interface RowItem
{
    /**
     * set quantity for the current item
     * @param val value to be set to
     */
    public void setQuantity(String val);

    /**
     * set new quantity for the current item
     * @param val value to be set to
     */
    public void setNewQuantity(String val);
}
