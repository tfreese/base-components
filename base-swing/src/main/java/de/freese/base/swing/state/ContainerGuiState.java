package de.freese.base.swing.state;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * State eines Containers (JFrame, JPanel etc.).
 *
 * @author Thomas Freese
 */
@XmlRootElement(name = "ContainerGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContainerGuiState extends AbstractGuiState
{
    /**
     *
     */
    private static final long serialVersionUID = 3075009969118716461L;

    /**
     *
     */
    private int height;

    /**
     *
     */
    private int width;

    /**
     *
     */
    private int x;

    /**
     *
     */
    private int y;

    /**
     * Creates a new {@link ContainerGuiState} object.
     */
    public ContainerGuiState()
    {
        super(Container.class);
    }

    /**
     * Erstellt ein neues {@link ContainerGuiState} Object.
     *
     * @param supportedTypes supportedTypes Class[]
     */
    protected ContainerGuiState(final Class<?>...supportedTypes)
    {
        super(supportedTypes);
    }

    /**
     * @see de.freese.base.swing.state.AbstractGuiState#restore(java.awt.Component)
     */
    @Override
    public void restore(final Component component)
    {
        super.restore(component);

        Container container = (Container) component;

        if ((this.width == 0) && (this.height == 0))
        {
            // Konfiguration wurde vorher noch nicht gespeichert.
            return;
        }

        container.setBounds(this.x, this.y, this.width, this.height);
    }

    /**
     * @see de.freese.base.swing.state.AbstractGuiState#store(java.awt.Component)
     */
    @Override
    public void store(final Component component)
    {
        super.store(component);

        Container container = (Container) component;

        Rectangle bounds = container.getBounds();

        this.x = (int) bounds.getX();
        this.y = (int) bounds.getY();
        this.width = (int) bounds.getWidth();
        this.height = (int) bounds.getHeight();
    }
}
