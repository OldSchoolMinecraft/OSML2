package com.oldschoolminecraft.osml.patches;

import java.net.URLClassLoader;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class MinecraftPatch extends RuntimePatch
{
    private URLClassLoader loader;
    
    public MinecraftPatch(URLClassLoader loader)
    {
        this.loader = loader;
    }
    
    public void apply()
    {
        try
        {
            new ByteBuddy()
                .redefine(loader.loadClass("net.minecraft.client.Minecraft"))
                .visit(Advice.to(MinecraftAdvice.class).on(ElementMatchers.isPublic().and(ElementMatchers.named("a").and(ElementMatchers.takesArguments(3)))))
                .make()
                .load(loader, ClassReloadingStrategy.fromInstalledAgent());
        } catch (Exception ex) {
            System.out.println("Failed to apply Minecraft patch");
            ex.printStackTrace();
        }
    }
}
