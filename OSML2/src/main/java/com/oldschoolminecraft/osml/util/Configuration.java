package com.oldschoolminecraft.osml.util;

public class Configuration
{
    public String gameDirectory;
    public int launcherVisibility;
    public String javaExecutable;
    public String jvmArguments;
    public boolean disableUpdates;
    
    public Configuration() {}
    
    public Configuration(String gameDirectory, int launcherVisibility, String javaExecutable, String jvmArguments, boolean disableUpdates)
    {
        this.gameDirectory = gameDirectory;
        this.launcherVisibility = launcherVisibility;
        this.javaExecutable = javaExecutable;
        this.jvmArguments = jvmArguments;
        this.disableUpdates = disableUpdates;
    }
    
    public static Configuration defaultConfig = new Configuration(Util.getGameDirectory().getAbsolutePath(), 0, Util.getJavaExecutable().exists() ? Util.getJavaExecutable().getAbsolutePath() : "java", "-Xmx1G -XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -XX:-UseAdaptiveSizePolicy -Xmn128M", false);
}
