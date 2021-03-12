package de.freese.base.swing.fontchange;

import java.awt.Font;

/**
 * Interface fuer eine Klasse zum aendern eines Fonts eine Objectes.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface FontChangeHandler
{
    /**
     * @param newFont {@link Font}
     * @param object Object
     */
    public void fontChanged(Font newFont, Object object);
}
