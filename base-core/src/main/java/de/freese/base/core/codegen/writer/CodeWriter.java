/**
 * Created: 26.07.2018
 */

package de.freese.base.core.codegen.writer;

import java.io.PrintStream;
import de.freese.base.core.codegen.model.ClassModel;

/**
 * Erzeugt aus den MetaDaten den Quellcode.
 *
 * @author Thomas Freese
 */
public interface CodeWriter
{
    /**
     * Liefert die Dateiendung.
     *
     * @return String
     */
    public abstract String getFileExtension();

    /**
     * Schreibt den Code einer Klasse.
     *
     * @param classModel {@link ClassModel}
     * @param output {@link PrintStream}
     * @throws Exception Falls was schief geht.
     */
    public void write(ClassModel classModel, PrintStream output) throws Exception;
}
