package com.oldschoolminecraft.osml.update;

import com.fasterxml.jackson.databind.ObjectMapper;

public class VersionManager
{
    private int totalBytes;
    
    public void updateVersion(String name) throws Exception
    {
        VersionManifest manifest = loadInternal(name);
        
        System.out.println("Updating version: " + manifest.name);
        
        for (String input : manifest.libraries)
        {
            Library lib = new Library(input);
            
        }
    }
    
    public void downloadLibrary(String name, String version)
    {
        
    }
    
    public VersionManifest loadInternal(String name) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(getClass().getResourceAsStream("/versions/" + name + ".json"), VersionManifest.class);
    }
}
