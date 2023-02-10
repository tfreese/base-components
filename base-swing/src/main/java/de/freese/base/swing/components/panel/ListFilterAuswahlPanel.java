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
        if (this.filterLabel == null) {
            this.filterLabel = new JLabel("#Filter");
        }

        return this.filterLabel;
    }

    public JTextField getFilterTextField() {
        if (this.filterTextField == null) {
            this.filterTextField = new JTextField(16);
        }

        return this.filterTextField;
    }

    public JList<T> getList() {
        if (this.list == null) {
            this.list = new JList<>();
        }

        return this.list;
    }

    public JScrollPane getScrollPane() {
        if (this.scrollPane == null) {
            this.scrollPane = new JScrollPane(getList());
        }

        return this.scrollPane;
    }

    public void setFilter(final Predicate filter) {
        DocumentListener documentListener = (DocumentListener) filter;

        getFilterTextField().getDocument().addDocumentListener(documentListener);
    }

    public void setFilterVisible(final boolean value) {
        getFilterLabel().setVisible(value);
        getFilterTextField().setVisible(value);
    }
}
