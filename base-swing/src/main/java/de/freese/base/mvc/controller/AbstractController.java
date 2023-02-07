// Created: 05.02.23
package de.freese.base.mvc.controller;

import java.util.Objects;

import de.freese.base.mvc.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractController
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final View view;

    protected AbstractController(final View view)
    {
        super();

        this.view = Objects.requireNonNull(view, "view required");
    }

    public View getView()
    {
        return view;
    }

    protected Logger getLogger()
    {
        return logger;
    }
}
