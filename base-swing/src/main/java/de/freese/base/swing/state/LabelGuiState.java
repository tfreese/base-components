package de.freese.base.swing.state;

import java.awt.Component;
import java.io.Serial;

import javax.swing.JLabel;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "LabelGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class LabelGuiState extends AbstractGuiState {
    @Serial
    private static final long serialVersionUID = 8640371387385687109L;

    private String text = "";

    public LabelGuiState() {
        super(JLabel.class);
    }

    @Override
    public void restore(final Component component) {
        super.restore(component);

        final JLabel label = (JLabel) component;

        label.setText(text);
    }

    @Override
    public void store(final Component component) {
        super.store(component);

        final JLabel label = (JLabel) component;

        text = label.getText();
    }
}
