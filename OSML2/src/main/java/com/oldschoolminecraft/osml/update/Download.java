package com.oldschoolminecraft.osml.update;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Download
{
    private Library lib;
    private HttpsURLConnection connection;
    private long contentLength;
    
    public Download(Library lib)
    {
        this.lib = lib;
    }
    
    public void connect() throws Exception
    {
        connection = (HttpsURLConnection) new URL(String.format("https://www.oldschoolminecraft.com/client_lib?name=%s&version=%s", lib.getName(), lib.getVersion())).openConnection();
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
}
