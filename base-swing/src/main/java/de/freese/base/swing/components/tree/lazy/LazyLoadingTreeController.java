package de.freese.base.swing.components.tree.lazy;

import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;

import javax.swing.SwingWorker;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ExpandListener eines Trees für das LazyLoading.<br>
 *
 * @author Thomas Freese
 * @see AbstractLazyLoadingTreeNode
 */
public class LazyLoadingTreeController implements TreeWillExpandListener
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LazyLoadingTreeController.class);

    /**
     * SwingWorker für das LazyLoading.
     *
     * @author Thomas Freese
     */
    private static class LoadWorker extends SwingWorker<MutableTreeNode[], Void>
    {
        /**
         *
         */
        private final AbstractLazyLoadingTreeNode node;
        /**
         *
         */
        private final Semaphore semaphore;

        /**
         * Erstellt ein neues {@link LoadWorker} Object.
         *
         * @param node {@link AbstractLazyLoadingTreeNode}
         * @param semaphore {@link Semaphore}
         */
        public LoadWorker(final AbstractLazyLoadingTreeNode node, final Semaphore semaphore)
        {
            super();

            this.node = node;
            this.semaphore = semaphore;
        }

        /**
         * @see javax.swing.SwingWorker#doInBackground()
         */
        @Override
        protected MutableTreeNode[] doInBackground() throws Exception
        {
            return this.node.loadChilds();
        }

        /**
         * @see javax.swing.SwingWorker#done()
         */
        @Override
        protected void done()
        {
            MutableTreeNode[] children = null;

            try
            {
                children = get();
            }
            catch (Exception ex)
            {
                children = new MutableTreeNode[0];
                LOGGER.error(null, ex);
            }

            this.node.setAllowsChildren(children.length > 0);
            this.node.setChildren(children);

            // Anderen Threads Warte-Lock entfernen
            this.semaphore.release();
        }
    }

    /**
     *
     */
    private final Executor executor;
    /**
     * BlockingSemaphore für den SwingWorker.
     */
    private final Semaphore semaphore = new Semaphore(1, true);

    /**
     * Erstellt ein neues {@link LazyLoadingTreeController} Object.<br>
     * Das Laden der Kind-Knoten wird als {@link SwingWorker} mit der Methode {@link SwingWorker#execute()} ausgeführt.
     */
    public LazyLoadingTreeController()
    {
        this(null);
    }

    /**
     * Erstellt ein neues {@link LazyLoadingTreeController} Object.<br>
     * Das Laden der Kind-Knoten wird als {@link SwingWorker} in dem übergebenen {@link Executor} durchgeführt. Ist dieser {@link Executor} null, wird
     * {@link SwingWorker#execute()} ausgeführt.
     *
     * @param executor {@link Executor}
     */
    public LazyLoadingTreeController(final Executor executor)
    {
        super();

        this.executor = executor;
    }

    /**
     * Blockiert den rufenden Thread solange, bis die Children des Knotens geladen worden sind.
     */
    public final void awaitChildNodes()
    {
        try
        {
            this.semaphore.acquire();
        }
        catch (Exception ex)
        {
            LOGGER.error(null, ex);
        }
    }

    /**
     * @see javax.swing.event.TreeWillExpandListener#treeWillCollapse(javax.swing.event.TreeExpansionEvent)
     */
    @Override
    public void treeWillCollapse(final TreeExpansionEvent event) throws ExpandVetoException
    {
        // NOOP
    }

    /**
     * @see javax.swing.event.TreeWillExpandListener#treeWillExpand(javax.swing.event.TreeExpansionEvent)
     */
    @Override
    public void treeWillExpand(final TreeExpansionEvent event) throws ExpandVetoException
    {
        TreePath path = event.getPath();
        Object lastPathComponent = path.getLastPathComponent();

        if (lastPathComponent instanceof AbstractLazyLoadingTreeNode lazyNode)
        {
            expandNode(lazyNode);
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
     * @param node {@link AbstractLazyLoadingTreeNode}
     */
    private void expandNode(final AbstractLazyLoadingTreeNode node)
    {
        if (node.areChildrenLoaded())
        {
            return;
        }

        node.setChildren(createLoadingNode());
        SwingWorker<MutableTreeNode[], Void> worker = new LoadWorker(node, this.semaphore);

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
