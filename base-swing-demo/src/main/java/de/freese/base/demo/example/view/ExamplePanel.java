package de.freese.base.demo.example.view;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Panel als Beispiel.
 *
 * @author Thomas Freese
 */
public class ExamplePanel extends JPanel
{
    /**
     *
     */
    private static final long serialVersionUID = -1162909250924225209L;

    /**
     *
     */
    private JButton buttonTaskStatistik;

    /**
     * @return {@link JButton}
     */
    public JButton getButtonTaskStatistik()
    {
        if (this.buttonTaskStatistik == null)
        {
            this.buttonTaskStatistik = new JButton();
        }

        return this.buttonTaskStatistik;
    }

    /**
     * Initialisiert die GUI.
     */
    public void initialize()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(getButtonTaskStatistik());
    }
}
