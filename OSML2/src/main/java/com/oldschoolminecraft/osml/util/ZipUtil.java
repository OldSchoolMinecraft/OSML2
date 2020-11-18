package com.oldschoolminecraft.osml.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import de.schlichtherle.truezip.file.TFile;

public class ZipUtil
{
    private static final int BUFFER_SIZE = 4096;

    public static void unzip(String zipFilePath, String destDirectory) throws IOException
    {
        File destDir = new File(destDirectory);
        if (!destDir.exists())
            destDir.mkdir();
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        while (entry != null)
        {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory())
                extractFile(zipIn, filePath);
            else {
                File dir = new File(filePath);
                dir.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException
    {
        File dst = new File(filePath);
        dst.getParentFile().mkdirs();
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1)
            bos.write(bytesIn, 0, read);
        bos.close();
    }
    
    public static void copyContents(String from, String to)
    {
        try
        {
            new TFile(from).cp_rp(new TFile(to));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static String buildPath(String path, String file)
    {
        if (path == null || path.isEmpty())
        {
            return file;
        } else {
            return path + "/" + file;
        }
    }
    
    public static void zipFile(ZipOutputStream zos, String path, File file) throws IOException
    {
        if (file.isDirectory())
        {
            System.out.println("File is directory");
            return;
        }
        
        if (!file.canRead())
        {
            System.out.println("Cannot read " + file.getCanonicalPath() + " (maybe because of permissions)");
            return;
        }

        System.out.println("Compressing " + file.getName());
        zos.putNextEntry(new ZipEntry(buildPath(path, file.getName())));
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4092];
        int byteCount = 0;
        while ((byteCount = fis.read(buffer)) != -1)
            zos.write(buffer, 0, byteCount);
        fis.close();
        zos.closeEntry();
    }
}
