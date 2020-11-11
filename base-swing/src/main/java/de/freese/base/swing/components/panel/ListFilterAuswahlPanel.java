package de.freese.base.swing.components.panel;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentListener;
import de.freese.base.swing.components.list.renderer.VerticalWrapListCellRenderer;
import de.freese.base.swing.filter.Filter;
import de.freese.base.swing.fontchange.SwingFontSizeChanger;
import de.freese.base.swing.ui.ThinHorizontalScrollBarUI;

/**
 * Panel mit einem Filtertextfeld in der Titlebar.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public class ListFilterAuswahlPanel<T> extends ExtTitledPanel
{
    /**
     *
     */
    private static final long serialVersionUID = 9023884779864134283L;

    /**
     *
     */
    private JLabel filterLabel;

    /**
     *
     */
    private JTextField filterTextField;

    /**
     *
     */
    private JList<T> list;

    /**
     *
     */
    private JScrollPane scrollPane;

    /**
     * Erstellt ein neues {@link ListFilterAuswahlPanel} Object.
     */
    public ListFilterAuswahlPanel()
    {
        super();

        add(getScrollPane());

        addTitleComponentLeft(getFilterLabel());
        addTitleComponentLeft(getFilterTextField());

        SwingFontSizeChanger.register(this, getFilterLabel(), getFilterTextField(), getList());
    }

    /**
     * Aktiviert den vertikalen Umbruch.
     *
     * @param cellRenderer {@link ListCellRenderer}, optional Default ist {@link VerticalWrapListCellRenderer}
     * @param fixedCellWidth int, 0 = Defaultbreite von 166
     */
    public void enableListVerticalWrap(final ListCellRenderer<T> cellRenderer, final int fixedCellWidth)
    {
        getList().setCellRenderer(new VerticalWrapListCellRenderer<>(cellRenderer));
        getList().setLayoutOrientation(JList.VERTICAL_WRAP);
        getList().setVisibleRowCount(0);
        getList().setFixedCellWidth(fixedCellWidth);

        getScrollPane().getHorizontalScrollBar().setUI(new ThinHorizontalScrollBarUI(9));
    }

    /**
     * @return {@link JLabel}
     */
    public JLabel getFilterLabel()
    {
        if (this.filterLabel == null)
        {
            this.filterLabel = new JLabel("#Filter");
        }

        return this.filterLabel;
    }

    /**
     * @return {@link JTextField}
     */
    public JTextField getFilterTextField()
    {
        if (this.filterTextField == null)
        {
            this.filterTextField = new JTextField(16);
        }

        return this.filterTextField;
    }

    /**
     * @return {@link JList}
     */
    public JList<T> getList()
    {
        if (this.list == null)
        {
            this.list = new JList<>();
        }

        return this.list;
    }

    /**
     * Liefert die ScrollPane
     *
     * @return {@link JScrollPane}
     */
    public JScrollPane getScrollPane()
    {
        if (this.scrollPane == null)
        {
            this.scrollPane = new JScrollPane(getList());
        }

        return this.scrollPane;
    }

    /**
     * Setzt den Filter als {@link DocumentListener}.
     *
     * @param filter {@link Filter}
     */
    public void setFilter(final Filter filter)
    {
        DocumentListener documentListener = (DocumentListener) filter;

        getFilterTextField().getDocument().addDocumentListener(documentListener);
    }

    /**
     * Sichtbarmachung des Filters.
     *
     * @param value boolean
     */
    public void setFilterVisible(final boolean value)
    {
        getFilterLabel().setVisible(value);
        getFilterTextField().setVisible(value);
    }
}
