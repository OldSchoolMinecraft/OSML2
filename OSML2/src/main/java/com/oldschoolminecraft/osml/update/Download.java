package com.oldschoolminecraft.osml.update;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import com.oldschoolminecraft.osml.util.Util;

public class Download
{
    private Library lib;
    private HttpsURLConnection connection;
    private long contentLength;
    private long file_size;
    private String file_hash;
    
    public Download(Library lib)
    {
        this.lib = lib;
    }
    
    public void getMeta() throws Exception
    {
        JSONObject res = new JSONObject(Util.get(String.format("https://www.oldschoolminecraft.com/client_lib?name=%s&version=%s&meta", lib.name, lib.version)));
        if (res.has("file_size") && res.has("file_hash") && !res.has("error"))
        {
            file_size = res.getLong("file_size");
            file_hash = res.getString("file_hash");
        }
    }
    
    public void connect() throws Exception
    {
        connection = (HttpsURLConnection) new URL(String.format("https://www.oldschoolminecraft.com/client_lib?name=%s&version=%s", lib.name, lib.version)).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        contentLength = connection.getContentLengthLong();
    }
    
    public Library getLibrary()
    {
        return lib;
    }
    
    public HttpsURLConnection getConnection()
    {
        return connection;
    }
    
    public long getContentLength()
    {
        return contentLength;
    }
    
    public long getFileSize()
    {
        return file_size;
    }
    
    public String getFileHash()
    {
        return file_hash;
    }
}
