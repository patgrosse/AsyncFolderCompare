package de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks;

import de.patgrosse.asyncfoldercompare.utils.fsthreads.QueuedCopyTask;

public interface CopyCallbackFactory {
    CopyMultiFileSingleTaskCallback getSingleTaskCallback(QueuedCopyTask task);

    CopyMultiFileMultiTaskCallback getMultiTaskCallback(QueuedCopyTask initialTask);
}
