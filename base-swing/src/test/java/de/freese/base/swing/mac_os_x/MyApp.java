/*
 * File: MyApp.java Abstract: Simple Swing app demonstrating how to use the Apple EAWT APIs by way of reflection, allowing a single codebase to build and run on
 * platforms without those APIs installed. Version: 2.0 Disclaimer: IMPORTANT: This Apple software is supplied to you by Apple Inc. ("Apple") in consideration
 * of your agreement to the following terms, and your use, installation, modification or redistribution of this Apple software constitutes acceptance of these
 * terms. If you do not agree with these terms, please do not use, install, modify or redistribute this Apple software. In consideration of your agreement to
 * abide by the following terms, and subject to these terms, Apple grants you a personal, non-exclusive license, under Apple's copyrights in this original Apple
 * software (the "Apple Software"), to use, reproduce, modify and redistribute the Apple Software, with or without modifications, in source and/or binary forms;
 * provided that if you redistribute the Apple Software in its entirety and without modifications, you must retain this notice and the following text and
 * disclaimers in all such redistributions of the Apple Software. Neither the name, trademarks, service marks or logos of Apple Inc. may be used to endorse or
 * promote products derived from the Apple Software without specific prior written permission from Apple. Except as expressly stated in this notice, no other
 * rights or licenses, express or implied, are granted by Apple herein, including but not limited to any patent rights that may be infringed by your derivative
 * works or by other works in which the Apple Software may be incorporated. The Apple Software is provided by Apple on an "AS IS" basis. APPLE MAKES NO
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE IMPLIED WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, REGARDING THE APPLE SOFTWARE OR ITS USE AND OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS. IN NO EVENT SHALL APPLE BE LIABLE FOR ANY SPECIAL,
 * INDIRECT, INCIDENTAL OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION, MODIFICATION AND/OR DISTRIBUTION OF THE APPLE SOFTWARE, HOWEVER CAUSED AND WHETHER
 * UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE), STRICT LIABILITY OR OTHERWISE, EVEN IF APPLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * Copyright 2003-2007 Apple, Inc., All Rights Reserved
 */
package de.freese.base.swing.mac_os_x;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serial;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import de.freese.base.swing.macOsX.OsxAdapter;

/**
 * @author Thomas Freese
 */
public class MyApp extends JFrame implements ActionListener
{
    /**
     * Check that we are on Mac OS X. This is crucial to loading and using the OsxAdapter class.
     */
    public static final boolean IS_OS_MAC_OSX = System.getProperty("os.name").toLowerCase().contains("mac os x");
    /**
     * Ask AWT which menu modifier we should be using.
     */
    final static int MENU_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -8257153622565922709L;

