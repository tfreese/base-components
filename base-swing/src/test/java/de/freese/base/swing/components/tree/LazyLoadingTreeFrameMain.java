package de.freese.base.swing.components.tree;

import static org.awaitility.Awaitility.await;

import java.awt.BorderLayout;
import java.io.Serial;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.swing.components.tree.lazy.LazyLoadingTreeController;
import de.freese.base.swing.components.tree.lazy.LazyLoadingTreeNode;
import de.freese.base.utils.TreeUtils;

/**
 * Zum Testen.
 *
 * @author Thomas Freese
 */
public final class LazyLoadingTreeFrameMain extends JFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(LazyLoadingTreeFrameMain.class);
    @Serial
    private static final long serialVersionUID = 3374150787460216252L;

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(LazyLoadingTreeFrameMain::new);
    }

    private LazyLoadingTreeFrameMain() {
        super();

        // Tree
        final JTree tree = new JTree();

        final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root", true);
        final DefaultTreeModel model = new DefaultTreeModel(rootNode);

        for (int i = 0; i < 3; i++) {
            rootNode.add(new LazyLoadingTreeNode("Node " + (i + 1)));
        }

        tree.setModel(model);

        final Function<LazyLoadingTreeNode, List<MutableTreeNode>> loadFunction = node -> {
            final List<MutableTreeNode> children = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                children.add(new LazyLoadingTreeNode("Node " + (i + 1)));
            }

            await().pollDelay(Duration.ofMillis(250)).until(() -> true);

            return children;
        };

        // LazyLoadingTreeController controller = new LazyLoadingTreeController(loadFunction);
        final LazyLoadingTreeController controller = new LazyLoadingTreeController(loadFunction, Executors.newCachedThreadPool());
        tree.addTreeWillExpandListener(controller);

        // Frame
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(new JScrollPane(tree), BorderLayout.CENTER);

        final JButton button = new JButton("Expand TreePath");
        button.addActionListener(event -> {
            TreeUtils.collapse(tree);
            // TreePath treePath = tree.getPathForRow(4);
            // TreeUtils.expand(tree, 10);

            final SwingWorker<Void, TreePath> testWorker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    final int[] expansionIndices = new int[]{0, 1, 2};

                    Object parent = tree.getModel().getRoot();
                    TreePath treePath = new TreePath(parent);

                    for (int index : expansionIndices) {
                        controller.awaitChildNodes();

                        parent = tree.getModel().getChild(parent, index);

                        if (parent == null) {
                            continue;
                        }

                        treePath = treePath.pathByAddingChild(parent);

                        await().pollDelay(Duration.ofMillis(350)).until(() -> true);

                        LOGGER.debug("publish");
                        publish(treePath);
                    }

                    LOGGER.debug("return");

                    return null;
                }

                @Override
                protected void process(final List<TreePath> chunks) {
                    for (TreePath treePath : chunks) {
                        LOGGER.debug("process: {}", treePath);
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
