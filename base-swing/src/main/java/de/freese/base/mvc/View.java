package de.freese.base.mvc;

import java.awt.Component;

/**
 * Interface einer View.
 *
 * @author Thomas Freese
 */
public interface View
{
    void createGUI();

    Component getComponent();

    void handleException(Throwable throwable);

    void restoreState();

    void saveState();
}
