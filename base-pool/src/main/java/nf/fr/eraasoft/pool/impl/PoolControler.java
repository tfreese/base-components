/**
 * Created: 27.01.2014
 */

package nf.fr.eraasoft.pool.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import nf.fr.eraasoft.pool.PoolSettings;

/**
 * Patch des Originals um Ausgaben auf der Console zu vermeiden.
 *
 * @author Thomas Freese
 */
public class PoolControler extends Thread
{
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(PoolControler.class);

    /**
     *
     */
    static PoolControler instance = null;

    /**
     * @param poolSettings {@link PoolSettings}
     */
    public static synchronized void addPoolSettings(final PoolSettings<?> poolSettings)
    {
        launch();
        instance.listPoolSettings.add(poolSettings);
    }

    /**
     *
     */
    private static synchronized void launch()
    {
        if (instance == null)
        {
            instance = new PoolControler();
        }

        if (!instance.alive)
        {
            instance.alive = true;
            instance.start();
        }
    }

    /**
     *
     */
    public static void shutdown()
    {
        if (instance != null)
        {
            instance.alive = false;

            for (PoolSettings<?> poolSettings : instance.listPoolSettings)
            {
                if (poolSettings.pool() instanceof Controlable)
                {
                    Controlable controlable = (Controlable) poolSettings.pool();
                    controlable.destroy();
                }
            }

            instance.listPoolSettings.clear();
            instance.interrupt();
            instance = null;
        }
    }

    /**
     *
     */
    boolean alive = false;

    /**
     *
     */
    Set<PoolSettings<?>> listPoolSettings = Collections.synchronizedSet(new HashSet<PoolSettings<?>>());

    /**
     * Erzeugt eine neue Instanz von {@link PoolControler}.
     */
    private PoolControler()
    {
        setName("PoolControler");
    }

    /**
     * Remove idle <br>
     * Validate idle
     */
    private void checkPool()
    {
        synchronized (this.listPoolSettings)
        {
            for (PoolSettings<?> poolSettings : this.listPoolSettings)
            {
                if (poolSettings.pool() instanceof Controlable)
                {
                    Controlable controlable = (Controlable) poolSettings.pool();

                    // if (poolSettings.debug())
                    if (LOGGER.isDebugEnabled())
                    {
                        LOGGER.debug(controlable.toString());
                    }

                    /*
                     * Remove idle
                     */
                    int idleToRemoves = controlable.idles() - poolSettings.maxIdle();

                    if (idleToRemoves > 0)
                    {
                        controlable.remove(idleToRemoves);
                    }

                    /*
                     * Check idle
                     */
                    controlable.validateIdles();
                }
            }
        }
    }

    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run()
    {
        LOGGER.info("Starting {}", getName());
        this.alive = true;

        while (this.alive)
        {
            try
            {
                sleep(PoolSettings.timeBetweenTwoControls() * 1000);
                checkPool();
            }
            catch (InterruptedException ex)
            {
                LOGGER.error("PoolControler {}", ex.getMessage());
                this.alive = false;
            }
        }
    }
}
