package com.oldschoolminecraft.osml.update;

public class Library
{
    private String name, version;
    
    public Library(String input)
    {
        String[] parts = input.split(":");
        name = parts[0];
        version = parts[1];
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getVersion()
    {
        return version;
    }
}
