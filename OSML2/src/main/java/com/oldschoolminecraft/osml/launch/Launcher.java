package com.oldschoolminecraft.osml.launch;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oldschoolminecraft.osml.Main;
import com.oldschoolminecraft.osml.mods.Mod;
import com.oldschoolminecraft.osml.patches.SkinFix;
import com.oldschoolminecraft.osml.update.Library;
import com.oldschoolminecraft.osml.update.VanillaLibrary;
import com.oldschoolminecraft.osml.update.VanillaManifest;
import com.oldschoolminecraft.osml.update.VersionManager;
import com.oldschoolminecraft.osml.update.VersionManifest;
import com.oldschoolminecraft.osml.util.ActionPipe;
import com.oldschoolminecraft.osml.util.OS;
import com.oldschoolminecraft.osml.util.Util;
import com.oldschoolminecraft.osml.util.ZipUtil;

@SuppressWarnings("all")
public class Launcher
{
    public Launcher() {}
    
    private ActionPipe launchEvent;
    private ActionPipe exitEvent;
    
    public void setLaunchHandler(ActionPipe pipe)
    {
        launchEvent = pipe;
    }
    
    public void setExitHandler(ActionPipe pipe)
    {
        exitEvent = pipe;
    }
    
    public void debugLaunch()
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            VanillaManifest manifest = mapper.readValue(getClass().getResourceAsStream("/versions/b1.7.3-vanilla.json"), VanillaManifest.class);
            
            ArrayList<File> loadQueue = new ArrayList<File>();
            for (VanillaLibrary lib : manifest.libraries)
            {
                File libFile = new File(Main.librariesDir, String.format("%s/%s/%s-%s.jar", lib.name, lib.version, lib.name, lib.version));
                if (!libFile.exists())
                    System.out.println(String.format("Library doesn't exist: %s-%s", lib.name, lib.version));
                else
                    System.out.println(String.format("Library added to load queue: %s-%s", lib.name, lib.version));
                loadQueue.add(libFile);
            }
            File clientFile = new File(Main.librariesDir, "minecraft/b1.7.3/minecraft-b1.7.3.jar");
            
            // prepare natives
            File nativesFile;
            switch (OS.getOS())
            {
                default:
                    nativesFile = new File(Main.librariesDir, "natives_windows/b1.7.3/natives_windows-b1.7.3.jar");
                    break;
                case Windows:
                    nativesFile = new File(Main.librariesDir, "natives_windows/b1.7.3/natives_windows-b1.7.3.jar");
                    break;
                case Linux:
                    nativesFile = new File(Main.librariesDir, "natives_linux/b1.7.3/natives_linux-b1.7.3.jar");
                    break;
                case Mac:
                    nativesFile = new File(Main.librariesDir, "natives_osx/b1.7.3/natives_osx-b1.7.3.jar");
                    break;
            }
            File clientDir = clientFile.getParentFile();
            File nativesDir = new File(clientDir, "natives");
            
            if (!nativesDir.exists())
                nativesDir.mkdir();
            
            if (nativesFile.exists() && !Main.config.disableUpdates)
                ZipUtil.unzip(nativesFile.getAbsolutePath(), nativesDir.getAbsolutePath());
            else
                System.out.println("Natives file doesn't exist");
            
            Object lock = new Object();
            
            // apply mods
            if (Main.modManager.mods.size() > 0)
            {
                System.out.println("Applying jarmods...");
                
                File moddedFile = new File(Main.modsDir, "minecraft.jar");
                
                if (moddedFile.exists())
                    moddedFile.delete();
                
                if (clientFile.exists())
                    Files.copy(Paths.get(clientFile.getAbsolutePath()), Paths.get(moddedFile.getAbsolutePath()));
                
                File modsTmpDir = new File(Main.modsDir, "tmp");
                if (modsTmpDir.exists())
                    Util.deleteDirectory(modsTmpDir);
                modsTmpDir.mkdirs();
                
                ZipUtil.extractAllTo(moddedFile.getAbsolutePath(), modsTmpDir.getAbsolutePath());
                
                for (Mod mod : Main.modManager.mods)
                    ZipUtil.extractAllTo(mod.getPath(), modsTmpDir.getAbsolutePath());
                
                new File(modsTmpDir, "META-INF/MANIFEST.MF").delete();
                
                ZipUtil.zipAllTo(modsTmpDir.getAbsolutePath(), moddedFile.getAbsolutePath());
                
                System.out.println("Applied jarmods");
                
                clientFile = moddedFile;
            } else {
                System.out.println("No mods available");
            }
            
            ArrayList<URL> urls_pre = new ArrayList<URL>();
            for (File file : loadQueue)
                urls_pre.add(Paths.get(file.getAbsolutePath()).toUri().toURL());
            urls_pre.add(Paths.get(clientFile.getAbsolutePath()).toUri().toURL());
            URL[] urls = new URL[urls_pre.size()];
            urls_pre.toArray(urls);
            
            // load jars
            URLClassLoader urlClassLoader = new URLClassLoader(urls);
            
            // apply patches
            new SkinFix().apply();
            
            // setup environment
            System.setProperty("java.library.path", nativesDir.getAbsolutePath());
            System.setProperty("org.lwjgl.librarypath", nativesDir.getAbsolutePath());
            
            Class mainClass = urlClassLoader.loadClass("net.minecraft.client.Minecraft");
            Method mainFunction = mainClass.getDeclaredMethod("main", String[].class);
            
