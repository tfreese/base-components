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

import de.freese.base.mvc.AbstractView;
import de.freese.base.resourcemap.ResourceMap;

/**
 * Konkrete Implementierung der IView.
 *
 * @author Thomas Freese
 */
public class DefaultNasaView extends AbstractView implements NasaView
{
    /**
     * @see de.freese.base.mvc.View#createGUI()
     */
    @Override
    public void createGUI()
    {
        setComponent(new NasaPanel());
        getComponent().initialize();

        ResourceMap resourceMap = getResourceMap();

        decorate(getComponent().getButtonPrevious(), resourceMap, "nasa.button.previous");
        decorate(getComponent().getButtonNext(), resourceMap, "nasa.button.next");
        decorate(getComponent().getButtonCancel(), resourceMap, "nasa.button.cancel");
    }

    /**
     * @see de.freese.base.mvc.AbstractView#getComponent()
     */
    @Override
    public NasaPanel getComponent()
    {
        return (NasaPanel) super.getComponent();
    }

    /**
     * @see de.freese.base.demo.nasa.view.NasaView#setImage(java.net.URL, java.awt.image.BufferedImage)
     */
    @Override
    public void setImage(final URL url, final BufferedImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();

        ResourceMap resourceMap = getResourceMap();
        String tip = resourceMap.getString("nasa.imageTooltip", url, width, height);

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
     * @see de.freese.base.demo.nasa.view.NasaView#setMessage(java.lang.String, java.net.URL, java.lang.Throwable)
     */
    @Override
    public void setMessage(final String key, final URL url, final Throwable throwable)
    {
        // JViewport viewport = getViewComponent().getScrollPane().getViewport();
        // getLogger().info(viewport.getViewPosition().toString());

        // Wird schon von der GlasPane übernommen.
        // String msg = getResourceMap().getString(key, url);

        // JLabel label = getComponent().getLabelImage();
        // label.setToolTipText("");
        // label.setText(msg);
        // label.setIcon(null);
        //
        // label = getComponent().getLabelURL();
        // label.setText(null);

        if (throwable != null)
        {
            handleException(throwable);
        }
    }

    /**
     * @param button {@link JButton}
     * @param resourceMap {@link ResourceMap}
     * @param key String
     */
    private void decorate(final JButton button, final ResourceMap resourceMap, final String key)
    {
        button.setText(resourceMap.getString(key + ".text"));
        button.setIcon(resourceMap.getIcon(key + ".icon"));
    }
}
