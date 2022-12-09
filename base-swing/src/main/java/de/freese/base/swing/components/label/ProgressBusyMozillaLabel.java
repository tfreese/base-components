package de.freese.base.swing.components.label;

import java.io.Serial;

import de.freese.base.core.progress.ProgressCallback;

/**
 * BusyMozillaLabel, dass den Fortschritt rechts neben den Text einblendet.
 *
 * @author Thomas Freese
 */
public class ProgressBusyMozillaLabel extends BusyMozillaLabel implements ProgressCallback
{
    @Serial
    private static final long serialVersionUID = -2376101531012003243L;

    /**
     * ms
     */
    private final long progressMax;
    /**
     * ms
     */
    private final long progressStart;

    private String originalText = "";
    /**
     * ms
     */
    private long progressCurrent;

    public ProgressBusyMozillaLabel(final String text, final long progressMax)
    {
        super(text);

        this.originalText = text;

        this.progressMax = progressMax;
        this.progressStart = System.currentTimeMillis();
        this.progressCurrent = System.currentTimeMillis();
    }

    /**
     * @see de.freese.base.core.progress.ProgressCallback#setProgress(double)
     */
    @Override
    public void setProgress(final double percentage)
    {
        int prozent = (int) (percentage * 100D);

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
