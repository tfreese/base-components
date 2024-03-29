package de.freese.base.demo.example;

import java.util.concurrent.TimeUnit;

import de.freese.base.swing.task.AbstractSwingTask;
import de.freese.base.swing.task.DurationStatistikTaskListener;

/**
 * Task for demonstrating {@link DurationStatistikTaskListener}.
 *
 * @author Thomas Freese
 */
public class DurationStatistikTask extends AbstractSwingTask<Void, Void> {
    public DurationStatistikTask() {
        super();

        setTitle("TaskStatistik");
    }

    @Override
    protected Void doInBackground() throws Exception {
        getLogger().info("Started");

        for (int i = 0; i < 50; i++) {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        getLogger().info("Finished");

        return null;
    }
}
