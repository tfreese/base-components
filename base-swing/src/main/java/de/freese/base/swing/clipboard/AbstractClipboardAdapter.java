// Created: 06.10.2008
package de.freese.base.swing.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.base.swing.clipboard.converter.BooleanClipboardConverter;
import de.freese.base.swing.clipboard.converter.DoubleClipBoardConverter;
import de.freese.base.swing.clipboard.converter.FloatClipBoardConverter;
import de.freese.base.swing.clipboard.converter.IntegerClipBoardConverter;
import de.freese.base.swing.clipboard.converter.LongClipBoardConverter;
import de.freese.base.swing.clipboard.converter.ReflectionClipboardConverter;
import de.freese.base.swing.clipboard.converter.StringClipboardConverter;

/**
 * BasisAdapter einer Komponente fuer die Zwischenablage.
 *
 * @author Thomas Freese
 */
public abstract class AbstractClipboardAdapter
{
    /**
     * Action zum kopieren der Daten.
     *
     * @author Thomas Freese
     */
    protected class ActionCopy extends AbstractAction
    {
        /**
         *
         */
        private static final long serialVersionUID = -6829341578505146619L;

        /**
         * Creates a new {@link ActionCopy} object.
         */
        public ActionCopy()
        {
            super();

            putValue(NAME, "Copy");
            putValue(SHORT_DESCRIPTION, "Copy");
            putValue(LONG_DESCRIPTION, "Copy");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(final ActionEvent e)
        {
            doCopy();
        }
    }

    /**
     * Action zum einfuegen der Daten.
     *
     * @author Thomas Freese
     */
    protected class ActionPaste extends AbstractAction
    {
        /**
         *
         */
        private static final long serialVersionUID = 4473841629940450442L;

        /**
         * Creates a new {@link ActionPaste} object.
         */
        public ActionPaste()
        {
            super();

            putValue(NAME, "Paste");
            putValue(SHORT_DESCRIPTION, "Paste");
            putValue(LONG_DESCRIPTION, "Paste");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(final ActionEvent e)
        {
            doPaste(false);
        }
    }

    /**
     * Action zum einfuegen der Daten mit gedrehten Achsen.
     *
     * @author Thomas Freese
     */
    protected class ActionPasteFlipAxes extends AbstractAction
    {
        /**
         *
         */
        private static final long serialVersionUID = 6114190778366220106L;

        /**
         * Creates a new {@link ActionPasteFlipAxes} object.
         */
        public ActionPasteFlipAxes()
        {
            super();

            putValue(NAME, "Paste (flip Axes)");
            putValue(SHORT_DESCRIPTION, "Paste (flip Axes)");
            putValue(LONG_DESCRIPTION, "Paste (flip Axes)");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(final ActionEvent e)
        {
            doPaste(true);
        }
    }

    /**
     *
     */
    private Action actionCopy;

    /**
     *
     */
    private Action actionPaste;

    /**
     *
     */
    private Action actionPasteFlipAxes;

    /**
     *
     */
    private final Clipboard clipboard;

    /**
     *
     */
    private final JComponent component;

    /**
     *
     */
    private final Map<Class<?>, ClipboardConverter> converterMap = new HashMap<>();

    /**
     *
     */
    private boolean enabled = true;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Creates a new {@link AbstractClipboardAdapter} object.
     *
     * @param component {@link JComponent}
     */
    protected AbstractClipboardAdapter(final JComponent component)
    {
        super();

        this.component = component;
        this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        this.component.putClientProperty(getClass(), this);
    }

    /**
     * Kopiert Daten aus der Komponente in die Zwischenablage.
     */
    public abstract void doCopy();

    /**
     * Kopiert Daten aus der Zwischenablage in die Komponente.
     *
     * @param flipAxes boolean, gedrehte Achsen oder nicht
     */
    public abstract void doPaste(boolean flipAxes);

