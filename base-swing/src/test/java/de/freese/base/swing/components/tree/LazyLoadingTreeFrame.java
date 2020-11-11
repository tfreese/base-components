package de.freese.base.swing.components.tree;

import java.awt.BorderLayout;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
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
import de.freese.base.swing.components.tree.lazy.AbstractLazyLoadingTreeNode;
import de.freese.base.swing.components.tree.lazy.LazyLoadingTreeController;
import de.freese.base.utils.TreeUtils;

/**
 * Zum Testen.
 *
 * @author Thomas Freese
 */
public class LazyLoadingTreeFrame extends JFrame
{
    /**
     * Test Knoten.
     *
     * @author Thomas Freese
     */
    private static class TestLazyLoadingTreeNode extends AbstractLazyLoadingTreeNode
    {
        /**
         *
         */
        private static final long serialVersionUID = -3561718904179675230L;

        /**
         * Erstellt ein neues {@link TestLazyLoadingTreeNode} Object.
         *
         * @param userObject Object
         * @param model {@link DefaultTreeModel}
         */
        public TestLazyLoadingTreeNode(final Object userObject, final DefaultTreeModel model)
        {
            super(userObject, model);
        }

        /**
         * @see de.freese.base.swing.components.tree.lazy.AbstractLazyLoadingTreeNode#loadChilds()
         */
        @Override
        public MutableTreeNode[] loadChilds()
        {
            MutableTreeNode[] childs = new MutableTreeNode[5];

            for (int i = 0; i < childs.length; i++)
            {
                childs[i] = new TestLazyLoadingTreeNode("Node " + (i + 1), getModel());

                try
                {
                    Thread.sleep(250);
                }
                catch (InterruptedException ex)
                {
                    // Ignore
                }
            }

            return childs;
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = 3374150787460216252L;

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        SwingUtilities.invokeLater(() -> new LazyLoadingTreeFrame());
    }

    /**
     * Erstellt ein neues {@link LazyLoadingTreeFrame} Object.
     */
    public LazyLoadingTreeFrame()
    {
        super();

        // Tree
        final JTree tree = new JTree();

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root", true);
        DefaultTreeModel model = new DefaultTreeModel(rootNode);

        for (int i = 0; i < 5; i++)
        {
            rootNode.add(new TestLazyLoadingTreeNode("Node " + (i + 1), model));
        }

        tree.setModel(model);

        Executor executor = Executors.newCachedThreadPool();
        final LazyLoadingTreeController controller = new LazyLoadingTreeController(executor);
        tree.addTreeWillExpandListener(controller);

        // Frame
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(new JScrollPane(tree), BorderLayout.CENTER);

        JButton button = new JButton("Expand TreePath");
        button.addActionListener(event -> {
            TreeUtils.collapse(tree);
            // TreePath treePath = tree.getPathForRow(4);
            // TreeUtils.expand(tree, 10);

            SwingWorker<Void, TreePath> testWorker = new SwingWorker<>()
            {
                /**
                 * @see javax.swing.SwingWorker#doInBackground()
                 */
                @Override
                protected Void doInBackground() throws Exception
                {
                    int[] expansionIndices = new int[]
                    {
                            0, 1, 2, 3, 4
                    };

                    Object parent = tree.getModel().getRoot();
                    TreePath treePath = new TreePath(parent);

                    for (int index : expansionIndices)
                    {
                        controller.awaitChildNodes();
                        System.out.println();

                        parent = tree.getModel().getChild(parent, index);

                        if (parent == null)
                        {
                            continue;
                        }

                        treePath = treePath.pathByAddingChild(parent);

                        System.out.println("publish");
                        publish(treePath);
                    }

                    System.out.println("return");

                    return null;
                }

                /**
                 * @see javax.swing.SwingWorker#process(java.util.List)
                 */
                @Override
                protected void process(final List<TreePath> chunks)
                {
                    System.out.println("process");

                    for (TreePath treePath : chunks)
                    {
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
