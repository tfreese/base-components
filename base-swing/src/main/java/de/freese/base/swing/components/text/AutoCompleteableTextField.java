package de.freese.base.swing.components.text;

import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;

/**
 * TextField was eine Autovervollständigung bietet aus den vorherigen Eingaben.
 *
 * @author Thomas Freese
 */
public class AutoCompleteableTextField extends JTextField {
    @Serial
    private static final long serialVersionUID = 8765972663291526963L;

    /**
     * MenuAction um den darin enthaltenen Text in das Textfeld zu setzten.
     *
     * @author Thomas Freese
     */
    private class PrevSearchAction extends AbstractAction {
        @Serial
        private static final long serialVersionUID = -6115968950918667824L;

        private final String term;

        PrevSearchAction(final String term) {
            super();

            this.term = term;
            putValue(Action.NAME, this.term);
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            final Runnable runner = () -> {
                setText(PrevSearchAction.this.term);

                // getFilterTextField().setCaretPosition(term.length());
                requestFocus();
            };

            SwingUtilities.invokeLater(runner);
        }

        @Override
        public String toString() {
            return term;
        }
    }

    private final LinkedList<String> prevSearches = new LinkedList<>();

    private JPopupMenu prevSearchMenu;

    public AutoCompleteableTextField() {
        this(0);
    }

    public AutoCompleteableTextField(final int columns) {
        super(columns);

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent event) {
                if (AutoCompleteableTextField.this.prevSearchMenu == null || !AutoCompleteableTextField.this.prevSearchMenu.isVisible()) {
                    saveLastSearch();
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER || event.getKeyCode() == KeyEvent.VK_TAB) {
                    final JPopupMenu popupMenu = AutoCompleteableTextField.this.prevSearchMenu;

                    // Wenn das PopupMenu geöffnet ist, das selektierte MenuItem ausführen.
                    if (popupMenu != null && popupMenu.isVisible()) {
                        final MenuElement[] path = MenuSelectionManager.defaultManager().getSelectedPath();

                        if (path.length > 0) {
                            final JMenuItem menuItem = (JMenuItem) path[path.length - 1];

                            menuItem.doClick();
                            popupMenu.setVisible(false);
                        }

                        return;
                    }

                    // Bei Enter oder Tabulator den Fokus wechseln und so den neuen Text zu Vervollständigung aufnehmen.
                    transferFocus();
                }
                else if (Character.isJavaIdentifierPart(event.getKeyChar())) {
                    // Popup einblenden
                    popupMenu(0, (int) getSize().getHeight());
                }
            }
        });
    }

    private void popupMenu(final int x, final int y) {
        if (prevSearchMenu != null) {
            prevSearchMenu.setVisible(false);
            prevSearchMenu.removeAll();
            prevSearchMenu = null;
        }

        if (!prevSearches.isEmpty() && !getText().isBlank()) {
            prevSearchMenu = new JPopupMenu();

            final Iterator<String> it = prevSearches.iterator();
            final List<String> matches = new ArrayList<>();

            // Treffer herausfinden
            while (it.hasNext()) {
                final String search = it.next();

                if (search.contains(getText().strip())) {
                    matches.add(search);
                }
            }

            // Treffer sortieren
            Collections.sort(matches);

            for (String element : matches) {
                final Action action = new PrevSearchAction(element);

                prevSearchMenu.add(action);
            }

            if (prevSearchMenu.getComponentCount() > 0) {
                prevSearchMenu.show(this, x, y);

                // Cursor wieder zurück ins Textfeld.
                // if (!hasFocus()) {
                requestFocus();

                // }
            }
        }
    }

    /**
     * Text im Textfeld in die Historie aufnehmen, max. 10 Einträge.
     */
    private void saveLastSearch() {
        final String search = getText().strip();

        if (search != null && search.length() > 1 && !prevSearches.contains(search)) {
            // System.out.println("AutoCompleteableTextField.saveLastSearch(): " + search);
            prevSearches.addFirst(search);

            if (prevSearches.size() > 10) {
                prevSearches.removeLast();
            }
        }
    }
}
