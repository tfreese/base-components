package de.freese.base.demo2.example;

import java.io.Serial;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author Thomas Freese
 */
class ExamplePanel extends JPanel
{
    @Serial
    private static final long serialVersionUID = -1162909250924225209L;

    private JButton buttonTaskStatistik;

    JButton getButtonTaskStatistik()
    {
        if (this.buttonTaskStatistik == null)
        {
            this.buttonTaskStatistik = new JButton();
        }

        return this.buttonTaskStatistik;
    }

    void init()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(getButtonTaskStatistik());
    }
}
