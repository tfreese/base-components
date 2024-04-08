// Created: 06.10.2008
package de.freese.base.swing.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.Serial;
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
 * BasisAdapter einer Komponente für die Zwischenablage.
 *
 * @author Thomas Freese
 */
public abstract class AbstractClipboardAdapter {
    /**
     * @author Thomas Freese
     */
    protected class ActionCopy extends AbstractAction {
        @Serial
        private static final long serialVersionUID = -6829341578505146619L;

        protected ActionCopy() {
            super();

            putValue(NAME, "Copy");
            putValue(SHORT_DESCRIPTION, "Copy");
            putValue(LONG_DESCRIPTION, "Copy");
        }

        @Override
        public void actionPerformed(final ActionEvent event) {
            doCopy();
        }
    }

    /**
     * @author Thomas Freese
     */
    protected class ActionPaste extends AbstractAction {
        @Serial
        private static final long serialVersionUID = 4473841629940450442L;

        protected ActionPaste() {
            super();

            putValue(NAME, "Paste");
            putValue(SHORT_DESCRIPTION, "Paste");
            putValue(LONG_DESCRIPTION, "Paste");
        }

        @Override
        public void actionPerformed(final ActionEvent event) {
            doPaste(false);
        }
    }

    /**
     * @author Thomas Freese
     */
    protected class ActionPasteFlipAxes extends AbstractAction {
        @Serial
        private static final long serialVersionUID = 6114190778366220106L;

        protected ActionPasteFlipAxes() {
            super();

            putValue(NAME, "Paste (flip Axes)");
            putValue(SHORT_DESCRIPTION, "Paste (flip Axes)");
            putValue(LONG_DESCRIPTION, "Paste (flip Axes)");
        }

        @Override
        public void actionPerformed(final ActionEvent event) {
            doPaste(true);
        }
    }

    private final Clipboard clipboard;
    private final JComponent component;
    private final Map<Class<?>, ClipboardConverter> converterMap = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Action actionCopy;
    private Action actionPaste;
    private Action actionPasteFlipAxes;
    private boolean enabled = true;

    protected AbstractClipboardAdapter(final JComponent component) {
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

    public Action getActionCopy() {
        if (this.actionCopy == null) {
            this.actionCopy = new ActionCopy();
        }

        return this.actionCopy;
    }

    public Action getActionPaste() {
        if (this.actionPaste == null) {
            this.actionPaste = new ActionPaste();
        }

        return this.actionPaste;
    }

    public Action getActionPasteFlipAxes() {
        if (this.actionPasteFlipAxes == null) {
            this.actionPasteFlipAxes = new ActionPasteFlipAxes();
        }

        return this.actionPasteFlipAxes;
    }

    /**
     * Liefert den Konverter für die Klasse.<br>
     * Ist kein Konverter registriert wird der {@link ReflectionClipboardConverter} registriert und geliefert.
     */

    public ClipboardConverter getConverter(final Class<?> clazz) {
        ClipboardConverter converter = getConverterMap().get(clazz);

        if (converter == null) {
            converter = new ReflectionClipboardConverter(clazz);
            register(clazz, converter);

            getLogger().info("ReflectionClipboardConverter for {} registered.", clazz.getSimpleName());
        }

        return converter;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void register(final Class<?> clazz, final ClipboardConverter converter) {
        getConverterMap().put(clazz, converter);
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;

        getActionCopy().setEnabled(enabled);
        getActionPaste().setEnabled(enabled);
        getActionPasteFlipAxes().setEnabled(enabled);
    }

    protected Clipboard getClipboard() {
        return this.clipboard;
    }

    protected JComponent getComponent() {
        return this.component;
    }

    protected Logger getLogger() {
        return this.logger;
    }

    protected void initialize() {
        registerDefaultConverters();

        final KeyStroke copyKS = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK, false);

        getComponent().registerKeyboardAction(getActionCopy(), "region-copy", copyKS, JComponent.WHEN_FOCUSED);

        final KeyStroke pasteKS = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK, false);

        getComponent().registerKeyboardAction(getActionPaste(), "region-paste", pasteKS, JComponent.WHEN_FOCUSED);
    }

    /**
     * Registrieren von Standartkonverter.<br>
     * Alle anderen Typen werden über {@link ReflectionClipboardConverter} gehandelt.<br>
     */
    protected void registerDefaultConverters() {
        register(Boolean.class, new BooleanClipboardConverter());
        register(String.class, new StringClipboardConverter());

        // Numbers
        register(Integer.class, new IntegerClipBoardConverter());
        register(Long.class, new LongClipBoardConverter());
        register(Float.class, new FloatClipBoardConverter());
        register(Double.class, new DoubleClipBoardConverter());
    }

    private Map<Class<?>, ClipboardConverter> getConverterMap() {
        return this.converterMap;
    }
}
