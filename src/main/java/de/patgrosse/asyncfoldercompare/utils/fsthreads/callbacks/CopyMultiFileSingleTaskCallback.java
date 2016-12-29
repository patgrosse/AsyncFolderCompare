package de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks;

import org.apache.commons.vfs2.FileObject;

public interface CopyMultiFileSingleTaskCallback extends ByteTransferCallback, FinishableCallback {
    void nextFile(int currentFileNumber, int totalFiles, FileObject file);
}