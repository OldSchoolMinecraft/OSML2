package com.oldschoolminecraft.osml.update;

public class Library
{
    public String name;
    public String version;
    
    public Library() {}
    
    public Library(String input)
    {
        String[] parts = input.split(":");
        name = parts[0];
        version = parts[1];
    }
}
