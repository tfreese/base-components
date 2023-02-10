// Created: 07.02.23
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

import de.freese.base.demo.nasa.NasaController;
import de.freese.base.demo.nasa.NasaImageTask;
import de.freese.base.mvc.ApplicationContext;
import de.freese.base.mvc.view.AbstractView;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.swing.task.AbstractSwingTask;
import de.freese.base.swing.task.TaskManager;
import de.freese.base.swing.task.inputblocker.DefaultGlassPaneInputBlocker;

/**
 * @author Thomas Freese
 */
public class DefaultNasaView extends AbstractView implements NasaView {
    private NasaController controller;

    @Override
    public NasaPanel getComponent() {
        return (NasaPanel) super.getComponent();
    }

    @Override
    public NasaView initComponent(final ApplicationContext applicationContext) {
        super.initComponent(applicationContext);

        NasaPanel nasaPanel = new NasaPanel();
        setComponent(nasaPanel);

        nasaPanel.init();

        ResourceMap resourceMap = getResourceMap();

        decorate(getComponent().getButtonPrevious(), resourceMap, "nasa.button.previous");
        decorate(getComponent().getButtonNext(), resourceMap, "nasa.button.next");
        decorate(getComponent().getButtonCancel(), resourceMap, "nasa.button.cancel");

        controller = new NasaController(this);

        nasaPanel.getButtonPrevious().addActionListener(event -> {
            NasaImageTask task = new NasaImageTask(controller, controller::getPreviousURL, this, getResourceMap());
            // task.setInputBlocker(new DefaultInputBlocker().add(panel.getButtonNext(), panel.getButtonPrevious()));
            task.setInputBlocker(new DefaultGlassPaneInputBlocker(nasaPanel));

            getService(TaskManager.class).execute(task);
        });

        nasaPanel.getButtonNext().addActionListener(event -> {
            NasaImageTask task = new NasaImageTask(controller, controller::getNextURL, this, getResourceMap());
            // task.setInputBlocker(new DefaultInputBlocker().add(panel.getButtonNext(), panel.getButtonPrevious()));
            task.setInputBlocker(new DefaultGlassPaneInputBlocker(nasaPanel));

            getService(TaskManager.class).execute(task);
        });

        nasaPanel.getButtonCancel().addActionListener(event -> {
            AbstractSwingTask<?, ?> task = getService(TaskManager.class).getForegroundTask();

            if (task != null) {
                task.cancel(true);
            }
        });

        //        SwingUtilities.invokeLater(nasaPanel.getButtonNext()::doClick);

        return this;
    }

    @Override
    public void setImage(final URL url, final BufferedImage image) {
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

    @Override
    public void setMessage(final String key, final URL url, final Throwable throwable) {
        if (throwable != null) {
            handleException(throwable);
        }
    }

    @Override
    protected ResourceMap getResourceMap() {
        return getApplicationContext().getResourceMap("bundles/nasa");
    }

    private void decorate(final JButton button, final ResourceMap resourceMap, final String key) {
        button.setText(resourceMap.getString(key + ".text"));
        button.setIcon(resourceMap.getIcon(key + ".icon"));
    }
}
