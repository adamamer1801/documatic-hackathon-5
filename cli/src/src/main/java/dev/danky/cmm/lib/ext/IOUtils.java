package dev.danky.cmm.lib.ext;

import java.io.IOException;
import java.io.OutputStream;

// This is also an Apache CommonsIO module
// IOUtils.write()

public class IOUtils {
    public static void write(final byte[] data, final OutputStream output)
            throws IOException {
        if (data != null) {
            output.write(data);
        }
    }
}
