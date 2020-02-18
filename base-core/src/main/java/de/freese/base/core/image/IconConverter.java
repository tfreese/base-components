package de.freese.base.core.image;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Mit Hilfe dieser Klasse kann man eine Grafikdatei in Java-Quellcode konvertieren.
 *
 * @author Thomas Freese
 */
public class IconConverter
{
    /**
     * Liest die Grafik aus der Quelldatei und generiert dafür Java-Quellcode in der Zieldatei
     *
     * @param source Der Name der Quelldatei
     * @param destination Der Name der Zieldatei
     * @throws IOException Falls was schief geht.
     */
    public static void convert(final String source, final String destination) throws IOException
    {
        try (FileInputStream input = new FileInputStream(source);
             FileWriter output = new FileWriter(destination))
        {
            int i = 0;

            output.write("return new ImageIcon(new byte[] {");

            while ((i = input.read()) != -1)
            {
                output.write(Byte.toString((byte) i));
                output.write(",");
            }

            output.write("});");
        }
    }
}
