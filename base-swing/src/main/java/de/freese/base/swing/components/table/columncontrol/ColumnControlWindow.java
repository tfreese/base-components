package de.freese.base.swing.components.table.columncontrol;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.TableColumn;

import de.freese.base.swing.components.table.ExtTable;
import de.freese.base.swing.components.table.column.ExtTableColumn;
import de.freese.base.swing.components.table.column.ExtTableColumnModel;
import de.freese.base.swing.layout.VerticalLayout;

/**
 * Window des {@link ColumnControlButton}.
 *
 * @author Thomas Freese
 */
public class ColumnControlWindow implements AWTEventListener {
    private final ColumnControlButton controlButton;

    private JWindow window;

    public ColumnControlWindow(final ColumnControlButton controlButton) {
        super();

        this.controlButton = controlButton;
    }

    public void clear() {
        if (this.window != null) {
            this.window.setVisible(false);
            this.window.removeAll();
            this.window.dispose();
            this.window = null;
        }
    }

    /**
     * @see java.awt.event.AWTEventListener#eventDispatched(java.awt.AWTEvent)
     */
    @Override
    public void eventDispatched(final AWTEvent event) {
        if ((event.getID() == MouseEvent.MOUSE_EXITED)) {
            Component source = (Component) event.getSource();

            if (!isChild(source, getWindow()) && !(event.getSource() instanceof JWindow) && !event.getSource().equals(this.controlButton)) {
                hideAndClear();
            }
        }
    }

    public void fill() {
        JPanel panel = new JPanel();
        panel.setBorder(new CompoundBorder(null, new LineBorder(Color.DARK_GRAY, 1)));
        panel.setLayout(new VerticalLayout());
        // panel.add(new JLabel("Column control"));

        // Spalten der Tabellen
        for (ExtTable table : this.controlButton.getTables()) {
            ExtTableColumnModel tableColumnModelExt = (ExtTableColumnModel) table.getColumnModel();
            List<TableColumn> columns = tableColumnModelExt.getColumns(true);

            for (TableColumn tableColumn : columns) {
                if (!(tableColumn instanceof ExtTableColumn extTableColumn) || !extTableColumn.isVisibleChange()) {
                    continue;
                }

                JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem();
                menuItem.setSelected(extTableColumn.isVisible());
                menuItem.setText(tableColumn.getHeaderValue().toString());
                menuItem.addActionListener(e -> {
                    JCheckBoxMenuItem menuItem1 = (JCheckBoxMenuItem) e.getSource();

                    extTableColumn.setVisible(menuItem1.isSelected());
                });

                panel.add(menuItem);
            }
        }

        // ZusatzActions
        String packText = Locale.GERMAN.equals(Locale.getDefault()) ? "Spalten anpassen" : "Pack Columns";

        Action packAllAction = new AbstractAction(packText) {
            @Serial
            private static final long serialVersionUID = 7374736910757374058L;

            /**
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            @Override
            public void actionPerformed(final ActionEvent e) {
                for (ExtTable table : ColumnControlWindow.this.controlButton.getTables()) {
                    Action action = table.getActionMap().get("pack_all");

                    if (action != null) {
                        action.actionPerformed(e);
                    }
                }
            }
        };

        panel.add(new JMenuItem(packAllAction));

        getWindow().add(panel, BorderLayout.CENTER);
    }

    public void show() {
        getWindow().setVisible(true);

        Dimension windowSize = getWindow().getSize();

        int dX = this.controlButton.getComponentOrientation().isLeftToRight() ? (-windowSize.width) : 0;

        Point pt = this.controlButton.getLocationOnScreen();
        pt.translate(dX, 0);
        getWindow().setLocation(pt);
        getWindow().pack();
        getWindow().setVisible(true);
        getWindow().toFront();
        getWindow().requestFocusInWindow();

        Toolkit.getDefaultToolkit().addAWTEventListener(ColumnControlWindow.this, AWTEvent.MOUSE_EVENT_MASK);
    }

    protected Frame getFrame(final Component comp) {
        Component c = comp;

        if (c == null) {
            c = this.controlButton;
        }

        Component parent = c.getParent();

        if (parent instanceof Frame f) {
            return f;
        }

        return getFrame(parent);
    }

    protected JWindow getWindow() {
        if (this.window == null) {
            this.window = new JWindow(getFrame(this.controlButton));
            this.window.setLayout(new BorderLayout());
            this.window.setFocusableWindowState(false);

            this.window.addMouseListener(new MouseAdapter() {
                /**
                 * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
                 */
                @Override
                public void mouseExited(final MouseEvent e) {
                    hideAndClear();
                }
            });
            // this.window.addFocusListener(new FocusAdapter()
            // {
            // /**
            // * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
            // */
            // @Override
            // public void focusLost(final FocusEvent e)
            // {
            // hideAndClear();
            // }
            // });
        }

        return this.window;
    }

    protected void hideAndClear() {
        if (this.window != null) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(this);

            this.window.setVisible(false);
            clear();
        }
    }

    /**
     * Bestimmt, ob die übergebene Komponente in den Unterkomponenten vorkommt.
     *
     * @return <code>true</code> wenn vorhanden, sonst <code>false</code>
     */
    protected boolean isChild(final Component src, final Component component) {
        for (Component c = src; c != null; c = c.getParent()) {
            if (c.equals(component)) {
                return true;
            }
        }

        return false;
    }
}
