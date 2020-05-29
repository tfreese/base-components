/**
 * Created: 29.05.2020
 */

package de.freese.base.mvc;

import java.util.Objects;
import de.freese.base.resourcemap.ResourceMap;

/**
 * @author Thomas Freese
 */
public final class ControllerBuilder
{
    /**
     * @param context {@link ApplicationContext}
     * @return {@link ControllerBuilder}
     */
    public static ControllerBuilder create(final ApplicationContext context)
    {
        return new ControllerBuilder(context);
    }

    /**
       *
       */
    private String bundleName = null;

    /**
     *
     */
    private final ApplicationContext context;

    /**
    *
    */
    private Class<? extends AbstractController> controllerClazz = null;

    /**
    *
    */
    private String name = null;

    /**
     * Erstellt ein neues {@link ControllerBuilder} Object.
     *
     * @param context {@link ApplicationContext}
     */
    private ControllerBuilder(final ApplicationContext context)
    {
        super();

        this.context = Objects.requireNonNull(context, "context required");
    }

    /**
     * @return {@link Controller}
     */
    public Controller build()
    {
        Objects.requireNonNull(this.name, "name required");
        Objects.requireNonNull(this.bundleName, "bundleName required");
        Objects.requireNonNull(this.controllerClazz, "controllerClazz required");

        AbstractController controller = null;

        try
        {
            ResourceMap resourceMap = ResourceMap.create(this.bundleName);
            resourceMap.setParent(this.context.getResourceMapRoot());
            this.context.addResourceMap(this.name, resourceMap);

            controller = this.controllerClazz.getDeclaredConstructor().newInstance();

            controller.setName(this.name);
            controller.setContext(this.context);
            controller.setResourceMap(resourceMap);
        }
        catch (Exception ex)
        {
            if (ex instanceof RuntimeException)
            {
                throw (RuntimeException) ex;
            }

            throw new RuntimeException(ex);
        }

        return controller;
    }

    /**
     * Setzt den Namen des ResourceBundles.
     *
     * @param bundleName String
     * @return {@link ControllerBuilder}
     */
    public ControllerBuilder bundleName(final String bundleName)
    {
        this.bundleName = bundleName;

        return this;
    }

    /**
     * Setzt die Klasse des {@link Controller}s.
     *
     * @param controllerClazz Class
     * @return {@link ControllerBuilder}
     */
    public ControllerBuilder clazz(final Class<? extends AbstractController> controllerClazz)
    {
        this.controllerClazz = controllerClazz;

        return this;
    }

    /**
     * Setzt den Namen des {@link Controller}s.
     *
     * @param name String
     * @return {@link ControllerBuilder}
     */
    public ControllerBuilder name(final String name)
    {
        this.name = name;

        return this;
    }
}
