package de.freese.base.swing.components.combobox;

import java.awt.Component;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComboBox.KeySelectionManager;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.text.JTextComponent;

/**
 * KeySelectionManager einer ComboBox. Dieser sucht auf Tastendruck einen Eintrag aus der ComboBox, der mit einer eingegebenen Zeichenfolge übereinstimmt. Diese
 * Zeichenfolge wird mit dem Wert aus dem {@link ListCellRenderer} verglichen. Des Weiteren kann nach Zeichenketten in der ComboBox gesucht werden, in dem
 * innerhalb kurzer Zeit aufeinanderfolgende Tastendrücke erfolgen.
 *
 * @author Thomas Freese
 */
public class RendererKeySelectionManager implements KeySelectionManager {
    @SuppressWarnings("rawtypes")
    private final JComboBox comboBox;
    private final JList<Object> list = new JList<>();

    private long lastKeyTime;
    private String pattern = "";

    public RendererKeySelectionManager(final JComboBox<?> comboBox) {
        super();

        this.comboBox = comboBox;
    }

    @Override
    public int selectionForKey(final char key, final ComboBoxModel<?> model) {
        final int selectedIndex = getSelectedIndex(model);

        setPattern(key);

        int resultIndex = search(model, selectedIndex + 1, model.getSize());

        if (resultIndex != -1) {
            return resultIndex;
        }

        resultIndex = search(model, 0, selectedIndex);

        return resultIndex;
    }

    private int getSelectedIndex(final ComboBoxModel<?> model) {
        final Object selectedObject = model.getSelectedItem();

        if (selectedObject != null) {
            for (int i = 0; i < model.getSize(); i++) {
                if (selectedObject.equals(model.getElementAt(i))) {
                    return i;
                }
            }
        }

        return 0;
    }

    @SuppressWarnings("unchecked")
    private String getString(final int row) {
        if (list.getModel() != comboBox.getModel()) {
            // JList wird nur für den ComboBoxRenderer gebraucht
            list.setModel(comboBox.getModel());
        }

        final Object value = comboBox.getModel().getElementAt(row);

        final Component rendererComponent = comboBox.getRenderer().getListCellRendererComponent(list, value, row, false, false);

        final String text = switch (rendererComponent) {
            case JLabel l -> l.getText();
            case JTextComponent tc -> tc.getText();
            default -> value.toString();
        };

        return text.toLowerCase();
    }

    /**
     * Sucht einen Eintrag aus dem {@link ComboBoxModel} in einem bestimmten Bereich.
     *
     * @param fromIndex Startindex(inklusive)
     * @param endIndex Endindex (exklusive)
     *
     * @return wenn ein Eintrag gefunden der Zeilenindex, sonst -1
     */
    private int search(final ComboBoxModel<?> model, final int fromIndex, final int endIndex) {
        // Search from top to current selection
        for (int i = fromIndex; i < endIndex; i++) {
            if (model.getElementAt(i) != null) {
                final String s = getString(i);

                if (s.startsWith(pattern)) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * Bestimmt die Zeichenfolge, nach der im {@link ComboBoxModel} gesucht werden soll. Wenn innerhalb von 250 ms nach einem Tastendruck erneut eine Taste
     * gedrückt wird, so wird das Tastaturzeichen an die bestehende Zeichenfolge angehängt.
     */
    private void setPattern(final char aKey) {
        final long curTime = System.currentTimeMillis();

        // If the last key was typed less than 250 ms ago, append to the current pattern.
        if ((curTime - lastKeyTime) < 250) {
            pattern += ("" + aKey).toLowerCase();
        }
        else {
            pattern = ("" + aKey).toLowerCase();
        }

        // Save current time.
        lastKeyTime = curTime;
    }
}
