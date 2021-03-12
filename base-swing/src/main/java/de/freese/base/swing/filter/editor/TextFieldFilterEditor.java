package de.freese.base.swing.filter.editor;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import de.freese.base.swing.components.text.AutoCompleteableTextField;
import de.freese.base.swing.fontchange.SwingFontSizeChanger;

/**
 * Klasse stellt ein {@link JTextField} als Filter bereit.
 *
 * @author Thomas Freese
 */
public class TextFieldFilterEditor extends AutoCompleteableTextField implements FilterEditor, DocumentListener
{
    /**
     * 
     */
    private static final long serialVersionUID = -1202264255047823398L;

    /**
     * 
     */
    private final int column;

    /**
     * Erstellt ein neues {@link TextFieldFilterEditor} Objekt.
     * 
     * @param column int
     */
    public TextFieldFilterEditor(final int column)
    {
        super();

        this.column = column;
        getDocument().addDocumentListener(this);

        SwingFontSizeChanger.getInstance().register(this);
    }

    /**
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
     */
    @Override
    public void changedUpdate(final DocumentEvent e)
    {
        firePropertyChange(getFilterPropertyName(), null, getText());
    }

    /**
     * @see de.freese.base.swing.filter.editor.FilterEditor#getColumn()
     */
    @Override
    public int getColumn()
    {
        return this.column;
    }

    /**
     * @see de.freese.base.swing.filter.editor.FilterEditor#getComponent()
     */
    @Override
    public JComponent getComponent()
    {
        return this;
    }

    /**
     * @see de.freese.base.swing.filter.editor.FilterEditor#getFilterPropertyName()
     */
    @Override
    public String getFilterPropertyName()
    {
        return getClass().getSimpleName();
    }

    /**
     * @see de.freese.base.swing.filter.editor.FilterEditor#getValue()
     */
    @Override
    public Object getValue()
    {
        return getText();
    }

    /**
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
     */
    @Override
    public void insertUpdate(final DocumentEvent e)
    {
        firePropertyChange(getFilterPropertyName(), null, getText());
    }

    /**
     * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
     */
    @Override
    public void removeUpdate(final DocumentEvent e)
    {
        firePropertyChange(getFilterPropertyName(), null, getText());
    }

    /**
     * @see de.freese.base.swing.filter.editor.FilterEditor#setValue(java.lang.Object)
     */
    @Override
    public void setValue(final Object value)
    {
        String newText = value == null ? null : value.toString();

        setText(newText);
    }
}
