package de.freese.base.swing.components.memory;

import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

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

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.setLocationRelativeTo(null);
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
            // Empty
        }

        setResizable(true);

        getContentPane().add("Center", new MemoryMonitorComponent());

        pack();
    }
}
