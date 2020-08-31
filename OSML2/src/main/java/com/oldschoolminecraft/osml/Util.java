package com.oldschoolminecraft.osml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import com.oldschoolminecraft.osml.util.OS;

public class Util
{
    public static String get(String url)
    {
        try
        {
            HttpsURLConnection httpClient = (HttpsURLConnection) new URL(url).openConnection();
            httpClient.setRequestMethod("GET");
            httpClient.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:79.0) Gecko/20100101 Firefox/79.0");
            
            try (BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream())))
            {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null)
                    response.append(line);
                return response.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }
    
    public static File getNativesPath()
    {
        return new File(getBinDirectory(), "natives");
    }
    
    public static File getBinDirectory()
    {
        return new File(getWorkingDirectory(), "bin");
    }
    
    public static File getWorkingDirectory()
    {
        switch (OS.getOS())
        {
            default:
                System.out.println("Unknown operating system (assuming Windows).");
                return new File(backslashes(System.getProperty("user.home") + "/AppData/Roaming/.osm/"));
            case Windows:
                return new File(backslashes(System.getProperty("user.home") + "/AppData/Roaming/.osm/"));
            case Mac:
                return new File("~/Library/Application Support/osm/");
            case Linux:
                return new File(getLinuxHomeDirectory() + "/.osm/");
            case Unsupported:
                System.out.println("Unsupported operating system (assuming Linux).");
                return new File(getLinuxHomeDirectory() + "/.osm/");
        }
    }
    
    public static File getJavaExecutable()
    {
        return new File(getJavaBin(), "java.exe");
    }
    
    public static File getJavaBin()
    {
        return new File(getJavaHome(), "bin");
    }
    
    public static File getJavaHome()
    {
        return new File(System.getenv("JAVA_HOME"));
    }
    
    public static String getLinuxHomeDirectory()
    {
        // $HOME environment variable should exist, but handle the situation when
        // it doesn't exist and/or the user executes program as root.
        String linux_home = System.getenv("HOME");
        if (linux_home == null)
        {
            String linux_user = System.getenv("USER");
            if (linux_user == "root")
                return "/root";
            else
                return "/home/" + linux_user;
        } else
            return linux_home;
    }
    
    private static String backslashes(String input)
    {
        return input.replaceAll("/", "\\\\");
    }
    
    public static JSONWebResponse postJSON(String url, JSONObject payload)
    {
        try
        {
            URL object = new URL(url);

            HttpURLConnection con = (HttpURLConnection) object.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("POST");

            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(payload.toString());
            wr.flush();

            StringBuilder sb = new StringBuilder();
            int status = con.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null)
                sb.append(line + "\n");
            br.close();
            return new JSONWebResponse(status, sb.toString().startsWith("{") ? new JSONObject(sb.toString()) : null);
        } catch (IOException ex) {
            JSONObject obj = new JSONObject();
            obj.put("error", "Something went wrong: " + ex.getMessage());
            return new JSONWebResponse(0, obj);
        }
    }
}
