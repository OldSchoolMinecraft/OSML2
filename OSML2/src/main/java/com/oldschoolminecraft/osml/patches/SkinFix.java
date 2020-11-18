package com.oldschoolminecraft.osml.patches;

import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @author moderator_man
 * @author codieradical
 */
public class SkinFix extends RuntimePatch
{
    public void apply()
    {
        new ByteBuddy().redefine(HttpURLConnection.class).visit(Advice.to(SkinFixAdvice.class).on(ElementMatchers.named("getResponseCode"))).make().load(HttpURLConnection.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
    
    public static class SkinFixAdvice
    {
        @Advice.OnMethodExit
        static void intercept(@Advice.This HttpURLConnection con, @Advice.Return(readOnly = false) int code)
        {
            if (con.getURL().toString().contains("http://s3.amazonaws.com/MinecraftSkins/"))
            {
                try
                {
                    String url = con.getURL().toString();
                    String username = (url.contains("/MinecraftSkins/")
                            ? url.substring(url.indexOf("/MinecraftSkins/"))
                            : url.substring(url.indexOf("/skin/")))
                            .replace("/MinecraftSkins/", "")
                            .replace("/skin/", "")
                            .replace(".png", "");
                    
                    HttpsURLConnection httpClient = (HttpsURLConnection) new URL("https://www.oldschoolminecraft.com/getskin?direct&username=" + username).openConnection();
                    httpClient.setRequestMethod("GET");
                    httpClient.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:79.0) Gecko/20100101 Firefox/79.0");
                    
                    Field isField = con.getClass().getDeclaredField("inputStream");
                    isField.setAccessible(true);
                    isField.set(con, httpClient.getInputStream());
                    
                    code = 200;
                    
                    System.out.println("SkinFix runtime patched applied (skin)");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (con.getURL().toString().contains("http://s3.amazonaws.com/MinecraftCloaks/")) {
                try
                {
                    String url = con.getURL().toString();
                    String username = (url.contains("/MinecraftCloaks/")
                            ? url.substring(url.indexOf("/MinecraftCloaks/"))
                            : url.substring(url.indexOf("/cloak/")))
                            .replace("/MinecraftCloaks/", "")
                            .replace("/cloak/", "")
                            .replace(".png", "");
                    
                    HttpsURLConnection httpClient = (HttpsURLConnection) new URL("https://www.oldschoolminecraft.com/getcloak?direct&username=" + username).openConnection();
                    httpClient.setRequestMethod("GET");
                    httpClient.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:79.0) Gecko/20100101 Firefox/79.0");
                    
                    Field isField = con.getClass().getDeclaredField("inputStream");
                    isField.setAccessible(true);
                    isField.set(con, httpClient.getInputStream());
                    
                    code = 200;
                    
                    System.out.println("SkinFix runtime patched applied (cloak)");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
