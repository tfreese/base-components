package de.freese.base.core.image;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Mithilfe dieser Klasse kann man eine Grafikdatei in Java-Quellcode konvertieren.
 *
 * @author Thomas Freese
 */
public final class IconConverter {
    /**
     * Liest die Grafik aus der Quelldatei und generiert dafür Java-Quellcode in der Zieldatei
     */
    public static void convert(final String sourceFile, final String destFile) throws IOException {
        try (FileInputStream input = new FileInputStream(sourceFile); FileWriter output = new FileWriter(destFile, StandardCharsets.UTF_8)) {
            int i = 0;

            output.write("return new ImageIcon(new byte[] {");

            if ((i = input.read()) != -1) {
                output.write(Byte.toString((byte) i));
            }

            while ((i = input.read()) != -1) {
                output.write(",");
                output.write(Byte.toString((byte) i));
            }

            output.write("});");
        }
    }

    private IconConverter() {
        super();
    }
}
