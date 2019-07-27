package com.recordrack.logic;

import com.recordrack.ui.RecordRackAlert;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Properties
{
    private java.util.Properties props = null;
    private static Properties properties = null;

    private void init()
    {
        if(props == null)
        {
            try
            {
                System.out.println("Init called");
                String path = System.getProperty("user.home")+File.separator+"RecordRack";
                File file = new File(path);
                System.out.println(file.getAbsolutePath());
                props = new java.util.Properties();
                if(!file.exists())
                {
                    file.mkdirs();
                    //file.setWritable(true,true);
                    file = new File(file.getAbsolutePath()+File.separator+"application.properties");
                    System.out.println("path is "+file.getAbsolutePath());
                    file.createNewFile();

                    props.put("dbName","rsnq34bert");
                    props.store(new FileWriter(file),"Properties file for RecordRack");

                }
                else
                {
                    file = new File(file.getAbsolutePath()+File.separator+"application.properties");
                    props.load(new FileReader(file));
                }


                System.out.println("file created");

            }
            catch (IOException e)
            {
                e.printStackTrace();
                RecordRackAlert.showInformationAlert(e.getMessage(),e.getCause().toString());
            }
        }
    }


    public static String getDbName()
    {
        if(properties == null)
        {
            properties = new Properties();
            System.out.println("about to call init");
            properties.init();
        }
        return properties.props.getProperty("dbName");
    }
}
