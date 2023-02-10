package de.freese.base.swing.fontchange;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTitledPanel;

import de.freese.base.swing.fontchange.handler.ComboBoxFontChangeHandler;
import de.freese.base.swing.fontchange.handler.ComponentFontChangeHandler;
import de.freese.base.swing.fontchange.handler.DatePickerFontChangeHandler;
import de.freese.base.swing.fontchange.handler.ListFontChangeHandler;
import de.freese.base.swing.fontchange.handler.MenuBarFontChangeHandler;
import de.freese.base.swing.fontchange.handler.TableFontChangeHandler;
import de.freese.base.swing.fontchange.handler.TextComponentFontChangeHandler;
import de.freese.base.swing.fontchange.handler.TitledBorderFontChangeHandler;
import de.freese.base.swing.fontchange.handler.TitledPanelFontChangeHandler;
import de.freese.base.swing.fontchange.handler.TreeFontChangeHandler;
import de.freese.base.utils.UICustomization;

/**
 * @author Thomas Freese
 */
public final class SwingFontSizeChanger {
    private static final SwingFontSizeChanger INSTANCE = new SwingFontSizeChanger();

    public static SwingFontSizeChanger getInstance() {
        return INSTANCE;
    }

    /**
     * Verknüpft ein oder mehrere Objekte mit einem Listener, der bei Font-Änderungen reagiert.
     */
    public static void register(final Object object, final Object... others) {
        getInstance().register(object);

        for (Object other : others) {
            getInstance().register(other);
        }
    }

    private final Map<Class<?>, FontChangeHandler> handlers = new HashMap<>();

    private final PropertyChangeSupport propertyChangeSupport;

    private Font font;

    private SwingFontSizeChanger() {
        super();

        this.propertyChangeSupport = new PropertyChangeSupport(this);

        // DefaultFont
        // this.font = new Font("Arial", Font.PLAIN, 11);
        // this.font = new Font("Dialog", Font.PLAIN, 11);
        this.font = new JLabel().getFont();

        // Handlers
        addFontChangeHandler(Component.class, new ComponentFontChangeHandler());
        addFontChangeHandler(JTable.class, new TableFontChangeHandler());
        addFontChangeHandler(JTree.class, new TreeFontChangeHandler());
        addFontChangeHandler(JList.class, new ListFontChangeHandler());
        addFontChangeHandler(JComboBox.class, new ComboBoxFontChangeHandler());
        addFontChangeHandler(JTextField.class, new TextComponentFontChangeHandler());
        addFontChangeHandler(JTextArea.class, new TextComponentFontChangeHandler());
        addFontChangeHandler(JTextPane.class, new TextComponentFontChangeHandler());
        addFontChangeHandler(JEditorPane.class, new TextComponentFontChangeHandler());
        addFontChangeHandler(JXTitledPanel.class, new TitledPanelFontChangeHandler());
        addFontChangeHandler(TitledBorder.class, new TitledBorderFontChangeHandler());
        addFontChangeHandler(JMenuBar.class, new MenuBarFontChangeHandler());
        addFontChangeHandler(JXDatePicker.class, new DatePickerFontChangeHandler());
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public Font getFont() {
        return this.font;
    }

    public String getFontFamily() {
        return this.font.getFamily();
    }

    public int getFontSize() {
        return this.font.getSize();
    }

    /**
     * @return int; z.B. Font.PLAIN
     */
    public int getFontStyle() {
        return this.font.getStyle();
    }

    /**
     * Verknüpft ein Objekt mit einem Listener, der bei Font-Änderungen reagiert.
     */
    public void register(final Object object) {
        PropertyChangeListener fontListener = event -> {
            if (object instanceof JComponent c) {
                updateFontForComponent(getFont(), c);
            }
            else if (object instanceof Container c) {
                updateFontForContainer(getFont(), c);
            }
            else {
                updateFontForObject(getFont(), object);
            }
        };

        addPropertyChangeListener(fontListener);
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void setFont(final Font font) {
        Font oldFont = this.font;
        this.font = font;

        this.propertyChangeSupport.firePropertyChange("font", oldFont, this.font);

        // UI-Konstanten anpassen für neue Komponenten.
        UICustomization.setDefaultFont(font);
    }

    public void setFontSize(final float fontSize) {
        if (getFontSize() == fontSize) {
            return;
        }

        Font newFont = this.font.deriveFont(fontSize);

        setFont(newFont);
    }

    // @Deprecated
    // private void setFontFamily(final String fontFamily)
    // {
    // if (getFontFamily().equals(fontFamily))
    // {
    // return;
    // }
    //
    // Font newFont = new Font(fontFamily, this.font.getStyle(), this.font.getSize());
    //
    // setFont(newFont);
    // }

    private void addFontChangeHandler(final Class<?> componentClass, final FontChangeHandler handler) {
        this.handlers.put(componentClass, handler);
    }

    // @Deprecated
    // private void setFontStyle(final int fontStyle)
    // {
    // if (getFontStyle() == fontStyle)
    // {
    // return;
    // }
    //
    // Font newFont = this.font.deriveFont(fontStyle);
    //
    // setFont(newFont);
    // }

    private void updateFontForComponent(final Font newFont, final JComponent component) {
        updateFontForObject(newFont, component);
    }

    private void updateFontForContainer(final Font newFont, final Container container) {
        updateFontForObject(newFont, container);

        Component[] components = container.getComponents();

        for (Component comp : components) {
            if (comp instanceof JComponent c) {
                updateFontForComponent(newFont, c);
            }
            else if (comp instanceof Container c) {
                updateFontForContainer(newFont, c);
            }
            else {
                updateFontForObject(newFont, comp);
            }
        }
    }

    private void updateFontForObject(final Font newFont, final Object object) {
        Class<?> clazz = object.getClass();
        FontChangeHandler handler = null;

        while (clazz != Object.class) {
            handler = this.handlers.get(clazz);

            if (handler != null) {
                break;
            }

            clazz = clazz.getSuperclass();
        }

        if (handler == null) {
            throw new NullPointerException("Handler not found for " + object.getClass().getName());
        }

        handler.fontChanged(newFont, object);
    }
}
