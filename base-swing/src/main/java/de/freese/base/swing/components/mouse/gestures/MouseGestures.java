/*
MouseGestures - pure Java library for recognition and processing mouse gestures.
Copyright (C) 2003-2004 Smardec

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package de.freese.base.swing.components.mouse.gestures;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Main class for mouse gestures.
 * <br>Sample usage:
 * <code><pre>
 *      MouseGestures mouseGestures = new MouseGestures();
 *      mouseGestures.addMouseGesturesListener(myMouseGesturesListener);
 *      mouseGestures.start();</pre>
 * </code>
 *
 * @author Smardec
 * @author Thomas freese
 * @version 1.2
 */
public class MouseGestures {
    private final List<MouseGesturesListener> listeners = Collections.synchronizedList(new ArrayList<>());
    /**
     * Responsible for processing mouse events.
     */
    private final MouseGesturesRecognizer mouseGesturesRecognizer = new MouseGesturesRecognizer(this);
    /**
     * Specifies mouse button for gestures handling.
     * Can be <code>MouseEvent.BUTTON1_DOWN_MASK</code>, <code>MouseEvent.BUTTON2_DOWN_MASK</code>
     * or <code>MouseEvent.BUTTON3_DOWN_MASK</code>.
     * The default is <code>MouseEvent.BUTTON3_DOWN_MASK</code> (right mouse button).
     */
    private int mouseButton = InputEvent.BUTTON3_DOWN_MASK;
    /**
     * Responsible for monitoring mouse gestures.
     */
    private AWTEventListener mouseGesturesEventListener;

    public void addMouseGesturesListener(final MouseGesturesListener listener) {
        if (listener == null) {
            return;
        }

        listeners.add(listener);
    }

    /**
     * Returns current grid size (minimum mouse movement length to be recognized).
     *
     * @return Grid size in pixels. Default is 30.
     */
    public int getGridSize() {
        return mouseGesturesRecognizer.getGridSize();
    }

    /**
     * Returns mouse button used for gestures handling.
     *
     * @return <code>MouseEvent.BUTTON1_DOWN_MASK</code>, <code>MouseEvent.BUTTON2_DOWN_MASK</code>
     * or <code>MouseEvent.BUTTON3_DOWN_MASK</code>
     */
    public int getMouseButton() {
        return mouseButton;
    }

    /**
     * Removes mouse gestures listener.
     *
     * @param listener Instance of {@link MouseGesturesListener}
     */
    public void removeMouseGesturesListener(final MouseGesturesListener listener) {
        if (listener == null) {
            return;
        }

        listeners.remove(listener);
    }

    /**
     * Sets grid size (minimum mouse movement length to be recognized).
     *
     * @param gridSize New grid size in pixels
     */
    public void setGridSize(final int gridSize) {
        mouseGesturesRecognizer.setGridSize(gridSize);
    }

    /**
     * Sets mouse button used for gestures handling.
     *
     * @param mouseButton <code>MouseEvent.BUTTON1_DOWN_MASK</code>, <code>MouseEvent.BUTTON2_DOWN_MASK</code>
     * or <code>MouseEvent.BUTTON3_DOWN_MASK</code>
     */
    public void setMouseButton(final int mouseButton) {
        this.mouseButton = mouseButton;
    }

    public void start() {
        if (mouseGesturesEventListener == null) {
            mouseGesturesEventListener = event -> {
                if (event instanceof MouseEvent mouseEvent) {
                    if ((mouseEvent.getModifiersEx() & mouseButton) == mouseButton) {
                        mouseGesturesRecognizer.processMouseEvent(mouseEvent);
                    }

                    if ((mouseEvent.getID() == MouseEvent.MOUSE_RELEASED || mouseEvent.getID() == MouseEvent.MOUSE_CLICKED)
                            && (mouseEvent.getModifiersEx() & mouseButton) == mouseButton) {
                        if (mouseGesturesRecognizer.isGestureRecognized()) {
                            // prevents displaying popup menu and so on
                            mouseEvent.consume();
                            final String gesture = mouseGesturesRecognizer.getGesture();
                            // clear temporary information
                            mouseGesturesRecognizer.clearTemporaryInfo();
                            // execute action
                            fireProcessMouseGesture(gesture);
                        }
                        else {
                            // clear temporary information
                            mouseGesturesRecognizer.clearTemporaryInfo();
                        }
                    }
                }
            };
        }

        Toolkit.getDefaultToolkit().addAWTEventListener(mouseGesturesEventListener, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    public void stop() {
        if (mouseGesturesEventListener != null) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(mouseGesturesEventListener);
        }
    }

    /**
     * Fires event when new mouse movement is recognized but mouse gesture is not yet completed.
     *
     * @param gesture String representation of recognized movements. "L" for left, "R" for right,
     * "U" for up, "D" for down movements. For example: "ULD".
     */
    void fireGestureMovementRecognized(final String gesture) {
        listeners.forEach(listener -> listener.gestureMovementRecognized(gesture));
    }

    /**
     * Fires event when full mouse gesture is recognized (mouse button is released).
     *
     * @param gesture String representation of mouse gesture. "L" for left, "R" for right,
     * "U" for up, "D" for down movements. For example: "ULD".
     */
    private void fireProcessMouseGesture(final String gesture) {
        listeners.forEach(listener -> listener.processGesture(gesture));
    }
}