    /**
     * Action zum kopieren der Daten.
     *
     * @return {@link Action}
     */
    public Action getActionCopy()
    {
        if (this.actionCopy == null)
        {
            this.actionCopy = new ActionCopy();
        }

        return this.actionCopy;
    }

    /**
     * Action zum einfuegen der Daten.
     *
     * @return {@link Action}
     */
    public Action getActionPaste()
    {
        if (this.actionPaste == null)
        {
            this.actionPaste = new ActionPaste();
        }

        return this.actionPaste;
    }

    /**
     * Action zum einfuegen der Daten mit gedrehten Achsen.
     *
     * @return {@link Action}
     */
    public Action getActionPasteFlipAxes()
    {
        if (this.actionPasteFlipAxes == null)
        {
            this.actionPasteFlipAxes = new ActionPasteFlipAxes();
        }

        return this.actionPasteFlipAxes;
    }

    /**
     * Liefert das Objekt der Zwischenbalage.
     *
     * @return {@link Clipboard}
     */
    protected Clipboard getClipboard()
    {
        return this.clipboard;
    }

    /**
     * Liefert die adaptierte Komponente.
     *
     * @return {@link JComponent}
     */
    protected JComponent getComponent()
    {
        return this.component;
    }

    /**
     * Liefert den Konverter für die Klasse.<br>
     * Ist kein Konverter registriert wird der {@link ReflectionClipboardConverter} registriert und geliefert.
     *
     * @param clazz Class
     * @return {@link ClipboardConverter}
     */

    public ClipboardConverter getConverter(final Class<?> clazz)
    {
        ClipboardConverter converter = getConverterMap().get(clazz);

        if (converter == null)
        {
            converter = new ReflectionClipboardConverter(clazz);
            register(clazz, converter);

            getLogger().info("ReflectionClipboardConverter for {} registered.", clazz.getSimpleName());
        }

        return converter;
    }

    /**
     * Liefert die Map der ClipboardConverter.
     *
     * @return {@link Map}
     */
    private Map<Class<?>, ClipboardConverter> getConverterMap()
    {
        return this.converterMap;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Konfiguration der Komponente mit dem ClipboardAdapter.
     */
    protected void initialize()
    {
        registerDefaultConverters();

        KeyStroke copyKS = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);

        getComponent().registerKeyboardAction(getActionCopy(), "region-copy", copyKS, JComponent.WHEN_FOCUSED);

        KeyStroke pasteKS = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);

        getComponent().registerKeyboardAction(getActionPaste(), "region-paste", pasteKS, JComponent.WHEN_FOCUSED);
    }

    /**
     * Liefert true, wenn die Actions aktiviert sind.
     *
     * @return boolean
     */
    public boolean isEnabled()
    {
        return this.enabled;
    }

    /**
     * Registriert fuer eine Klasse den entsprechenden {@link ClipboardConverter}.
     *
     * @param clazz Class
     * @param converter {@link ClipboardConverter}
     */
    public void register(final Class<?> clazz, final ClipboardConverter converter)
    {
        getConverterMap().put(clazz, converter);
    }

    /**
     * Registrieren von Standartkonverter.<br>
     * Alle anderen Typen werden über {@link ReflectionClipboardConverter} gehandelt.<br>
     *
     * @see #getConverter(Class)
     */
    protected void registerDefaultConverters()
    {
        register(Boolean.class, new BooleanClipboardConverter());
        register(String.class, new StringClipboardConverter());

        // Numbers
        register(Integer.class, new IntegerClipBoardConverter());
        register(Long.class, new LongClipBoardConverter());
        register(Float.class, new FloatClipBoardConverter());
        register(Double.class, new DoubleClipBoardConverter());
    }

    /**
     * Aktiviert oder deaktiviert die Actions.
     *
     * @param enabled boolean
     */
    public void setEnabled(final boolean enabled)
    {
        this.enabled = enabled;

        getActionCopy().setEnabled(enabled);
        getActionPaste().setEnabled(enabled);
        getActionPasteFlipAxes().setEnabled(enabled);
    }
}
