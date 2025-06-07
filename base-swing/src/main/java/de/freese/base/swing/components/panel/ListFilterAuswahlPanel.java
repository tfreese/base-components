package de.freese.base.swing.components.panel;

import java.io.Serial;
import java.util.function.Predicate;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentListener;

import de.freese.base.swing.components.list.renderer.VerticalWrapListCellRenderer;
import de.freese.base.swing.fontchange.SwingFontSizeChanger;
import de.freese.base.swing.ui.ThinHorizontalScrollBarUI;

/**
 * Panel with Filterable Text field in  the TitleBar.
 *
 * @author Thomas Freese
 */
public class ListFilterAuswahlPanel<T> extends ExtTitledPanel {
    @Serial
    private static final long serialVersionUID = 9023884779864134283L;

    private JLabel filterLabel;
    private JTextField filterTextField;
    private JList<T> list;
    private JScrollPane scrollPane;

    public ListFilterAuswahlPanel() {
        super();

        add(getScrollPane());

        addTitleComponentLeft(getFilterLabel());
        addTitleComponentLeft(getFilterTextField());

        SwingFontSizeChanger.register(this, getFilterLabel(), getFilterTextField(), getList());
    }

    /**
     * @param cellRenderer {@link ListCellRenderer}, optional Default is {@link VerticalWrapListCellRenderer}
     * @param fixedCellWidth int, 0 = Default-width 166
     */
    public void enableListVerticalWrap(final ListCellRenderer<T> cellRenderer, final int fixedCellWidth) {
        getList().setCellRenderer(new VerticalWrapListCellRenderer<>(cellRenderer));
        getList().setLayoutOrientation(JList.VERTICAL_WRAP);
        getList().setVisibleRowCount(0);
        getList().setFixedCellWidth(fixedCellWidth);

        getScrollPane().getHorizontalScrollBar().setUI(new ThinHorizontalScrollBarUI(9));
    }

    public JLabel getFilterLabel() {
        if (filterLabel == null) {
            filterLabel = new JLabel("#Filter");
        }

        return filterLabel;
    }

    public JTextField getFilterTextField() {
        if (filterTextField == null) {
            filterTextField = new JTextField(16);
        }

        return filterTextField;
    }

    public JList<T> getList() {
        if (list == null) {
            list = new JList<>();
        }

        return list;
    }

    public JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane(getList());
        }

        return scrollPane;
    }

    public void setFilter(final Predicate<T> filter) {
        final DocumentListener documentListener = (DocumentListener) filter;

        getFilterTextField().getDocument().addDocumentListener(documentListener);
    }

    public void setFilterVisible(final boolean value) {
        getFilterLabel().setVisible(value);
        getFilterTextField().setVisible(value);
    }
}
