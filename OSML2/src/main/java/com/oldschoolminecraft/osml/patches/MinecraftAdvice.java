package com.oldschoolminecraft.osml.patches;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.oldschoolminecraft.osml.Main;

import net.bytebuddy.asm.Advice;

public class MinecraftAdvice
{
    @Advice.OnMethodExit
    static void intercept()
    {
        try
        {
            if (Main.urlClassLoader != null)
            {
                Class<?> minecraft = Main.urlClassLoader.loadClass("net.minecraft.client.Minecraft");
                
                if (minecraft != null)
                {
                    Field theWorld = minecraft.getField("f"); // f = theWorld
                    boolean multiplayerWorld = theWorld != null && theWorld.get(null).getClass().getField("B").getBoolean(null); // B = multiplayerWorld
                    
                    Class<?> rpcHandler = ClassLoader.getSystemClassLoader().loadClass("com.oldschoolminecraft.osml.rpc.RPCHandler");
                    Method updateMethod = rpcHandler.getMethod("update");
                    
                    rpcHandler.getField("connected").set(null, multiplayerWorld);
                    updateMethod.invoke(null);
                    
                    System.out.println(">> DEBUG << UPDATED MULTIPLAYER WORLD STATUS");
                } else {
                    System.out.println(">> DEBUG << MINECRAFT == NULL");
                }
            } else {
                System.out.println(">> DEBUG << CLASSLOADER == NULL");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
