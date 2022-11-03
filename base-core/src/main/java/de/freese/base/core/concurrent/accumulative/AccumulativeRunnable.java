// Created: 24.10.2011

/*
 * Copyright 2005-2006 Sun Microsystems, Inc. All Rights Reserved. DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. This code is free software; you
 * can redistribute it and/or modify it under the terms of the GNU General Public License version 2 only, as published by the Free Software Foundation. Sun
 * designates this particular file as subject to the "Classpath" exception as provided by Sun in the LICENSE file that accompanied this code. This code is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License version 2 for more details (a copy is included in the LICENSE file that accompanied this code). You should have
 * received a copy of the GNU General Public License version 2 along with this work; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth
 * Floor, Boston, MA 02110-1301 USA. Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara, CA 95054 USA or visit www.sun.com if you need
 * additional information or have any questions.
 */

package de.freese.base.core.concurrent.accumulative;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;

/**
 * An abstract class to be used in the cases where we need {@code Runnable} to perform some actions on an appendable set of data. The set of data might be
 * appended after the {@code Runnable} is sent for the execution. Usually such {@code Runnables} are sent to the EDT.
 * <p>
 * Usage example:
 * <p>
 * Say we want to implement JLabel.setText(String text) which sends {@code text} string to the JLabel.setTextImpl(String text) on the EDT. In the event
 * JLabel.setText is called rapidly many times off the EDT we will get many updates on the EDT but only the last one is important. (Every next updates overrides
 * the previous one.) We might want to implement this {@code setText} in a way that only the last update is delivered.
 * <p>
 * Here is how one can do this using {@code AccumulativeRunnable}:
 *
 * <pre>
 * {@code AccumulativeRunnable<String> doSetTextImpl =
 *  new  AccumulativeRunnable<String>()} {
 *    {@literal @Override}
 *    {@code protected void run(List<String> args)} {
 *         //set to the last string being passed
 *         setTextImpl(args.get(args.size() - 1));
 *     }
 * }
 * void setText(String text) {
 *     //add text and send for the execution if needed.
 *     doSetTextImpl.add(text);
 * }
 * </pre>
 * <p>
 * Say we want to implement addDirtyRegion(Rectangle rect) which sends this region to the {@code handleDirtyRegions(List<Rect> regions)} on the EDT.
 * addDirtyRegions better be accumulated before handling on the EDT.
 * <p>
 * Here is how it can be implemented using AccumulativeRunnable:
 *
 * <pre>
 * {@code AccumulativeRunnable<Rectangle> doHandleDirtyRegions =}
 *    {@code new AccumulativeRunnable<Rectangle>()} {
 *        {@literal @Override}
 *        {@code protected void run(List<Rectangle> args)} {
 *             handleDirtyRegions(args);
 *         }
 *     };
 *  void addDirtyRegion(Rectangle rect) {
 *      doHandleDirtyRegions.add(rect);
 *  }
 * </pre>
 *
 * @param <T> the type this {@code Runnable} accumulates
 *
 * @author Igor Kushnirskiy
 * @author Thomas Freese
 * @see "sun.swing.AccumulativeRunnable"
 * @since 1.6
 */
public abstract class AccumulativeRunnable<T> implements Runnable
{
    private List<T> arguments;

    @SafeVarargs
    public final synchronized void add(final T... args)
    {
        boolean isSubmitted = true;

        if (this.arguments == null)
        {
            isSubmitted = false;
            this.arguments = new ArrayList<>();
        }

        Collections.addAll(this.arguments, args);

        if (!isSubmitted)
        {
            submit();
        }
    }

    /**
     * This implementation calls {@code run(List<T> args)} method with the list of accumulated arguments.
     */
    @Override
    public final void run()
    {
        run(flush());
    }

    /**
     * Equivalent to {@code Runnable.run} method with the accumulated arguments to process.
     *
     * @param args accumulated arguments to process.
     */
    protected abstract void run(List<T> args);

    /**
     * Sends this {@code Runnable} for the execution
     * <p>
     * This method is to be executed only from {@code add} method.
     * <p>
     * This implementation uses {@code SwingWorker.invokeLater}.
     */
    protected void submit()
    {
        SwingUtilities.invokeLater(this);
    }

    /**
     * Returns accumulated arguments and flashes the argument's storage.
     *
     * @return accumulated arguments
     */
    private synchronized List<T> flush()
    {
        List<T> list = this.arguments;
        this.arguments = null;

        return list;
    }
}
