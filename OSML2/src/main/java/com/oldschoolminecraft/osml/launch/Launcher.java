package com.oldschoolminecraft.osml.launch;

import java.io.File;
import java.util.ArrayList;

import com.oldschoolminecraft.osml.Main;
import com.oldschoolminecraft.osml.update.Library;
import com.oldschoolminecraft.osml.update.VersionManager;
import com.oldschoolminecraft.osml.update.VersionManifest;
import com.oldschoolminecraft.osml.util.Util;

public class Launcher
{
    private String[] libraries = new String[]
    {
        "jinput.jar",
        "lwjgl.jar",
        "lwjgl_util.jar",
        "json.jar",
        "core.jar",
        "databind.jar",
        "annotations.jar"
    };
    
    public Launcher() {}
    
    public void launch()
    {
        try
        {
            VersionManifest manifest = new VersionManager().loadInternal("b1.7.3");

            StringBuilder libsb = new StringBuilder();
            for (String input : manifest.libraries)
            {
                Library lib = new Library(input);
                File libFile = new File(Main.librariesDir, String.format("%s/%s/%s-%s.jar", lib.getName(), lib.getVersion(), lib.getName(), lib.getVersion()));
                if (!libFile.exists())
                    System.out.println(String.format("Library doesn't exist: %s-%s", lib.getName(), lib.getVersion()));
                libsb.append(libFile.getAbsolutePath() + File.pathSeparator);
            }
            
            Library client = new Library(manifest.client);
            File clientFile = new File(Main.librariesDir, String.format("%s/%s/%s-%s.jar", client.getName(), client.getVersion(), client.getName(), client.getVersion()));
            libsb.append(clientFile.getAbsolutePath());
            
            ArrayList<String> launchArguments = new ArrayList<String>();
            
            launchArguments.add(Main.config.javaExecutable);
            launchArguments.add("-Djava.library.path=" + Util.getNativesPath().getAbsolutePath());
            launchArguments.add("-classpath");
            launchArguments.add(libsb.toString());
            launchArguments.add("net.minecraft.client.Minecraft");
            
            String[] jvm = Main.config.jvmArguments.split(" ");
            for (String arg : jvm)
                launchArguments.add(arg);
            
            String[] launchParameters = manifest.launchArgs.split(" ");
            for (String arg : launchParameters)
            {
                arg = arg.replace("{username}", Main.authDataFile.username);
                arg = arg.replace("{accessToken}", Main.authDataFile.accessToken);
                arg = arg.replace("{uuid}", Main.authDataFile.uuid);
                launchArguments.add(arg);
            }
            
            System.out.println("Launch args: " + launchArguments.toString());
            
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
            
            System.out.println(launchArguments.toString());
            
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
