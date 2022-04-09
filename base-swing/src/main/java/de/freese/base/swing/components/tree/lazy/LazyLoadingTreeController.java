package de.freese.base.swing.components.tree.lazy;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.function.Function;

import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Tree ExpandListener for LazyLoading of the Children for a MutableTreeNode.<br>
 *
 * @author Thomas Freese
 * @see LazyLoadingTreeNode
 */
public class LazyLoadingTreeController implements TreeWillExpandListener
{
    /**
     *
     */
    private final Executor executor;
    /**
     *
     */
    private final Function<LazyLoadingTreeNode, List<MutableTreeNode>> loadFunction;
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     *
     */
    private final Semaphore semaphore = new Semaphore(1, true);

    /**
     * Erstellt ein neues {@link LazyLoadingTreeController} Object.<br>
     * Das Laden der Kind-Knoten wird als {@link SwingWorker} mit der Methode {@link SwingWorker#execute()} ausgeführt.
     *
     * @param loadFunction {@link Function}
     */
    public LazyLoadingTreeController(Function<LazyLoadingTreeNode, List<MutableTreeNode>> loadFunction)
    {
        this(loadFunction, null);
    }

    /**
     * Erstellt ein neues {@link LazyLoadingTreeController} Object.<br>
     * Das Laden der Kind-Knoten wird als {@link SwingWorker} in dem übergebenen {@link Executor} durchgeführt. Ist dieser {@link Executor} null, wird
     * {@link SwingWorker#execute()} ausgeführt.
     *
     * @param loadFunction {@link Function}
     * @param executor {@link Executor}; Optional
     */
    public LazyLoadingTreeController(Function<LazyLoadingTreeNode, List<MutableTreeNode>> loadFunction, final Executor executor)
    {
        super();

        this.loadFunction = Objects.requireNonNull(loadFunction, "loadFunction required");
        this.executor = executor;
    }

    /**
     * Blockiert den rufenden Thread solange, bis die Children des aktuellen Knotens geladen worden sind.
     */
    public void awaitChildNodes()
    {
        this.semaphore.acquireUninterruptibly();
    }

    /**
     * @see TreeWillExpandListener#treeWillCollapse(TreeExpansionEvent)
     */
    @Override
    public void treeWillCollapse(final TreeExpansionEvent event) throws ExpandVetoException
    {
        // NOOP
    }

    /**
     * @see TreeWillExpandListener#treeWillExpand(TreeExpansionEvent)
     */
    @Override
    public void treeWillExpand(final TreeExpansionEvent event) throws ExpandVetoException
    {
        DefaultTreeModel treeModel = (DefaultTreeModel) ((JTree) event.getSource()).getModel();
        TreePath path = event.getPath();
        Object lastPathComponent = path.getLastPathComponent();

        if (lastPathComponent instanceof LazyLoadingTreeNode lazyNode)
        {
            loadChildren(treeModel, lazyNode);
        }
    }

    /**
     * Liefert den DummyKnoten für den Kinderzweig des Parents mit dem "Laden"-Text.
     *
     * @return {@link MutableTreeNode}
     */
    protected MutableTreeNode createLoadingNode()
    {
        return new DefaultMutableTreeNode("Loading ...", false);
    }

    /**
     * @return {@link Executor}
     */
    protected Executor getExecutor()
    {
        return this.executor;
    }

    /**
     * @return Logger
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @param treeModel {@link DefaultTreeModel}
     * @param node {@link LazyLoadingTreeNode}
     */
    protected void loadChildren(DefaultTreeModel treeModel, final LazyLoadingTreeNode node)
    {
        // Alle Children entfernen.
        node.removeAllChildren();

        // Loading Node setzen.
        MutableTreeNode loadingNode = createLoadingNode();
        node.add(loadingNode);
        //treeModel.insertNodeInto(loadingNode, node, 0);
        treeModel.nodeStructureChanged(node);

        SwingWorker<List<MutableTreeNode>, Void> worker = new SwingWorker<>()
        {
            @Override
            protected List<MutableTreeNode> doInBackground() throws Exception
            {
                getLogger().debug("Loading children for {}", node);

                return loadFunction.apply(node);
            }

            @Override
            protected void done()
            {
                loadingNode.removeFromParent();

                try
                {
                    List<MutableTreeNode> children = get();

                    children.forEach(node::add);

                    node.setChildrenLoaded(true);
                }
                catch (Exception ex)
                {
                    getLogger().error(null, ex);
                }
                finally
                {
                    treeModel.nodeStructureChanged(node);

                    semaphore.release();
                }
            }
        };

        if (getExecutor() == null)
        {
            worker.execute();
        }
        else
        {
            getExecutor().execute(worker);
        }
    }
}
