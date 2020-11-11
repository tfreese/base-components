package de.freese.base.swing.state;

import java.awt.Component;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JWindow;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import de.freese.base.swing.components.frame.ExtFrame;

/**
 * State eines Frames.
 *
 * @author Thomas Freese
 */
@XmlRootElement(name = "FrameGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class FrameGuiState extends ContainerGuiState
{
    /**
     *
     */
    private static final long serialVersionUID = -3974478602033414091L;

    /**
     *
     */
    private int extendedState = Frame.NORMAL;

    /**
     * Creates a new {@link FrameGuiState} object.
     */
    public FrameGuiState()
    {
        super(JFrame.class, JWindow.class, JDialog.class, ExtFrame.class);
    }

    /**
     * @see de.freese.base.swing.state.ContainerGuiState#restore(java.awt.Component)
     */
    @Override
    public void restore(final Component component)
    {
        super.restore(component);

        Frame frame = (Frame) component;

        frame.setExtendedState(this.extendedState);

        // Frames sind immer sichtbar
        if (!frame.isVisible())
        {
            frame.setVisible(true);
        }
    }

    /**
     * @see de.freese.base.swing.state.ContainerGuiState#store(java.awt.Component)
     */
    @Override
    public void store(final Component component)
    {
        super.store(component);

        Frame frame = (Frame) component;

        this.extendedState = frame.getExtendedState();

        // Frames sind immer sichtbar
        setVisible(true);
    }
}
