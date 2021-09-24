// Created: 24.11.2020
package de.freese.base.swing.components.graph.demo;

import java.util.function.Supplier;

/**
 * @author Thomas Freese
 */
public class SinusValueSupplier implements Supplier<Float>
{
    /**
     *
     */
    private static final double GRAD_TO_RADIAN_FACTOR = Math.PI / 180D;
    /**
     *
     */
    private int grad;

    /**
     * @see java.util.function.Supplier#get()
     */
    @Override
    public Float get()
    {
        double radian = this.grad * GRAD_TO_RADIAN_FACTOR;
        double sinus = Math.sin(radian);

        this.grad += 1;

        if (this.grad > 360)
        {
            this.grad = 0;
        }

        return (float) sinus;
    }
}
