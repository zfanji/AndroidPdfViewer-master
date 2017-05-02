package utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Zfanji on 2017/3/27.
 */

public class AppUtils {
    /**
     * Close outputStream
     *
     * @param is
     */
    public static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
                stream = null;
            } catch (IOException e) {
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        stream = null;
                    }
                    stream = null;
                }
            }
        }
    }
}
