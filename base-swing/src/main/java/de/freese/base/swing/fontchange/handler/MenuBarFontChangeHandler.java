package de.freese.base.swing.fontchange.handler;

import java.awt.Font;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

/**
 * Defaultimplementierung fuer die Fontaenderung einer {@link JMenuBar}.<br>
 * Zusaetzlich werden die {@link JMenu} und {@link JMenuItem} Elemente angepasst.
 *
 * @author Thomas Freese
 */
public class MenuBarFontChangeHandler extends ComponentFontChangeHandler
{
    /**
     * @see de.freese.base.swing.fontchange.handler.ComponentFontChangeHandler#fontChanged(java.awt.Font, java.lang.Object)
     */
    @Override
    public void fontChanged(final Font newFont, final Object object)
    {
        super.fontChanged(newFont, object);

        JMenuBar menuBar = (JMenuBar) object;

        // JMenu der JMenuBar
        for (int i = 0; i < menuBar.getMenuCount(); i++)
        {
            JMenu menu = menuBar.getMenu(i);
            super.fontChanged(newFont, menu);

            // JMenuItem des JMenu's
            for (int j = 0; j < menu.getItemCount(); j++)
            {
                JMenuItem menuItem = menu.getItem(j);

                if (menuItem == null)
                {
                    continue;
                }

                super.fontChanged(newFont, menuItem);

                // SubMenus des JMenuItem's
                for (MenuElement menuElement : menuItem.getSubElements())
                {
                    if (menuElement instanceof JPopupMenu popupMenu)
                    {
                        for (MenuElement subMenuElement : popupMenu.getSubElements())
                        {
                            super.fontChanged(newFont, subMenuElement);
                        }
                    }
                }
            }
        }
    }
}
