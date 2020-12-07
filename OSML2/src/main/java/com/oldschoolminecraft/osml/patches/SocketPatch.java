package com.oldschoolminecraft.osml.patches;

import java.net.Socket;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class SocketPatch extends RuntimePatch
{
    public void apply()
    {
        new ByteBuddy().redefine(Socket.class).visit(Advice.to(SocketAdvice.class).on(ElementMatchers.named("connect"))).make().load(Socket.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
