package de.patgrosse.asyncfoldercompare.utils.fsthreads;

import de.patgrosse.asyncfoldercompare.utils.VFSUtils;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks.CopyMultiFileSingleTaskCallback;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks.MultiFileTransferCallback;
import org.apache.commons.vfs2.FileObject;

public class CopyMultiFileSingleTaskThread extends AFCThread {
    private QueuedCopyTask task;
    private CopyMultiFileSingleTaskCallback callback;

    public CopyMultiFileSingleTaskThread(QueuedCopyTask task, CopyMultiFileSingleTaskCallback callback) {
        this.task = task;
        this.callback = callback;
    }

    @Override
    public void perform() {
        try {
            VFSUtils.performCopyTaskWithCallback(task, new MultiFileTransferCallback() {

                @Override
                public void nextFile(int currentFileNumber, int totalFiles, FileObject file) {
                    if (callback != null) {
                        callback.nextFile(currentFileNumber, totalFiles, file);
                    }
                }

                @Override
                public void bytesTransferred(long currentFileBytesTransferred, int currentFileBytesTransferredInBuffer,
                                             long currentFileTotalByte) {
                    callback.bytesTransferred(currentFileBytesTransferred, currentFileBytesTransferredInBuffer,
                            currentFileTotalByte);
                }
            });
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
}
