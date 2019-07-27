package com.recordrack.interfaces;

/**
 * Interface for setting income/expense values
 */
public interface IncomeExpenseListener
{
    /**
     * set the value for income
     * @param income income value
     */
    public void setIncome(double income);

    /**
     * set the value for expense
     * @param expense expense value
     */
    public void setExpense(double expense);
}
