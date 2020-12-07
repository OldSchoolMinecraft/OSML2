package com.oldschoolminecraft.osml;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class OutStream extends PrintStream
{
    private BufferedOutputStream bos;
    
    public OutStream(OutputStream out)
    {
        super(out);
        
        this.bos = new BufferedOutputStream(out);
    }
    
    public void println(String msg)
    {
        super.println(msg);
        Logger.raw(msg);
    }
    
    public BufferedOutputStream getBufferedOutputStream()
    {
        return bos;
    }
}
