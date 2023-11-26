package de.freese.base.core.image;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * @author Thomas Freese
 */
public final class IconConverter {
    /**
     * Generate Java-Code from the Icon.
     */
    public static void convert(final String iconFile, final String javaDest) throws IOException {
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(iconFile));
             FileWriter writer = new FileWriter(javaDest, StandardCharsets.UTF_8)) {
            convert(inputStream, writer);
        }
    }

    public static void convert(final InputStream iconInputStream, final Writer javaWriter) throws IOException {
        int i = 0;

        javaWriter.write("return new ImageIcon(new byte[] {");

        if ((i = iconInputStream.read()) != -1) {
            javaWriter.write(i);
            //            javaWriter.write(Byte.toString((byte) i));
        }

        while ((i = iconInputStream.read()) != -1) {
            javaWriter.write(",");
            javaWriter.write(i);
            //            javaWriter.write(Byte.toString((byte) i));
        }

        javaWriter.write("});");
    }

    private IconConverter() {
        super();
    }
}
