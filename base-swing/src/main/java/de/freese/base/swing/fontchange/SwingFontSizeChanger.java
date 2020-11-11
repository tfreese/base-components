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
 * Aendert bei allen registrierten Componenten den Font.
 *
 * @author Thomas Freese
 */
public final class SwingFontSizeChanger
{
    /**
     *
     */
    private static SwingFontSizeChanger INSTANCE = new SwingFontSizeChanger();

    /**
     * Liefert die Instanz.
     *
     * @return {@link SwingFontSizeChanger}
     */
    public static final SwingFontSizeChanger getInstance()
    {
        return INSTANCE;
    }

    /**
     * Verknuepft ein oder mehrere Objecte mit einem Listener, der bei Font-Aenderungen reagiert.
     *
     * @param object Object
     * @param others Object[]
     */
    public static void register(final Object object, final Object...others)
    {
        getInstance().register(object);

        for (Object other : others)
        {
            getInstance().register(other);
        }
    }

    /**
     *
     */
    private Font font;

    /**
     *
     */
    private final Map<Class<?>, FontChangeHandler> handler = new HashMap<>();

    /**
     *
     */
    private final PropertyChangeSupport propertyChangeSupport;

    /**
     * Erstellt ein neues {@link SwingFontSizeChanger} Object.
     */
    private SwingFontSizeChanger()
    {
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

    /**
     * Hinzufuegen eines neuen Handlers fuer eine {@link Component}.
     *
     * @param componenClass {@link Class}
     * @param handler {@link FontChangeHandler}
     */
    private void addFontChangeHandler(final Class<?> componenClass, final FontChangeHandler handler)
    {
        this.handler.put(componenClass, handler);
    }

    /**
     * Hinzufuegen eines neuen Listeners.
     *
     * @param listener {@link PropertyChangeListener}
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener)
    {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Liefert den aktuellen Font.
     *
     * @return {@link Font}
     */
    public Font getFont()
    {
        return this.font;
    }

    /**
     * Liefert die Fontfamilie.
     *
     * @return String
     */
    public String getFontFamily()
    {
        return this.font.getFamily();
    }

    /**
     * Liefert die Fontgroesse.
     *
     * @return int
     */
    public int getFontSize()
    {
        return this.font.getSize();
    }

    /**
     * Liefert die Fontart.
     *
     * @return int; z.B. Font.PLAIN
     */
    public int getFontStyle()
    {
        return this.font.getStyle();
    }

    /**
     * Verknuepft ein Object mit einem Listener, der bei Font-Aenderungen reagiert.
     *
     * @param object Object
     */
    public void register(final Object object)
    {
        PropertyChangeListener fontListener = event -> {
            if (object instanceof JComponent)
            {
                updateFontForComponent(getFont(), (JComponent) object);
            }
            else if (object instanceof Container)
            {
                updateFontForContainer(getFont(), (Container) object);
            }
            else
            {
                updateFontForObject(getFont(), object);
            }
        };

        addPropertyChangeListener(fontListener);
    }

    /**
     * Entfernen eines neuen Listeners.
     *
     * @param listener {@link PropertyChangeListener}
     */
    public void removePropertyChangeListener(final PropertyChangeListener listener)
    {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Setzt den neuen Font.
     *
     * @param font {@link Font}
     */
    public void setFont(final Font font)
    {
        Font oldFont = this.font;
        this.font = font;

        this.propertyChangeSupport.firePropertyChange("font", oldFont, this.font);

        // UI-Konstanten anpassen fuer neue Komponenten.
        UICustomization.setDefaultFont(font);
    }

    /**
     * Setzt eine neue Fontfamilie.
     *
     * @param fontFamily String
     * @deprecated Entfaellt
     */
    @Deprecated
    protected void setFontFamily(final String fontFamily)
    {
        if (getFontFamily().equals(fontFamily))
        {
            return;
        }

        Font newFont = new Font(fontFamily, this.font.getStyle(), this.font.getSize());

        setFont(newFont);
    }

    /**
     * Setzt eine neue Fontgroesse.
     *
     * @param fontSize float
     */
    public void setFontSize(final float fontSize)
    {
        if (getFontSize() == fontSize)
        {
            return;
        }

        // System.out.println(fontSize);

        Font newFont = this.font.deriveFont(fontSize);

        setFont(newFont);
    }

    /**
     * Setzt die Art des Fonts.
     *
     * @param fontStyle int; {zB Font.PLAIN}
     * @deprecated Entfaellt
     */
    @Deprecated
    protected void setFontStyle(final int fontStyle)
    {
        if (getFontStyle() == fontStyle)
        {
            return;
        }

        Font newFont = this.font.deriveFont(fontStyle);

        setFont(newFont);
    }

    /**
     * Aktualisiert eine {@link Component} mit einem neuen Font.
     *
     * @param newFont {@link Font}
     * @param component {@link JComponent}
     */
    private void updateFontForComponent(final Font newFont, final JComponent component)
    {
        updateFontForObject(newFont, component);
    }

    /**
     * Aktualisiert einen {@link Container} mit einem neuen Font.
     *
     * @param newFont {@link Font}
     * @param container {@link Container}
     */
    private void updateFontForContainer(final Font newFont, final Container container)
    {
        updateFontForObject(newFont, container);

        Component[] components = container.getComponents();

        for (Component comp : components)
        {
            if (comp instanceof JComponent)
            {
                updateFontForComponent(newFont, (JComponent) comp);
            }
            else if (comp instanceof Container)
            {
                updateFontForContainer(newFont, (Container) comp);
            }
            else
            {
                updateFontForObject(newFont, comp);
            }
        }
    }

    /**
     * Setzt in ein Object einem neuen Font.
     *
     * @param newFont {@link Font}
     * @param object Object
     */
    private void updateFontForObject(final Font newFont, final Object object)
    {
        Class<?> clazz = object.getClass();
        FontChangeHandler handler = null;

        while (clazz != Object.class)
        {
            handler = this.handler.get(clazz);

            if (handler != null)
            {
                break;
            }

            clazz = clazz.getSuperclass();
        }

        if (handler == null)
        {
            throw new NullPointerException("Handler not found for " + object.getClass().getName());
        }

        handler.fontChanged(newFont, object);
    }
}
