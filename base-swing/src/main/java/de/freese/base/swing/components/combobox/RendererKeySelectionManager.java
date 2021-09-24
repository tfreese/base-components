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
 * KeySelectionManager einer ComboBox. Dieser sucht auf Tastendruck einen Eintrag aus der Combobox, der mit einer eingegebenen Zeichfolge uebereinstimmt. Diese
 * Zeichenfolge wird mit dem Wert aus dem {@link ListCellRenderer} verglichen. Des Weiteren kann nach Zeichenketten in der ComboBox gesucht werden, in dem
 * innerhalb kurzer Zeit aufeinanderfolgende Tastendruecke erfolgen.
 *
 * @author Thomas Freese
 */
public class RendererKeySelectionManager implements KeySelectionManager
{
    /**
     *
     */
    @SuppressWarnings("rawtypes")
    private final JComboBox comboBox;
    /**
     * Zeitpunkt, der letzten Suche (des Tastendrucks)
     */
    private long lastKeyTime;
    /**
     * JList temporaere Liste zum Rendern des Inhalts
     */
    private JList<Object> list = new JList<>();
    /**
     * Pattern auf das ueberprueft wird.
     */
    private String pattern = "";

    /**
     * Erstellt ein neues {@link RendererKeySelectionManager} Objekt.
     *
     * @param comboBox {@link JComboBox}
     */
    public RendererKeySelectionManager(final JComboBox<?> comboBox)
    {
        super();

        this.comboBox = comboBox;
    }

    /**
     * Bestimmt das aktuell gewaehlte Objekt in der Combobox
     *
     * @param model {@link ComboBoxModel}
     *
     * @return Index des selektierten Objekts, sonst 0
     */
    private int getSelectedIndex(final ComboBoxModel<?> model)
    {
        Object selectedObject = model.getSelectedItem();

        if (selectedObject != null)
        {
            for (int i = 0; i < model.getSize(); i++)
            {
                if (selectedObject.equals(model.getElementAt(i)))
                {
                    return i;
                }
            }
        }

        return 0;
    }

    /**
     * Liefert den String aus dem {@link ListCellRenderer} der Zeile.
     *
     * @param row Zeileinindex
     *
     * @return String
     */
    @SuppressWarnings("unchecked")
    private String getString(final int row)
    {
        if (this.list.getModel() != this.comboBox.getModel())
        {
            // JList wird nur fuer den ComboBoxRenderer gebraucht
            this.list.setModel(this.comboBox.getModel());
        }

        Object value = this.comboBox.getModel().getElementAt(row);

        Component rendererComponent = this.comboBox.getRenderer().getListCellRendererComponent(this.list, value, row, false, false);

        String text = null;

        if (rendererComponent instanceof JLabel l)
        {
            text = l.getText();
        }
        else if (rendererComponent instanceof JTextComponent tc)
        {
            text = tc.getText();
        }
        else
        {
            text = value.toString();
        }

        return text.toLowerCase();
    }

    /**
     * Sucht einen Eintrag aus dem {@link ComboBoxModel} in einem bestimmten Bereich.
     *
     * @param model {@link ComboBoxModel}
     * @param fromIndex Startindex(inklusive)
     * @param endIndex Endindex (exklusive)
     *
     * @return wenn ein Eintrag gefunden der Zeilenindex, sonst -1
     */
    private int search(final ComboBoxModel<?> model, final int fromIndex, final int endIndex)
    {
        // Search from top to current selection
        for (int i = fromIndex; i < endIndex; i++)
        {
            if (model.getElementAt(i) != null)
            {
                String s = getString(i);

                if (s.startsWith(this.pattern))
                {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * @see javax.swing.JComboBox.KeySelectionManager#selectionForKey(char, javax.swing.ComboBoxModel)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public int selectionForKey(final char key, final ComboBoxModel model)
    {
        int selectedIndex = getSelectedIndex(model);

        setPattern(key);

        int resultIndex = search(model, selectedIndex + 1, model.getSize());

        if (resultIndex != -1)
        {
            return resultIndex;
        }

        resultIndex = search(model, 0, selectedIndex);

        return (resultIndex != -1) ? resultIndex : (-1);
    }

    /**
     * Bestimmt die Zeichenfolge, nach der im {@link ComboBoxModel} gesucht werden soll. Wenn innerhlab von 250 ms nach einem Tastendruck erneut eine Taste
     * gedrueckt wird, so wird das Tastaturzeichen an die bestehende Zeichenfolge angehaengt.
     *
     * @param aKey char
     */
    private void setPattern(final char aKey)
    {
        long curTime = System.currentTimeMillis();

        // If last key was typed less than 250 ms ago, append to current pattern
        if ((curTime - this.lastKeyTime) < 250)
        {
            this.pattern += ("" + aKey).toLowerCase();
        }
        else
        {
            this.pattern = ("" + aKey).toLowerCase();
        }

        // Save current time
        this.lastKeyTime = curTime;
    }
}
