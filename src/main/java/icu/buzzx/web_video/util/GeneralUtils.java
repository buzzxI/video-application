package icu.buzzx.web_video.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class GeneralUtils {

    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return -1;
        return file.length();
    }

    public static byte[] readFile(String filePath, long start, long end) {
        byte[] rst = new byte[(int)(end - start + 1)];
        // try-catch-resource -> no need for explicit raf.close()
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
            raf.seek(start);
            raf.read(rst);
        } catch (IOException e) {
            rst = null;
        }
        return rst;
    }
}
