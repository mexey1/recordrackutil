package com.recordrack.logic;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Class that contains methods for formatting string into money format and vice-versa
 */
public class MoneyFormatter
{
    public static double unFormatMoney(String money)
    {
        double d =0;
        try
        {
            DecimalFormat numberFormat = (DecimalFormat) DecimalFormat.getNumberInstance();
            numberFormat.setParseBigDecimal(true);
            d = numberFormat.parse(money.replaceAll("[^\\d.,]]","")).doubleValue();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        System.out.println("Money "+d);
        return d;
    }

    public static String formatMoney(String money)
    {
        String result = null;
        //System.out.println("Money passed in "+money);
        if(money==null|| money.equals("0") || money==""||money.equals("0.0")||money.equals("0.00"))
            result = "0.00";
        else if(money!="")
        {
            double d = 0;
            if (money.contains(","))
                d = unFormatMoney(money);
            else
                d = Double.parseDouble(money);
            DecimalFormat decimalFormat = new DecimalFormat("####,###,###.00");
            //NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
            result = decimalFormat.format(d);

           // System.out.println(result);
        }
        return result;
    }
}
