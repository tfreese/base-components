package de.freese.base.swing.state;

import java.awt.Component;
import java.awt.Frame;
import java.io.Serial;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JWindow;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import de.freese.base.swing.components.frame.ExtFrame;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "FrameGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class FrameGuiState extends ContainerGuiState {
    @Serial
    private static final long serialVersionUID = -3974478602033414091L;

    private int extendedState = Frame.NORMAL;

    public FrameGuiState() {
        super(JFrame.class, JWindow.class, JDialog.class, ExtFrame.class);
    }

    @Override
    public void restore(final Component component) {
        super.restore(component);

        final Frame frame = (Frame) component;

        frame.setExtendedState(extendedState);

        // Frames are always visible.
        if (!frame.isVisible()) {
            frame.setVisible(true);
        }
    }

    @Override
    public void store(final Component component) {
        super.store(component);

        final Frame frame = (Frame) component;

        extendedState = frame.getExtendedState();

        // Frames are always visible.
        setVisible(true);
    }
}