    /**
     * @param args String[][]
     */
    public static void main(final String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            System.setProperty("apple.laf.useScreenMenuBar", "true");

            new MyApp().setVisible(true);
        });
    }

    /**
     *
     */
    final String[] colorNames =
            {
                    "White", "Black", "Red", "Blue", "Yellow", "Orange"
            };
    /**
     *
     */
    final Color[] colors =
            {
                    Color.WHITE, Color.BLACK, Color.RED, Color.BLUE, Color.YELLOW, Color.ORANGE
            };
    /**
     *
     */
    protected final JDialog aboutBox;
    /**
     *
     */
    protected final JComboBox<String> colorComboBox;
    /**
     *
     */
    protected final JLabel imageLabel;
    /**
     *
     */
    protected final JDialog prefs;
    /**
     *
     */
    protected JMenuItem aboutMI;
    /**
     *
     */
    protected transient BufferedImage currentImage;
    /**
     *
     */
    protected JMenuItem docsMI;
    /**
     *
     */
    protected JMenu fileMenu;
    /**
     *
     */
    protected JMenu helpMenu;
    /**
     *
     */
    protected JMenuItem openMI;
    /**
     *
     */
    protected JMenuItem optionsMI;
    /**
     *
     */
    protected JMenuItem quitMI;
    /**
     *
     */
    protected JMenuItem supportMI;

    /**
     *
     */
    public MyApp()
    {
        super("OsxAdapter");

        addMenus();

        // Main content area; set up a JLabel to display images selected by the user
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(this.imageLabel = new JLabel("Open an image to view it"));
        this.imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        this.imageLabel.setOpaque(true);

        // set up a simple about box
        this.aboutBox = new JDialog(this, "About OsxAdapter");
        this.aboutBox.getContentPane().setLayout(new BorderLayout());
        this.aboutBox.getContentPane().add(new JLabel("OsxAdapter", SwingConstants.CENTER));
        this.aboutBox.getContentPane().add(new JLabel("\u00A92003-2007 Apple, Inc.", SwingConstants.CENTER), BorderLayout.SOUTH);
        this.aboutBox.setSize(160, 120);
        this.aboutBox.setResizable(false);

        // Preferences dialog lets you select the background color when displaying an image
        this.prefs = new JDialog(this, "OsxAdapter Preferences");
        this.colorComboBox = new JComboBox<>(this.colorNames);
        this.colorComboBox.addActionListener(ev ->
        {
            if (MyApp.this.currentImage != null)
            {
                MyApp.this.imageLabel.setBackground(MyApp.this.colors[MyApp.this.colorComboBox.getSelectedIndex()]);
            }
        });

        JPanel masterPanel = new JPanel();
        masterPanel.setBorder(new TitledBorder("Window background color:"));
        masterPanel.add(this.colorComboBox);
        this.prefs.getContentPane().add(masterPanel);
        this.prefs.setSize(240, 100);
        this.prefs.setResizable(false);

        // Set up our application to respond to the Mac OS X application menu
        registerForMacOSXEvents();

        setSize(320, 240);
    }

    /**
     * General info dialog; fed to the OsxAdapter as the method to call when<br>
     * "About OsxAdapter" is selected from the application menu.
     */
    public void about()
    {
        this.aboutBox.setLocation((int) this.getLocation().getX() + 22, (int) this.getLocation().getY() + 22);
        this.aboutBox.setVisible(true);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e)
    {
        Object source = e.getSource();

        if (source == this.quitMI)
        {
            quit();
        }
        else
        {
            if (source == this.optionsMI)
            {
                preferences();
            }
            else
            {
                if (source == this.aboutMI)
                {
                    about();
                }
                else
                {
                    if (source == this.openMI)
                    {
                        // File:Open action shows a FileDialog for loading displayable images
                        FileDialog openDialog = new FileDialog(this);
                        openDialog.setMode(FileDialog.LOAD);
                        openDialog.setFilenameFilter((dir, name) ->
                        {
                            String[] supportedFiles = ImageIO.getReaderFormatNames();

                            for (String supportedFile : supportedFiles)
                            {
                                if (name.endsWith(supportedFile))
                                {
                                    return true;
                                }
                            }

                            return false;
                        });

                        openDialog.setVisible(true);
                        String filePath = openDialog.getDirectory() + openDialog.getFile();

                        if (filePath.length() > 0)
                        {
                            loadImageFile(filePath);
                        }
                    }
                }
            }
        }
    }

    /**
     *
     */
    public void addMenus()
    {
        JMenu fileMenu = new JMenu("File");
        JMenuBar mainMenuBar = new JMenuBar();
        mainMenuBar.add(fileMenu = new JMenu("File"));
        fileMenu.add(this.openMI = new JMenuItem("Open..."));
        this.openMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, MENU_MASK));
        this.openMI.addActionListener(this);

        // Quit/prefs menu items are provided on Mac OS X; only add your own on other platforms
        if (!IS_OS_MAC_OSX)
        {
            fileMenu.addSeparator();
            fileMenu.add(this.optionsMI = new JMenuItem("Options"));
            this.optionsMI.addActionListener(this);

            fileMenu.addSeparator();
            fileMenu.add(this.quitMI = new JMenuItem("Quit"));
            this.quitMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, MENU_MASK));
            this.quitMI.addActionListener(this);
        }

        mainMenuBar.add(this.helpMenu = new JMenu("Help"));
        this.helpMenu.add(this.docsMI = new JMenuItem("Online Documentation"));
        this.helpMenu.addSeparator();
        this.helpMenu.add(this.supportMI = new JMenuItem("Technical Support"));

        // About menu item is provided on Mac OS X; only add your own on other platforms
        if (!IS_OS_MAC_OSX)
        {
            this.helpMenu.addSeparator();
            this.helpMenu.add(this.aboutMI = new JMenuItem("About OsxAdapter"));
            this.aboutMI.addActionListener(this);
        }

        setJMenuBar(mainMenuBar);
    }

    /**
     * @param path String
     */
    public void loadImageFile(final String path)
    {
        try
        {
            this.currentImage = ImageIO.read(new File(path));
            this.imageLabel.setIcon(new ImageIcon(this.currentImage));
            this.imageLabel.setBackground(this.colors[this.colorComboBox.getSelectedIndex()]);
            this.imageLabel.setText("");
        }
        catch (IOException ioe)
        {
            System.out.println("Could not load image " + path);
        }

        repaint();
    }

    /**
     * General preferences' dialog; fed to the OsxAdapter as the method to call when<br>
     * "Preferences..." is selected from the application menu.
     */
    public void preferences()
    {
        this.prefs.setLocation((int) this.getLocation().getX() + 22, (int) this.getLocation().getY() + 22);
        this.prefs.setVisible(true);
    }

    /**
     * General quit handler; fed to the OsxAdapter as the method to call when a system quit event<br>
     * occurs. A quit event is triggered by Cmd-Q, selecting Quit from the application or<br>
     * Dock menu, or logging out.
     * <p/>
     *
     * @return boolean
     */
    public boolean quit()
    {
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?", "Quit?", JOptionPane.YES_NO_OPTION);

        return (option == JOptionPane.YES_OPTION);
    }

    /**
     * Generic registration with the Mac OS X application menu.<br>
     * Checks the platform, then attempts to register with the Apple EAWT.<br>
     * See OsxAdapter.java to see how this is done without directly referencing any Apple APIs
     */
    public void registerForMacOSXEvents()
    {
        if (IS_OS_MAC_OSX)
        {
            try
            {
                // Generate and register the OsxAdapter, passing it a hash of all the methods we
                // wish to use as delegates for various com.apple.eawt.ApplicationListener methods.
                OsxAdapter.setQuitHandler(this, getClass().getDeclaredMethod("quit", (Class<?>[]) null));
                OsxAdapter.setAboutHandler(this, getClass().getDeclaredMethod("about", (Class<?>[]) null));
                OsxAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("preferences", (Class<?>[]) null));
                OsxAdapter.setFileHandler(this, getClass().getDeclaredMethod("loadImageFile", String.class));
            }
            catch (NoSuchMethodException | SecurityException ex)
            {
                System.err.println("Error while loading the OsxAdapter:");
                ex.printStackTrace();
            }
        }
    }
}
