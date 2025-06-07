package de.freese.base.demo.fibonacci.task;

import java.util.Objects;

import de.freese.base.demo.fibonacci.FibonacciController;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.swing.task.AbstractSwingTask;

/**
 * @author Thomas Freese
 */
public class FibonacciTask extends AbstractSwingTask<Long, Void> {
    private final FibonacciController controller;
    private final ResourceMap resourceMap;
    private final int value;

    public FibonacciTask(final int value, final FibonacciController controller, final ResourceMap resourceMap) {
        super();

        this.value = value;
        this.controller = Objects.requireNonNull(controller, "controller required");
        this.resourceMap = Objects.requireNonNull(resourceMap, "resourceMap required");

        setTitle(this.resourceMap.getString("fibonacci.title"));
    }

    @Override
    protected Long doInBackground() {
        if (value > 50) {
            throw new IllegalArgumentException("Wert > 50");
        }

        getLogger().info("Started");

        setSubTitle(resourceMap.getString("fibonacci.start"));

        final long result = controller.fibonacci(value);

        setSubTitle(resourceMap.getString("fibonacci.finished"));

        getLogger().info("Finished");

        return result;
    }
}
