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
public final class IconConverter
{
    /**
     * Liest die Grafik aus der Quelldatei und generiert dafür Java-Quellcode in der Zieldatei
     *
     * @param source Der Name der Quelldatei
     * @param destination Der Name der Zieldatei
     *
     * @throws IOException Falls was schiefgeht.
     */
    public static void convert(final String source, final String destination) throws IOException
    {
        try (FileInputStream input = new FileInputStream(source);
             FileWriter output = new FileWriter(destination, StandardCharsets.UTF_8))
        {
            int i = 0;

            output.write("return new ImageIcon(new byte[] {");

            if ((i = input.read()) != -1)
            {
                output.write(Byte.toString((byte) i));
            }

            while ((i = input.read()) != -1)
            {
                output.write(",");
                output.write(Byte.toString((byte) i));
            }

            output.write("});");
        }
    }

    /**
     * Erstellt ein neues {@link IconConverter} Object.
     */
    private IconConverter()
    {
        super();
    }
}
