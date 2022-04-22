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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.io.Serial;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Simple test frame.
 */
public class TestFrame extends JFrame
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -3351411878765636929L;

    /**
     * @param args String[]
     */
    public static void main(String[] args)
    {
        TestFrame frame = new TestFrame();
        frame.setVisible(true);
    }

    /**
     *
     */
    private final JLabel statusLabel = new JLabel("Mouse gesture: ");
    /**
     *
     */
    private MouseGestures mouseGestures = new MouseGestures();

    public TestFrame()
    {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Mouse Gestures Test Frame");
        getContentPane().setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        initSize();
        initControls();
        initStatusBar();
        initMouseGestures();
    }

    private void initControls()
    {
        JCheckBox jCheckBoxButton1 = new JCheckBox("Right button");
        jCheckBoxButton1.addActionListener(event ->
                {
                    mouseGestures.setMouseButton(MouseEvent.BUTTON3_DOWN_MASK);
                }
        );

        JCheckBox jCheckBoxButton2 = new JCheckBox("Middle button");
        jCheckBoxButton2.addActionListener(event ->
                {
                    mouseGestures.setMouseButton(MouseEvent.BUTTON2_DOWN_MASK);
                }
        );

        JCheckBox jCheckBoxButton3 = new JCheckBox("Left button");
        jCheckBoxButton3.addActionListener(event ->
                {
                    mouseGestures.setMouseButton(MouseEvent.BUTTON1_DOWN_MASK);
                }
        );

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(jCheckBoxButton1);
        buttonGroup.add(jCheckBoxButton2);
        buttonGroup.add(jCheckBoxButton3);
        jCheckBoxButton1.setSelected(true);

        JPanel jPanel = new JPanel(new GridLayout(4, 1));
        jPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        jPanel.add(new JLabel("Select mouse button used for gestures handling."));
        jPanel.add(jCheckBoxButton1);
        jPanel.add(jCheckBoxButton2);
        jPanel.add(jCheckBoxButton3);

        getContentPane().add(jPanel, BorderLayout.NORTH);
    }

    private void initMouseGestures()
    {
        mouseGestures = new MouseGestures();
        mouseGestures.addMouseGesturesListener(new MouseGesturesListener()
        {
            @Override
            public void gestureMovementRecognized(String currentGesture)
            {
                setGestureString(addCommas(currentGesture));
            }

            @Override
            public void processGesture(String gesture)
            {
                try
                {
                    Thread.sleep(200);
                }
                catch (InterruptedException ex)
                {
                    // Empty
                }

                setGestureString("");
            }

            private String addCommas(String gesture)
            {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < gesture.length(); i++)
                {
                    sb.append(gesture.charAt(i));

                    if (i != gesture.length() - 1)
                    {
                        sb.append(",");
                    }
                }

                return sb.toString();
            }
        });

        mouseGestures.start();
    }

    private void initSize()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = new Dimension(640, 480);

        if (size.height > screenSize.height)
        {
            size.height = screenSize.height;
        }

        if (size.width > screenSize.width)
        {
            size.width = screenSize.width;
        }

        setSize(size);
        setLocation((screenSize.width - size.width) / 2, (screenSize.height - size.height) / 2);
    }

    private void initStatusBar()
    {
        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        jPanel.add(statusLabel);
        getContentPane().add(jPanel, BorderLayout.SOUTH);
    }

    private void setGestureString(String gesture)
    {
        statusLabel.setText("Mouse gesture: " + gesture);
    }
}