// Created: 29.05.2020
package de.freese.base.mvc;

import java.util.Objects;

/**
 * @author Thomas Freese
 */
public final class ControllerBuilder
{
    public static ControllerBuilder create(final ApplicationContext context)
    {
        return new ControllerBuilder(context);
    }
    private final ApplicationContext context;
    private String bundleName;
    private Class<? extends AbstractController> controllerClazz;

    private String name;

    private ControllerBuilder(final ApplicationContext context)
    {
        super();

        this.context = Objects.requireNonNull(context, "context required");
    }

    public Controller build()
    {
        Objects.requireNonNull(this.name, "name required");
        Objects.requireNonNull(this.bundleName, "bundleName required");
        Objects.requireNonNull(this.controllerClazz, "controllerClazz required");

        AbstractController controller = null;

        try
        {
            controller = this.controllerClazz.getDeclaredConstructor().newInstance();

            controller.setContext(this.context);
            controller.setName(this.name);
            controller.setResourceMap(this.context.getResourceMap(this.bundleName));
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

    public ControllerBuilder bundleName(final String bundleName)
    {
        this.bundleName = Objects.requireNonNull(bundleName, "bundleName required");

        return this;
    }

    public ControllerBuilder clazz(final Class<? extends AbstractController> controllerClazz)
    {
        this.controllerClazz = Objects.requireNonNull(controllerClazz, "controllerClazz required");

        return this;
    }

    public ControllerBuilder name(final String name)
    {
        this.name = Objects.requireNonNull(name, "name required");

        return this;
    }
}
