package com.oldschoolminecraft.osml.mods;

import java.io.File;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oldschoolminecraft.osml.Main;

public class ModManager
{
    public ArrayList<Mod> mods = new ArrayList<Mod>();
    
    public void load()
    {
        try
        {
            if (!Main.modsManifestFile.exists())
            {
                save();
                return;
            }
            
            ObjectMapper mapper = new ObjectMapper();
            ModsManifest manifest = mapper.readValue(Main.modsManifestFile, ModsManifest.class);
            for (String mod : manifest.mods)
            {
                File modFile = new File(mod);
                mods.add(new Mod(modFile.getName(), modFile.getAbsolutePath()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void save()
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            ModsManifest manifest = new ModsManifest();
            for (Mod mod : mods)
                manifest.mods.add(mod.getPath());
            mapper.writeValue(Main.modsManifestFile, manifest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void applyMods()
    {
        //TODO: appply jar mods
    }
    
    public void registerShutdownHook()
    {
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            //TODO: remove modded jar
        }));
    }
}