            mainFunction.invoke(null, new Object[] { new String[] { Main.authDataFile.username, Main.authDataFile.accessToken } });
            
            if (launchEvent != null) launchEvent.fire();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void launch()
    {
        try
        {
            VersionManifest manifest = new VersionManager().loadInternal("b1.7.3");

            StringBuilder libsb = new StringBuilder();
            for (String input : manifest.libraries)
            {
                Library lib = new Library(input);
                File libFile = new File(Main.librariesDir, String.format("%s/%s/%s-%s.jar", lib.name, lib.version, lib.name, lib.version));
                if (!libFile.exists())
                    System.out.println(String.format("Library doesn't exist: %s-%s", lib.name, lib.version));
                libsb.append(libFile.getAbsolutePath() + File.pathSeparator);
            }
            
            Library client = new Library(manifest.client);
            File clientFile = new File(Main.librariesDir, String.format("%s/%s/%s-%s.jar", client.name, client.version, client.name, client.version));
            libsb.append(clientFile.getAbsolutePath() + ((Main.modManager.mods.size() > 0) ? ";" : ""));
            
            ArrayList<String> launchArguments = new ArrayList<String>();
            
            launchArguments.add(Main.config.javaExecutable);
            String[] jvm = Main.config.jvmArguments.split(" ");
            for (String arg : jvm)
                launchArguments.add(arg);
            
            String nativesInput = manifest.natives.windows.url;
            /*switch (OS.getOS())
            {
                default:
                    nativesInput = manifest.natives.windows;
                    break;
                case Windows:
                    nativesInput = manifest.natives.windows;
                    break;
                case Linux:
                    nativesInput = manifest.natives.linux;
                    break;
                case Mac:
                    nativesInput = manifest.natives.osx;
                    break;
            }*/
            Library natives = new Library(nativesInput);
            File nativesFile = new File(Main.librariesDir, String.format("%s/%s/%s-%s.jar", natives.name, natives.version, natives.name, natives.version));
            File clientDir = clientFile.getParentFile();
            File nativesDir = new File(clientDir, "natives");
            
            if (!nativesDir.exists())
                nativesDir.mkdir();
            
            if (nativesFile.exists())
                ZipUtil.unzip(nativesFile.getAbsolutePath(), nativesDir.getAbsolutePath());
            else
                System.out.println("Natives file doesn't exist");
            
            launchArguments.add("-Djava.library.path=" + nativesDir.getAbsolutePath());
            launchArguments.add("-classpath");
            launchArguments.add(libsb.toString().trim());
            launchArguments.add("net.minecraft.client.Minecraft");
            
            String[] launchParameters = manifest.launchArgs.split(" ");
            for (String arg : launchParameters)
            {
                arg = arg.replace("{username}", Main.authDataFile.username);
                arg = arg.replace("{accessToken}", Main.authDataFile.accessToken);
                arg = arg.replace("{uuid}", Main.authDataFile.uuid);
                launchArguments.add(arg.trim());
            }
            
            System.out.println("Launch args: " + launchArguments.toString());
            
            Thread nativesRemovalHook = new Thread(() -> nativesDir.delete());
            Runtime.getRuntime().addShutdownHook(nativesRemovalHook);
            
            ProcessBuilder pb = new ProcessBuilder(launchArguments);
            pb.directory(new File(Main.config.gameDirectory));
            pb.inheritIO();
            Process proc = pb.start();
            
            // apply runtime patches
            new SkinFix().apply();
            
            int exitCode = proc.waitFor();
            
            System.out.println("Game process exited with code: " + exitCode);
            
            //TODO: launcher visibility?
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private String[] libraries =
    {
        "jinput.jar",
        "lwjgl.jar",
        "lwjgl_util.jar",
        "json.jar",
        "core.jar",
        "databind.jar",
        "annotations.jar"
    };
    
    public void oldLaunch()
    {
        try
        {
            StringBuilder libsb = new StringBuilder();
            for (int i = 0; i < libraries.length; i++)
                libsb.append(os_library(libraries[i]) + File.pathSeparator);
            libsb.append(os_library("minecraft.jar"));
            String libs = libsb.toString();
            
            ArrayList<String> launchArguments = new ArrayList<String>();
            
            launchArguments.add(Main.config.javaExecutable);
            String[] jvm = Main.config.jvmArguments.split(" ");
            for (String arg : jvm)
                launchArguments.add(arg);
            launchArguments.add("-Djava.library.path=" + Util.getNativesPath().getAbsolutePath());
            launchArguments.add("-classpath");
            launchArguments.add(libs);
            launchArguments.add("net.minecraft.client.Minecraft");
            launchArguments.add(Main.authDataFile.username);
            launchArguments.add(Main.authDataFile.accessToken);
            launchArguments.add(Main.authDataFile.uuid);
            launchArguments.add("--enable-auth");
            launchArguments.add("--mojang-auth");
            
            System.out.println("Old launch: " + launchArguments.toString());
            
            ProcessBuilder pb = new ProcessBuilder(launchArguments);
            pb.directory(new File(Main.config.gameDirectory));
            pb.inheritIO();
            pb.start();
            
            //TODO: launcher visibility?
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private String os_library(String name)
    {
        return new File(Util.getBinDirectory(), name).getAbsolutePath();
    }
}
