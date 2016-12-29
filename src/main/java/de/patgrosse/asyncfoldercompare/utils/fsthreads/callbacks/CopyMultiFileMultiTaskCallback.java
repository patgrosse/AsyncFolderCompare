package de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks;

import de.patgrosse.asyncfoldercompare.utils.fsthreads.QueuedCopyTask;

public interface CopyMultiFileMultiTaskCallback extends CopyMultiFileSingleTaskCallback {
    void taskListChanged(QueuedCopyTask[] tasks);

    void nextTask(int currentTaskNumber, int totalTasks, QueuedCopyTask task);
}