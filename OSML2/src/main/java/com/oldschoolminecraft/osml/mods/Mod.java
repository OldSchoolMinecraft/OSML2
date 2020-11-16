package com.oldschoolminecraft.osml.mods;

public class Mod
{
    private String fileName;
    private String filePath;
    
    public Mod(String fileName, String filePath)
    {
        this.fileName = fileName;
        this.filePath = filePath;
    }
    
    public Mod() {}
    
    public String getName()
    {
        return fileName;
    }
    
    public String getPath()
    {
        return filePath;
    }
    
    public String toString()
    {
        return fileName;
    }
}
