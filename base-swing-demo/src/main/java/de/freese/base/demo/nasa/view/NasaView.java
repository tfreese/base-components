package de.freese.base.demo.nasa.view;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import de.freese.base.demo.nasa.bp.INasaBP;
import de.freese.base.demo.nasa.task.NasaImageTask;
import de.freese.base.mvc.context.ApplicationContext;
import de.freese.base.mvc.view.AbstractView;
import de.freese.base.resourcemap.IResourceMap;
import de.freese.base.swing.task.AbstractTask;
import de.freese.base.swing.task.inputblocker.ComponentInputBlocker;

/**
 * Konkrete Implementierung der IView.
 *
 * @author Thomas Freese
 */
public class NasaView extends AbstractView implements INasaView
{
    /**
     * Erstellt ein neues {@link NasaView} Object.
     *
     * @param process {@link INasaBP}
     * @param context {@link ApplicationContext}
     */
    public NasaView(final INasaBP process, final ApplicationContext context)
    {
        super(process, context);
    }

    /**
     * @see de.freese.base.mvc.view.AbstractView#getComponent()
     */
    @Override
    public NasaPanel getComponent()
    {
        return (NasaPanel) super.getComponent();
    }

    /**
     * @see de.freese.base.mvc.view.AbstractView#getProcess()
     */
    @Override
    public INasaBP getProcess()
    {
        return (INasaBP) super.getProcess();
    }

    /**
     * @see de.freese.base.mvc.view.AbstractView#initialize()
     */
    @Override
    public void initialize()
    {
        super.initialize();

        setComponent(new NasaPanel());
        getComponent().initialize();

        IResourceMap resourceMap = getResourceMap();

        decorate(getComponent().getButtonPrevious(), resourceMap, "nasa.button.previous");
        decorate(getComponent().getButtonNext(), resourceMap, "nasa.button.next");
        decorate(getComponent().getButtonCancel(), resourceMap, "nasa.button.cancel");

        getComponent().getButtonPrevious().addActionListener(event ->
        {
            URL url = null;

            try
            {
                url = getProcess().getPreviousURL();
            }
            catch (Exception ex)
            {
                handleException(ex);

                return;
            }

            startTask(url);
        });
        getComponent().getButtonNext().addActionListener(event ->
        {
            URL url = null;

            try
            {
                url = getProcess().getNextURL();
            }
            catch (Exception ex)
            {
                handleException(ex);

                return;
            }

            startTask(url);
        });
        getComponent().getButtonCancel().addActionListener(event ->
        {
            AbstractTask<?, ?> task = getContext().getTaskMonitor().getForegroundTask();

            if (task != null)
            {
                task.cancel(true);
            }
        });
    }

    /**
     * @see de.freese.base.demo.nasa.view.INasaView#setImage(java.net.URL, java.awt.image.BufferedImage)
     */
    @Override
    public void setImage(final URL url, final BufferedImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();

        IResourceMap resourceMap = getResourceMap();
        String tip = resourceMap.getString("nasa.imageTooltip", url, Integer.valueOf(width), Integer.valueOf(height));

        JLabel label = getComponent().getLabelImage();
        label.setToolTipText(tip);
        label.setText(null);
        label.setIcon(new ImageIcon(image));

        label = getComponent().getLabelURL();
        label.setText(resourceMap.getString("nasa.label.url", url));

        // ScrollPane zentrieren
        JScrollPane scrollPane = getComponent().getScrollPane();
        JViewport viewport = scrollPane.getViewport();
        JScrollBar scrollBarH = scrollPane.getHorizontalScrollBar();
        JScrollBar scrollBarV = scrollPane.getVerticalScrollBar();

        // scrollBarH.setValue((scrollBarH.getMaximum() - scrollBarH.getMinimum()) / 2);
        // scrollBarV.setValue((scrollBarV.getMaximum() - scrollBarV.getMinimum()) / 2);
        // scrollBarH.setValue(width / 2);
        // scrollBarV.setValue(height / 2);

        int deltaH = scrollBarH.getVisibleAmount() / 2;
        int deltaV = scrollBarV.getVisibleAmount() / 2;

        viewport.setViewPosition(new Point((width / 2) - deltaH, (height / 2) - deltaV));
        // viewport.validate();
    }

    /**
     * @see de.freese.base.demo.nasa.view.INasaView#setMessage(java.lang.String, java.net.URL, java.lang.Throwable)
     */
    @Override
    public void setMessage(final String key, final URL url, final Throwable throwable)
    {
        // JViewport viewport = getViewComponent().getScrollPane().getViewport();
        // getLogger().info(viewport.getViewPosition().toString());

        String msg = getResourceMap().getString(key, url);

        JLabel label = getComponent().getLabelImage();
        label.setToolTipText("");
        label.setText(msg);
        label.setIcon(null);

        label = getComponent().getLabelURL();
        label.setText(null);

        if (throwable != null)
        {
            handleException(throwable);
        }
    }

    /**
     * @param button {@link JButton}
     * @param resourceMap {@link IResourceMap}
     * @param key String
     */
    private void decorate(final JButton button, final IResourceMap resourceMap, final String key)
    {
        button.setText(resourceMap.getString(key + ".text"));
        button.setIcon(resourceMap.getIcon(key + ".icon"));
    }

    /**
     * @param url {@link URL}
     */
    private void startTask(final URL url)
    {
        NasaImageTask task = new NasaImageTask(getProcess(), url, this, getResourceMap());
        task.setInputBlocker(new ComponentInputBlocker(task, getComponent()));

        getContext().getTaskService().execute(task);
    }

    /**
     * @see de.freese.base.mvc.view.AbstractView#getResourceMap()
     */
    @Override
    protected IResourceMap getResourceMap()
    {
        return getContext().getResourceMap("nasa");
    }
}
