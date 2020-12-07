package com.oldschoolminecraft.osml;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.oldschoolminecraft.osml.util.Util;

public class Logger
{
    private static FileWriter writer;
    
    public static void init()
    {
        try
        {
            if (writer != null)
                return;
            
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());
            
            writer = new FileWriter(new File(Util.getLauncherDirectory(), String.format("launcher-%s.log", formatter.format(date))), true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void close()
    {
        try
        {
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void raw(String msg)
    {
        try
        {
            writer.append(msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void info(String msg)
    {
        try
        {
            writer.append("[INFO] " + msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void warning(String msg)
    {
        try
        {
            writer.append("[WARNING] " + msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void severe(String msg)
    {
        try
        {
            writer.append("[SEVERE] " + msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
