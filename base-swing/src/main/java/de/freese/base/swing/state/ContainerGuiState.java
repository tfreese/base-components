package de.freese.base.swing.state;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.io.Serial;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "ContainerGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContainerGuiState extends AbstractGuiState {
    @Serial
    private static final long serialVersionUID = 3075009969118716461L;

    private int height;
    private int width;
    private int x;
    private int y;

    public ContainerGuiState() {
        super(Container.class);
    }

    protected ContainerGuiState(final Class<?>... supportedTypes) {
        super(supportedTypes);
    }

    @Override
    public void restore(final Component component) {
        super.restore(component);

        final Container container = (Container) component;

        if (this.width == 0 && this.height == 0) {
            // Configuration not saved before.
            return;
        }

        container.setBounds(this.x, this.y, this.width, this.height);
    }

    @Override
    public void store(final Component component) {
        super.store(component);

        final Container container = (Container) component;

        final Rectangle bounds = container.getBounds();

        this.x = (int) bounds.getX();
        this.y = (int) bounds.getY();
        this.width = (int) bounds.getWidth();
        this.height = (int) bounds.getHeight();
    }
}
