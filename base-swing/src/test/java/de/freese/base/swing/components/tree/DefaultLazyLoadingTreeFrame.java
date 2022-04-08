package de.freese.base.swing.components.tree;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import de.freese.base.swing.components.tree.lazy.DefaultLazyLoadingTreeController;
import de.freese.base.swing.components.tree.lazy.DefaultLazyLoadingTreeNode;
import de.freese.base.utils.TreeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Zum Testen.
 *
 * @author Thomas Freese
 */
public class DefaultLazyLoadingTreeFrame extends JFrame
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLazyLoadingTreeFrame.class);
    /**
     *
     */
    private static final long serialVersionUID = 3374150787460216252L;

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        SwingUtilities.invokeLater(DefaultLazyLoadingTreeFrame::new);
    }

    /**
     * Erstellt ein neues {@link DefaultLazyLoadingTreeFrame} Object.
     */
    public DefaultLazyLoadingTreeFrame()
    {
        super();

        // Tree
        final JTree tree = new JTree();

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root", true);
        DefaultTreeModel model = new DefaultTreeModel(rootNode);

        for (int i = 0; i < 3; i++)
        {
            rootNode.add(new DefaultLazyLoadingTreeNode("Node " + (i + 1)));
        }

        tree.setModel(model);

        Function<DefaultLazyLoadingTreeNode, List<MutableTreeNode>> loadFunction = node ->
        {
            List<MutableTreeNode> children = new ArrayList<>();

            for (int i = 0; i < 3; i++)
            {
                children.add(new DefaultLazyLoadingTreeNode("Node " + (i + 1)));
            }

            try
            {
                TimeUnit.MILLISECONDS.sleep(250);
            }
            catch (InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }

            return children;
        };

        //DefaultLazyLoadingTreeController controller = new DefaultLazyLoadingTreeController(loadFunction);
        DefaultLazyLoadingTreeController controller = new DefaultLazyLoadingTreeController(loadFunction, Executors.newCachedThreadPool());
        tree.addTreeWillExpandListener(controller);

        // Frame
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(new JScrollPane(tree), BorderLayout.CENTER);

        JButton button = new JButton("Expand TreePath");
        button.addActionListener(event ->
        {
            TreeUtils.collapse(tree);
            // TreePath treePath = tree.getPathForRow(4);
            // TreeUtils.expand(tree, 10);

            SwingWorker<Void, TreePath> testWorker = new SwingWorker<>()
            {
                /**
                 * @see SwingWorker#doInBackground()
                 */
                @Override
                protected Void doInBackground() throws Exception
                {
                    int[] expansionIndices = new int[]
                            {
                                    0, 1, 2
                            };

                    Object parent = tree.getModel().getRoot();
                    TreePath treePath = new TreePath(parent);

                    for (int index : expansionIndices)
                    {
                        System.out.println();

                        parent = tree.getModel().getChild(parent, index);

                        if (parent == null)
                        {
                            continue;
                        }

                        treePath = treePath.pathByAddingChild(parent);

                        try
                        {
                            TimeUnit.MILLISECONDS.sleep(350);
                        }
                        catch (InterruptedException ex)
                        {
                            Thread.currentThread().interrupt();
                        }

                        LOGGER.info("publish");
                        publish(treePath);
                    }

                    LOGGER.info("return");

                    return null;
                }

                /**
                 * @see SwingWorker#process(List)
                 */
                @Override
                protected void process(final List<TreePath> chunks)
                {
                    for (TreePath treePath : chunks)
                    {
                        LOGGER.info("process: {}", treePath);
                        tree.expandPath(treePath);
                    }
                }
            };

            testWorker.execute();
        });

        add(button, BorderLayout.SOUTH);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
