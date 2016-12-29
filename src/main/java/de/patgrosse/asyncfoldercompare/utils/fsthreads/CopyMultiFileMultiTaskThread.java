package de.patgrosse.asyncfoldercompare.utils.fsthreads;

import java.util.concurrent.ConcurrentLinkedQueue;

import de.patgrosse.asyncfoldercompare.utils.VFSUtils;
import org.apache.commons.vfs2.FileObject;

import de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks.CopyMultiFileMultiTaskCallback;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks.MultiFileTransferCallback;

public class CopyMultiFileMultiTaskThread extends AFCThread {
    private int totalTaskCount;
    private ConcurrentLinkedQueue<QueuedCopyTask> queue;
    private CopyMultiFileMultiTaskCallback callback;

    public CopyMultiFileMultiTaskThread(CopyMultiFileMultiTaskCallback callback) {
        setName("CopyQueue");
        queue = new ConcurrentLinkedQueue<>();
        this.callback = callback;
    }

    @Override
    public void perform() {
        try {
            int i = 0;
            while (!isCancelRequested()) {
                QueuedCopyTask task = queue.poll();
                if (task == null) {
                    break;
                }
                i++;
                if (callback != null) {
                    callback.nextTask(i, totalTaskCount, task);
                    callback.taskListChanged(queue.toArray(new QueuedCopyTask[0]));
                }
                VFSUtils.performCopyTaskWithCallback(task, new MultiFileTransferCallback() {
                    @Override
                    public void nextFile(int currentFileNumber, int totalFiles, FileObject file) {
                        if (callback != null) {
                            callback.nextFile(currentFileNumber, totalFiles, file);
                        }
                    }

                    @Override
                    public void bytesTransferred(long currentFileBytesTransferred,
                                                 int currentFileBytesTransferredInBuffer, long currentFileTotalByte) {
                        if (callback != null) {
                            callback.bytesTransferred(currentFileBytesTransferred, currentFileBytesTransferredInBuffer,
                                    currentFileTotalByte);
                        }
                    }
                });
            }
        } catch (Exception e) {
            if (callback != null) {
                callback.finished(true, e);
            }
            return;
        }
        if (callback != null) {
            callback.finished(false, null);
        }
    }

    public boolean queue(QueuedCopyTask task) {
        if (queue.contains(task)) {
            return false;
        }
        queue.add(task);
        totalTaskCount++;
        callback.taskListChanged(queue.toArray(new QueuedCopyTask[0]));
        return true;
    }

    public boolean unqueue(QueuedCopyTask task) {
        if (queue.remove(task)) {
            totalTaskCount--;
            callback.taskListChanged(queue.toArray(new QueuedCopyTask[0]));
            return true;
        }
        return false;
    }
}
