package com.oldschoolminecraft.osml.mods;

import java.io.File;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ModManager
{
    public ArrayList<Mod> mods;
    
    public ModManager()
    {
        this.mods = new ArrayList<Mod>();
    }
    
    public void addMod(Mod mod)
    {
        mods.add(mod);
    }
    
    public void removeMod(Mod mod)
    {
        mods.remove(mod);
    }
    
    public void removeMod(String fileName)
    {
        for (Mod mod : mods)
            if (mod.getName() == fileName)
                mods.remove(mod);
    }
    
    public void load(File src)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            ModsManifest manifest = mapper.readValue(src, ModsManifest.class);
            for (String path : manifest.mods)
                mods.add(new Mod(path, new File(path).getName()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void save(File dst)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            ModsManifest manifest = new ModsManifest();
            for (Mod mod : mods)
                manifest.mods.add(mod.getPath());
            mapper.writeValue(dst, manifest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
