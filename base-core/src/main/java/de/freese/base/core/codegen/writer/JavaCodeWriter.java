/**
 * Created: 29.07.2018
 */

package de.freese.base.core.codegen.writer;

/**
 * Java-Implementierung eines {@link CodeWriter}.
 *
 * @author Thomas Freese
 */
public class JavaCodeWriter extends AbstractCodeWriter
{
    /**
     * Erstellt ein neues {@link JavaCodeWriter} Object.
     */
    public JavaCodeWriter()
    {
        super();
    }

    /**
     * @see de.freese.base.core.codegen.writer.AbstractCodeWriter#getFileExtension()
     */
    @Override
    public String getFileExtension()
    {
        return ".java";
    }
}