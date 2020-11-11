/*
 * Created on 23.08.2004 To change the template for this generated file go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.freese.base.swing.components.watermark;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

/**
 * Beispiel Panel fuer Watermarks.
 *
 * @author Thomas Freese
 */
public class WatermarkExample extends JPanel implements ActionListener
{
    /**
     *
     */
    private static final long serialVersionUID = -4609321404275287633L;

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(new WatermarkExample());
        frame.addWindowListener(new WindowAdapter()
        {
            /**
             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
             */
            @Override
            public void windowClosing(final WindowEvent e)
            {
                Window win = e.getWindow();
                win.setVisible(false);
                win.dispose();
                System.exit(0);
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    /**
     *
     */
    private JButton jButton;

    /**
     *
     */
    private JLabel jLabel = new JLabel();

    /**
     *
     */
    private JPanel jPanel;

    /**
     *
     */
    private JPanel jPanel1;

    /**
     *
     */
    private JSplitPane jSplitPane;

    /**
     *
     */
    private WatermarkTable watermarkTable;

    /**
     *
     */
    private WatermarkTree watermarkTree;

    /**
     * This is the default constructor
     */
    public WatermarkExample()
    {
        super();

        initialize();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent ae)
    {
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(this);

        if (option != JFileChooser.CANCEL_OPTION)
        {
            File curFile = chooser.getSelectedFile();
            this.jLabel.setText(curFile.getAbsolutePath());

            ImageIcon image = new ImageIcon(curFile.getAbsolutePath());
            getWatermarkTable().setWatermark(image);
            getWatermarkTree().setWatermark(image);
        }
    }

    /**
     * @return {@link JButton}
     */
    private JButton getJButton()
    {
        if (this.jButton == null)
        {
            this.jButton = new JButton();
            this.jButton.setText("Change ...");
            this.jButton.addActionListener(this);
        }

        return this.jButton;
    }

    /**
     * @return {@link JPanel}
     */
    private JPanel getJPanel()
    {
        if (this.jPanel == null)
        {
            this.jPanel = new JPanel();
            this.jPanel.setLayout(new BorderLayout());
            this.jPanel.add(getJSplitPane(), BorderLayout.CENTER);
            this.jPanel.add(getJPanel1(), BorderLayout.NORTH);
        }

        return this.jPanel;
    }

    /**
     * @return {@link JPanel}
     */
    private JPanel getJPanel1()
    {
        if (this.jPanel1 == null)
        {
            JLabel jLabel1 = new JLabel();
            this.jPanel1 = new JPanel();
            this.jLabel.setText("None");
            this.jLabel.setFont(new Font("MS Sans Serif", Font.ITALIC, 11));
            jLabel1.setText("Icon :");
            jLabel1.setFont(new Font("MS Sans Serif", Font.BOLD, 11));
            this.jPanel1.add(jLabel1, null);
            this.jPanel1.add(this.jLabel, null);
            this.jPanel1.add(getJButton(), null);
        }

        return this.jPanel1;
    }

    /**
     * @return {@link JSplitPane}
     */
    private JSplitPane getJSplitPane()
    {
        if (this.jSplitPane == null)
        {
            this.jSplitPane = new JSplitPane();
            this.jSplitPane.setLeftComponent(getWatermarkTree());
            this.jSplitPane.setRightComponent(getWatermarkTable());
        }

        return this.jSplitPane;
    }

    /**
     * @return {@link WatermarkTable}
     */
    private WatermarkTable getWatermarkTable()
    {
        if (this.watermarkTable == null)
        {
            this.watermarkTable = new WatermarkTable();
        }

        return this.watermarkTable;
    }

    /**
     * @return {@link WatermarkTree}
     */
    private WatermarkTree getWatermarkTree()
    {
        if (this.watermarkTree == null)
        {
            this.watermarkTree = new WatermarkTree();
        }

        return this.watermarkTree;
    }

    /**
     *
     */
    private void initialize()
    {
        setLayout(new BorderLayout());
        this.setSize(400, 300);
        this.add(getJPanel(), BorderLayout.CENTER);
    }
}
