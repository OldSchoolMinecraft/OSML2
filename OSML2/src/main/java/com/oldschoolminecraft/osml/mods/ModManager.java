package com.oldschoolminecraft.osml.mods;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oldschoolminecraft.osml.Main;
import com.oldschoolminecraft.osml.update.Library;
import com.oldschoolminecraft.osml.update.VersionManager;
import com.oldschoolminecraft.osml.update.VersionManifest;
import com.oldschoolminecraft.osml.util.ZipUtil;

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
        try
        {
            File playJar = new File(Main.modsDir, "minecraft.jar");
            
            if (playJar.exists())
                playJar.delete();
            
            VersionManifest manifest = new VersionManager().loadInternal("b1.7.3");
            
            Library client = new Library(manifest.client);
            File clientFile = new File(Main.librariesDir, String.format("%s/%s/%s-%s.jar", client.name, client.version, client.name, client.version));
            
            if (clientFile.exists())
                Files.copy(Paths.get(clientFile.getAbsolutePath()), Paths.get(playJar.getAbsolutePath()));
            
            File tmpDir = new File(Main.modsDir, "tmp");
            if (!tmpDir.exists())
                tmpDir.mkdirs();
            
            for (Mod mod : mods)
                ZipUtil.copyContents(mod.getPath(), playJar.getAbsolutePath(), tmpDir.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void registerShutdownHook()
    {
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            System.out.println("Mod manager shutting down...");
        }));
    }
}
