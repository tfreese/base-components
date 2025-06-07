// Created: 24.11.2020
package de.freese.base.swing.components.graph.model;

import java.util.function.Supplier;

/**
 * @author Thomas Freese
 */
public class SinusValueSupplier implements Supplier<Float> {
    private static final double GRAD_TO_RADIAN_FACTOR = Math.PI / 180D;

    private int grad;

    @Override
    public Float get() {
        final double radian = grad * GRAD_TO_RADIAN_FACTOR;
        final double sinus = Math.sin(radian);

        grad += 1;

        if (grad > 360) {
            grad = 0;
        }

        return (float) sinus;
    }
}
