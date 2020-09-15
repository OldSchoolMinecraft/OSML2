package com.oldschoolminecraft.osml.update;

import java.io.File;

import com.oldschoolminecraft.osml.Main;

public class LibraryManager
{
    private File libraryDir = new File(new File(Main.config.gameDirectory), "libraries");
    
    public boolean libraryExists(Library lib)
    {
        File libFile = new File(libraryDir, String.format("%s/%s/%s-%s.jar", lib.getName(), lib.getVersion(), lib.getName(), lib.getVersion()));
        return libFile.exists();
    }
    
    public File getLibraryFile(Library lib)
    {
        return new File(libraryDir, String.format("%s/%s/%s-%s.jar", lib.getName(), lib.getVersion(), lib.getName(), lib.getVersion()));
    }
    
    public void updateLibrary(Library lib)
    {
        File libFile = getLibraryFile(lib);
        
        
    }
}
