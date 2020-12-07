package com.oldschoolminecraft.osml.rpc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

public class RPCHandler
{
    public static String hostname;
    public static boolean connected;
    public static boolean playing;
    
    public static void init()
    {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> System.out.println("Discord is ready!");
        
        update();
        
        DiscordRPC.INSTANCE.Discord_Initialize("754299715855843368", handlers, true, "STEAM_ID");
        
        new Thread(() ->
        {
            while (!Thread.currentThread().isInterrupted())
            {
                DiscordRPC.INSTANCE.Discord_RunCallbacks();
                
                try
                {
                    Thread.sleep(2000);
                } catch (Exception ex) {}
            }
        }, "RPC-Callback-Handler").start();
    }
    
    public static void update()
    {
        DiscordRichPresence presence = new DiscordRichPresence();
        
        //presence.details = "Old School Minecraft";
        presence.largeImageKey = "osm";
        
        if (playing)
        {
            if (connected)
                presence.state = "Playing on " + ((hostname == null) ? "multiplayer" : hostname);
            else
                presence.state = "Playing singleplayer";
        } else
            presence.state = "Not playing";
        
        DiscordRPC.INSTANCE.Discord_UpdatePresence(presence);
    }
}
