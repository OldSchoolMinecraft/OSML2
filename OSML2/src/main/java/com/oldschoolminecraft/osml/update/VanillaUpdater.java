package com.oldschoolminecraft.osml.update;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oldschoolminecraft.osml.Main;
import com.oldschoolminecraft.osml.util.ActionPipe;
import com.oldschoolminecraft.osml.util.OS;
import com.oldschoolminecraft.osml.util.Util;

/**
 * @author moderator_man
 */
public class VanillaUpdater extends Thread
{
    private ActionPipe completeEvent;
    
    public VanillaUpdater(ActionPipe pipe)
    {
        completeEvent = pipe;
    }
    
    public void run()
    {
        try
        {
            if (Main.config.disableUpdates)
            {
                completeEvent.fire();
                return;
            }
            
            ObjectMapper mapper = new ObjectMapper();
            VanillaManifest manifest = mapper.readValue(getClass().getResourceAsStream("/versions/b1.7.3-vanilla.json"), VanillaManifest.class);
            
            for (VanillaLibrary lib : manifest.libraries)
            {
                File file = new File(Main.librariesDir, String.format("%s/%s/%s-%s.jar", lib.name, lib.version, lib.name, lib.version));
                if (!file.exists() || !Util.sha1File(file.getAbsolutePath()).equalsIgnoreCase(lib.sha1))
                    Util.downloadFile(lib.url, file.getAbsolutePath(), true);
            }
            
            File clientFile = new File(Main.librariesDir, "minecraft/b1.7.3/minecraft-b1.7.3.jar");
            if (!clientFile.exists() || !Util.sha1File(clientFile.getAbsolutePath()).equalsIgnoreCase(manifest.client.sha1))
                Util.downloadFile(manifest.client.url, clientFile.getAbsolutePath(), true);
            
            switch (OS.getOS())
            {
                case Windows:
                    File nWin = new File(Main.librariesDir, "natives_windows/b1.7.3/natives_windows-b1.7.3.jar");
                    if (!nWin.exists() || !Util.sha1File(nWin.getAbsolutePath()).equalsIgnoreCase(manifest.natives.windows.sha1))
                        Util.downloadFile(manifest.natives.windows.url, nWin.getAbsolutePath(), true);
                    break;
                case Linux:
                    File nLin = new File(Main.librariesDir, "natives_linux/b1.7.3/natives_linux-b1.7.3.jar");
                    if (!nLin.exists() || !Util.sha1File(nLin.getAbsolutePath()).equalsIgnoreCase(manifest.natives.linux.sha1))
                        Util.downloadFile(manifest.natives.linux.url, nLin.getAbsolutePath(), true);
                    break;
                case Mac:
                    File nMac = new File(Main.librariesDir, "natives_osx/b1.7.3/natives_osx-b1.7.3.jar");
                    if (!nMac.exists() || !Util.sha1File(nMac.getAbsolutePath()).equalsIgnoreCase(manifest.natives.osx.sha1))
                        Util.downloadFile(manifest.natives.osx.url, nMac.getAbsolutePath(), true);
                    break;
                case Unsupported:
                    System.out.println("Failed to download natives (unsupported operating system)");
                    break;
            }
            
            completeEvent.fire();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
