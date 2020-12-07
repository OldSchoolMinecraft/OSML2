package com.oldschoolminecraft.osml.patches;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import net.bytebuddy.asm.Advice;

public class SocketAdvice
{
    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 0) SocketAddress address)
    {
        if (address instanceof InetSocketAddress)
        {
            InetSocketAddress inet = (InetSocketAddress) address;
            
            try
            {
                if (inet.getPort() == 80 || inet.getPort() == 443) // NO HTTP PORTS!
                    return;
                
                Class<?> rpcHandler = ClassLoader.getSystemClassLoader().loadClass("com.oldschoolminecraft.osml.rpc.RPCHandler");
                Method updateMethod = rpcHandler.getMethod("update");
                
                rpcHandler.getField("playing").set(null, true);
                rpcHandler.getField("hostname").set(null, inet.getHostName());
                rpcHandler.getField("connected").set(null, true);
                
                updateMethod.invoke(null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
