package de.freese.base.swing.components.memory;

import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * GUI fuer den Memory-Monitor.
 *
 * @author Thomas Freese
 */
public class MemoryMonitorFrame extends JFrame
{
    /**
     *
     */
    private static final long serialVersionUID = 7387120307215034527L;

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        JFrame frame = new MemoryMonitorFrame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * Creates a new {@link MemoryMonitorFrame} object.
     */
    public MemoryMonitorFrame()
    {
        super("Memory Monitor");

        try
        {
            URL iconURL = ClassLoader.getSystemResource("icons/memory.gif");

            if (iconURL == null)
            {
                iconURL = getClass().getResource("icons/memory.gif");
            }

            if (iconURL != null)
            {
                setIconImage(new ImageIcon(iconURL).getImage());
            }
        }
        catch (Throwable ex)
        {
            // Ignore
        }

        setResizable(true);

        getContentPane().add("Center", new MemoryMonitorComponent());

        pack();
    }
}
