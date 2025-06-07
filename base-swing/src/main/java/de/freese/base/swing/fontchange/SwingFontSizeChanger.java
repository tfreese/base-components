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

        propertyChangeSupport = new PropertyChangeSupport(this);

        // DefaultFont
        // font = new Font("Arial", Font.PLAIN, 11);
        // font = new Font("Dialog", Font.PLAIN, 11);
        font = new JLabel().getFont();

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
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public Font getFont() {
        return font;
    }

    public String getFontFamily() {
        return font.getFamily();
    }

    public int getFontSize() {
        return font.getSize();
    }

    /**
     * @return int; Font.PLAIN
     */
    public int getFontStyle() {
        return font.getStyle();
    }

    public void register(final Object object) {
        final PropertyChangeListener fontListener = event -> {
            switch (object) {
                case JComponent c -> updateFontForComponent(getFont(), c);
                case Container c -> updateFontForContainer(getFont(), c);
                default -> updateFontForObject(getFont(), object);
            }
        };

        addPropertyChangeListener(fontListener);
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void setFont(final Font font) {
        final Font oldFont = this.font;
        this.font = font;

        propertyChangeSupport.firePropertyChange("font", oldFont, this.font);

        UICustomization.setDefaultFont(font);
    }

    public void setFontSize(final float fontSize) {
        if (getFontSize() == (int) fontSize) {
            return;
        }

        final Font newFont = font.deriveFont(fontSize);

        setFont(newFont);
    }

    private void addFontChangeHandler(final Class<?> componentClass, final FontChangeHandler handler) {
        handlers.put(componentClass, handler);
    }

    private void updateFontForComponent(final Font newFont, final JComponent component) {
        updateFontForObject(newFont, component);
    }

    private void updateFontForContainer(final Font newFont, final Container container) {
        updateFontForObject(newFont, container);

        final Component[] components = container.getComponents();

        for (Component comp : components) {
            switch (comp) {
                case JComponent c -> updateFontForComponent(newFont, c);
                case Container c -> updateFontForContainer(newFont, c);
                default -> updateFontForObject(newFont, comp);
            }
        }
    }

    private void updateFontForObject(final Font newFont, final Object object) {
        Class<?> clazz = object.getClass();
        FontChangeHandler handler = null;

        while (clazz != Object.class) {
            handler = handlers.get(clazz);

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
