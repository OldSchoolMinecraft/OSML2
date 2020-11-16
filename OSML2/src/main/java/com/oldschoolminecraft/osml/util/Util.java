package com.oldschoolminecraft.osml.util;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

public class Util
{
    /*
     * Thank you JuliusVan for the most retarded term known to man (netpage).
     */
    public static void openNetpage(String url)
    {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
        {
            try
            {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
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
        return new File(getLauncherDirectory(), "bin");
    }

    public static File getLauncherDirectory()
    {
        switch (OS.getOS())
        {
            default:
                System.out.println("Unknown operating system (assuming Windows).");
                return new File(backslashes(System.getProperty("user.home") + "/AppData/Roaming/.osml/"));
            case Windows:
                return new File(backslashes(System.getProperty("user.home") + "/AppData/Roaming/.osml/"));
            case Mac:
                return new File("~/Library/Application Support/osml/");
            case Linux:
                return new File(getLinuxHomeDirectory() + "/.osml/");
            case Unsupported:
                System.out.println("Unsupported operating system (assuming Linux).");
                return new File(getLinuxHomeDirectory() + "/.osml/");
        }
    }
    
    public static File getGameDirectory()
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
        return new File(System.getenv("JAVA_HOME") == null ? "./jre" : System.getenv("JAVA_HOME"));
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
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
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
            ex.printStackTrace();
            JSONObject obj = new JSONObject();
            obj.put("error", "Something went wrong: " + ex.getMessage());
            return new JSONWebResponse(0, obj);
        }
    }

    public static void downloadFile(String url, String path, boolean deleteIfExists)
    {
        try
        {
            File targetFile = new File(path);
            if (targetFile.exists() && deleteIfExists)
                targetFile.delete();

            URL fileURL = new URL(url);
            HttpURLConnection httpConn = (HttpURLConnection) fileURL.openConnection();
            httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            int responseCode = httpConn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                InputStream inputStream = httpConn.getInputStream();
                String saveFilePath = path;
                FileOutputStream outputStream = new FileOutputStream(saveFilePath);

                int bytesRead = -1;
                byte[] buffer = new byte[4096];
                while ((bytesRead = inputStream.read(buffer)) != -1)
                    outputStream.write(buffer, 0, bytesRead);

                outputStream.close();
                inputStream.close();

                System.out.println(String.format("Downloaded file '%s' to '%s'", url, path));
            } else {
                System.out.println(String.format("Download failed ('%s'): HTTP code was '%s'", url, responseCode));
            }
            httpConn.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String sha256File(String file) throws Exception
    {
        byte[] buffer = new byte[8192];
        int count;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        while ((count = bis.read(buffer)) > 0)
            md.update(buffer, 0, count);
        bis.close();
        BigInteger hash = new BigInteger(1, md.digest());
        return hash.toString(16);
    }

    public static String replaceLast(String string, String substring, String replacement)
    {
        int index = string.lastIndexOf(substring);
        if (index == -1)
            return string;
        return string.substring(0, index) + replacement + string.substring(index + substring.length());
    }
}
