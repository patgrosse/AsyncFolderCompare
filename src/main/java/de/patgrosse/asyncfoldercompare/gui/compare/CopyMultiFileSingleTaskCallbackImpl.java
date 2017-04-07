package de.patgrosse.asyncfoldercompare.gui.compare;

import de.patgrosse.asyncfoldercompare.utils.InterruptCopyFakeException;
import de.patgrosse.asyncfoldercompare.utils.VFSUtils;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks.CopyMultiFileSingleTaskCallback;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CopyMultiFileSingleTaskCallbackImpl implements CopyMultiFileSingleTaskCallback {
    private static final Logger LOG = LoggerFactory.getLogger(CopyMultiFileSingleTaskCallbackImpl.class);
    private static final int DELTA_TIME = 800000000;

    private CopyProgressDialog progmon;

    private int currentFileNumber;
    private int totalFiles;
    private long lastTime = System.nanoTime();
    private int currentAmountOfBytes;
    private float nextLogPercentage;
    private String baseFileNameCache;

    @Override
    public void bytesTransferred(long currentFileBytesTransferred, int currentFileBytesTransferredInBuffer,
                                 long currentFileTotalBytes) {
        if (progmon.isCanceled()) {
            throw new InterruptCopyFakeException();
        }
        long newTime = System.nanoTime();
        currentAmountOfBytes += currentFileBytesTransferredInBuffer;
        float currentPercentage = ((float) currentFileBytesTransferred) / currentFileTotalBytes;
        double deltaSeconds = (newTime - lastTime) / Math.pow(10D, 9D);
        long bytesPerSec = (long) (currentAmountOfBytes / deltaSeconds);
        if (newTime - DELTA_TIME > lastTime) {
            /*
             * System.out.println("bytes: " + currentFileBytesTransferred +
			 * "/" + currentFileTotalBytes);
			 * System.out.println("percentage: file: " + currentPercentage +
			 * " total: " + ((currentFileNumber - 1 + currentPercentage) /
			 * totalFiles)); System.out.println("speed: currentB: " +
			 * currentAmountOfBytes + " delta: " + deltaSeconds + " bps: " +
			 * bytesPerSec); System.out.println();
			 */
            progmon.setDescriptionText(String.format(AFCTreeGUI.STATUS_HTML, currentFileNumber, totalFiles,
                    baseFileNameCache, currentPercentage * 100, VFSUtils.humanReadableByteCount(bytesPerSec, true)));
            progmon.setProgress((currentFileNumber - 1 + currentPercentage) / totalFiles * 100);
            lastTime = newTime;
            currentAmountOfBytes = 0;
        }
        if (currentPercentage > nextLogPercentage) {
            nextLogPercentage += 0.1F;
            if (LOG.isDebugEnabled()) {
                LOG.debug(baseFileNameCache + " - " + String.format("%5.1f %%", currentPercentage * 100)
                        + " - " + VFSUtils.humanReadableByteCount(bytesPerSec, true) + "/s");
            }
        }
    }

    @Override
    public void nextFile(int currentFileNumber, int totalFiles, FileObject currentFile) {
        setCurrentFileNumber(currentFileNumber);
        setTotalFiles(totalFiles);
        setBaseFileNameCache(currentFile.getName().getBaseName());
        setCurrentAmountOfBytes(0);
        setLastTime(System.nanoTime());
        setNextLogPercentage(0.0F);
    }

    public CopyProgressDialog getProgmon() {
        return progmon;
    }

    public void setProgmon(CopyProgressDialog progmon) {
        this.progmon = progmon;
    }

    public int getCurrentFileNumber() {
        return currentFileNumber;
    }

    public void setCurrentFileNumber(int currentFileNumber) {
        this.currentFileNumber = currentFileNumber;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public int getCurrentAmountOfBytes() {
        return currentAmountOfBytes;
    }

    public void setCurrentAmountOfBytes(int currentAmountOfBytes) {
        this.currentAmountOfBytes = currentAmountOfBytes;
    }

    public float getNextLogPercentage() {
        return nextLogPercentage;
    }

    public void setNextLogPercentage(float nextLogPercentage) {
        this.nextLogPercentage = nextLogPercentage;
    }

    public String getBaseFileNameCache() {
        return baseFileNameCache;
    }

    public void setBaseFileNameCache(String baseFileNameCache) {
        this.baseFileNameCache = baseFileNameCache;
    }
}
