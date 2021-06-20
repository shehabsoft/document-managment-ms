package org.emu.docmanagment.alfresco;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
/**
 * Copyright 2021-2022 By Dirac Systems.
 * <p>
 * Created by {@khalid.nouh on 27/5/2021}.
 */
public class Utility {
    public static String getFileExtension(String originalFilename) {
        int index = originalFilename.lastIndexOf('.');
        String extension = originalFilename.substring(index + 1);
        return extension;
    }
    public static String getFileName(String originalFilename) {
        int index = originalFilename.lastIndexOf('.');
        String fileName = originalFilename.substring(0,index);
        return fileName;
    }

    public static byte[] getArrayFromInputStream(InputStream inputStream) throws IOException {
        byte[] bytes;
        byte[] buffer = new byte[1024];
        try(BufferedInputStream is = new BufferedInputStream(inputStream)){
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int length;
            while ((length = is.read(buffer)) > -1 ) {
                bos.write(buffer, 0, length);
            }
            bos.flush();
            bytes = bos.toByteArray();
        }
        return bytes;
    }
}
