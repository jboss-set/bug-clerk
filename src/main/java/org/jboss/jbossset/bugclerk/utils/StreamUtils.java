package org.jboss.jbossset.bugclerk.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.stream.StreamResult;

public final class StreamUtils {

    private StreamUtils() {}

    public static OutputStream getOutputStreamForFile(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            return new FileOutputStream(file);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static StreamResult getStreamResultForFile(String filename) {
        if (filename == null || "".equals(filename) )
            throw new IllegalArgumentException("Filename for result can't be null or empty.");
        return  new StreamResult(new File(filename));
    }

}
