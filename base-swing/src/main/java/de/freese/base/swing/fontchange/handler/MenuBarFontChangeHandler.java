package de.freese.base.swing.fontchange.handler;

import java.awt.Font;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

/**
 * @author Thomas Freese
 */
public class MenuBarFontChangeHandler extends ComponentFontChangeHandler {
    @Override
    public void fontChanged(final Font newFont, final Object object) {
        super.fontChanged(newFont, object);

        final JMenuBar menuBar = (JMenuBar) object;

        // JMenu
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            final JMenu menu = menuBar.getMenu(i);
            super.fontChanged(newFont, menu);

            // JMenuItem
            for (int j = 0; j < menu.getItemCount(); j++) {
                final JMenuItem menuItem = menu.getItem(j);

                if (menuItem == null) {
                    continue;
                }

                super.fontChanged(newFont, menuItem);

                // SubMenus
                for (MenuElement menuElement : menuItem.getSubElements()) {
                    if (menuElement instanceof JPopupMenu popupMenu) {
                        for (MenuElement subMenuElement : popupMenu.getSubElements()) {
                            super.fontChanged(newFont, subMenuElement);
                        }
                    }
                }
            }
        }
    }
}
