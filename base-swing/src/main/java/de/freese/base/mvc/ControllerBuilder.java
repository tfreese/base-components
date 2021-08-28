// Created: 29.05.2020
package de.freese.base.mvc;

import java.util.Objects;

import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.resourcemap.ResourceMapBuilder;

/**
 * @author Thomas Freese
 */
public final class ControllerBuilder
{
    /**
     * @param context {@link ApplicationContext}
     *
     * @return {@link ControllerBuilder}
     */
    public static ControllerBuilder create(final ApplicationContext context)
    {
        return new ControllerBuilder(context);
    }

    /**
       *
       */
    private String bundleName;

    /**
     *
     */
    private final ApplicationContext context;

    /**
    *
    */
    private Class<? extends AbstractController> controllerClazz;

    /**
    *
    */
    private String name;

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
            ResourceMap resourceMap = ResourceMapBuilder.create(this.bundleName).parent(this.context.getResourceMapRoot()).build();
            this.context.addResourceMap(this.name, resourceMap);

            controller = this.controllerClazz.getDeclaredConstructor().newInstance();

            controller.setName(this.name);
            controller.setContext(this.context);
            controller.setResourceMap(resourceMap);
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return controller;
    }

    /**
     * Setzt den Namen des ResourceBundles.
     *
     * @param bundleName String
     *
     * @return {@link ControllerBuilder}
     */
    public ControllerBuilder bundleName(final String bundleName)
    {
        this.bundleName = Objects.requireNonNull(bundleName, "bundleName required");

        return this;
    }

    /**
     * Setzt die Klasse des {@link Controller}s.
     *
     * @param controllerClazz Class
     *
     * @return {@link ControllerBuilder}
     */
    public ControllerBuilder clazz(final Class<? extends AbstractController> controllerClazz)
    {
        this.controllerClazz = Objects.requireNonNull(controllerClazz, "controllerClazz required");

        return this;
    }

    /**
     * Setzt den Namen des {@link Controller}s.
     *
     * @param name String
     *
     * @return {@link ControllerBuilder}
     */
    public ControllerBuilder name(final String name)
    {
        this.name = Objects.requireNonNull(name, "name required");

        return this;
    }
}
