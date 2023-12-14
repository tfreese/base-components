// Created: 07.02.23
package de.freese.base.demo.nasa.view;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.net.URI;

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

        final NasaPanel nasaPanel = new NasaPanel();
        setComponent(nasaPanel);

        nasaPanel.init();

        final ResourceMap resourceMap = getResourceMap();

        decorate(getComponent().getButtonPrevious(), resourceMap, "nasa.button.previous");
        decorate(getComponent().getButtonNext(), resourceMap, "nasa.button.next");
        decorate(getComponent().getButtonCancel(), resourceMap, "nasa.button.cancel");

        controller = new NasaController(this);

        nasaPanel.getButtonPrevious().addActionListener(event -> {
            final NasaImageTask task = new NasaImageTask(controller, controller::getPreviousUri, this, getResourceMap());
            // task.setInputBlocker(new DefaultInputBlocker().add(panel.getButtonNext(), panel.getButtonPrevious()));
            task.setInputBlocker(new DefaultGlassPaneInputBlocker(nasaPanel));

            getService(TaskManager.class).execute(task);
        });

        nasaPanel.getButtonNext().addActionListener(event -> {
            final NasaImageTask task = new NasaImageTask(controller, controller::getNextUri, this, getResourceMap());
            // task.setInputBlocker(new DefaultInputBlocker().add(panel.getButtonNext(), panel.getButtonPrevious()));
            task.setInputBlocker(new DefaultGlassPaneInputBlocker(nasaPanel));

            getService(TaskManager.class).execute(task);
        });

        nasaPanel.getButtonCancel().addActionListener(event -> {
            final AbstractSwingTask<?, ?> task = getService(TaskManager.class).getForegroundTask();

            if (task != null) {
                task.cancel(true);
            }
        });

        //        SwingUtilities.invokeLater(nasaPanel.getButtonNext()::doClick);

        return this;
    }

    @Override
    public void setImage(final URI uri, final BufferedImage image) {
        final int width = image.getWidth();
        final int height = image.getHeight();

        final ResourceMap resourceMap = getResourceMap();
        final String tip = resourceMap.getString("nasa.imageTooltip", uri, width, height);

        JLabel label = getComponent().getLabelImage();
        label.setToolTipText(tip);
        label.setText(null);
        label.setIcon(new ImageIcon(image));

        label = getComponent().getLabelUri();
        label.setText(resourceMap.getString("nasa.label.uri", uri));

        // ScrollPane zentrieren
        final JScrollPane scrollPane = getComponent().getScrollPane();
        final JViewport viewport = scrollPane.getViewport();
        final JScrollBar scrollBarH = scrollPane.getHorizontalScrollBar();
        final JScrollBar scrollBarV = scrollPane.getVerticalScrollBar();

        // scrollBarH.setValue((scrollBarH.getMaximum() - scrollBarH.getMinimum()) / 2);
        // scrollBarV.setValue((scrollBarV.getMaximum() - scrollBarV.getMinimum()) / 2);
        // scrollBarH.setValue(width / 2);
        // scrollBarV.setValue(height / 2);

        final int deltaH = scrollBarH.getVisibleAmount() / 2;
        final int deltaV = scrollBarV.getVisibleAmount() / 2;

        viewport.setViewPosition(new Point((width / 2) - deltaH, (height / 2) - deltaV));
        // viewport.validate();
    }

    @Override
    public void setMessage(final String key, final URI uri, final Throwable throwable) {
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
