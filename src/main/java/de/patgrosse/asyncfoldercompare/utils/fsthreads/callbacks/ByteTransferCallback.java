package de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks;

public interface ByteTransferCallback {
    void bytesTransferred(long currentFileBytesTransferred, int currentFileBytesTransferredInBuffer,
                          long currentFileTotalByte);
}