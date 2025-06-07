package de.freese.base.demo.example;

import java.io.Serial;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author Thomas Freese
 */
class ExamplePanel extends JPanel {
    @Serial
    private static final long serialVersionUID = -1162909250924225209L;

    private JButton buttonTaskStatistik;

    JButton getButtonTaskStatistik() {
        if (buttonTaskStatistik == null) {
            buttonTaskStatistik = new JButton();
        }

        return buttonTaskStatistik;
    }

    void init() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(getButtonTaskStatistik());
    }
}
