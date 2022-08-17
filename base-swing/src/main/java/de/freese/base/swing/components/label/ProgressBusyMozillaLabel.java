package de.freese.base.swing.components.label;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.Serial;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import de.freese.base.core.progress.ProgressCallback;

/**
 * BusyMozillaLabel, dass den Fortschritt rechts neben den Text einblendet.
 *
 * @author Thomas Freese
 */
public class ProgressBusyMozillaLabel extends BusyMozillaLabel implements ProgressCallback
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -2376101531012003243L;

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        final JFrame frame = new JFrame("GlassPaneDemo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setSize(new Dimension(400, 400));

        ProgressBusyMozillaLabel mozillaLabel = new ProgressBusyMozillaLabel("Taeschd", 9999);

        // mozillaLabel.setTrail(7);
        frame.getContentPane().add(mozillaLabel, BorderLayout.NORTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    /**
     * ms
     */
    private final long progressMax;
    /**
     * ms
     */
    private final long progressStart;
    /**
     *
     */
    private String originalText = "";
    /**
     * ms
     */
    private long progressCurrent;

    /**
     * Creates a new ProgressBusyMozillaLabel object.
     *
     * @param text String
     * @param progressMax long, Progressdauer
     */
    public ProgressBusyMozillaLabel(final String text, final long progressMax)
    {
        super(text);

        this.originalText = text;

        this.progressMax = progressMax;
        this.progressStart = System.currentTimeMillis();
        this.progressCurrent = System.currentTimeMillis();
    }

    /**
     * @see de.freese.base.core.progress.ProgressCallback#setProgress(float)
     */
    @Override
    public void setProgress(final float percentage)
    {
        int prozent = (int) (percentage * 100);

        if (prozent > 99)
        {
            prozent = 99;
        }

        setText(this.originalText + prozent);
    }

    /**
     * @see de.freese.base.swing.components.label.BusyMozillaLabel#performAnimation()
     */
    @Override
    protected void performAnimation()
    {
        super.performAnimation();

        this.progressCurrent = System.currentTimeMillis();

        try
        {
            // Progress nur anzeigen, wenn Maximum gesetzt
            if (this.progressMax > 0)
            {
                long delta = this.progressCurrent - this.progressStart;

                setProgress(delta, this.progressMax);
            }
        }
        catch (Exception ex)
        {
            // Ignore
        }
    }
}
