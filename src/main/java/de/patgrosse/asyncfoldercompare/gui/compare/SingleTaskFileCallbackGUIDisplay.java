package de.patgrosse.asyncfoldercompare.gui.compare;

import java.awt.Frame;

import javax.swing.JOptionPane;

import de.patgrosse.asyncfoldercompare.utils.InterruptCopyFakeException;
import de.patgrosse.asyncfoldercompare.utils.VFSUtils;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.QueuedCopyTask;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks.CopyMultiFileSingleTaskCallback;
import org.apache.commons.vfs2.FileObject;

public class SingleTaskFileCallbackGUIDisplay implements CopyMultiFileSingleTaskCallback {
    private static final int DELTA_TIME = 800000000;

    private CopyProgressDialog progmon;

    private int currentFileNumber;
    private int totalFiles;
    private long lastTime = System.nanoTime();
    private int currentAmountOfBytes;
    private float nextLogPercentage;

    private QueuedCopyTask task;
    private String baseFileNameCache;

    public SingleTaskFileCallbackGUIDisplay(Frame parent, QueuedCopyTask task) {
        this.task = task;
        progmon = new CopyProgressDialog(parent);
        progmon.setTitle("Copy in progress");
        progmon.setHeaderText("Copying " + task.getSource().getName().getBaseName());
        progmon.setVisible(true);
    }

    @Override
    public void nextFile(int currentFileNumber, int totalFiles, FileObject currentFile) {
        this.currentFileNumber = currentFileNumber;
        this.totalFiles = totalFiles;
        baseFileNameCache = currentFile.getName().getBaseName();
        currentAmountOfBytes = 0;
        lastTime = System.nanoTime();
        nextLogPercentage = 0.0F;
    }

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
            progmon.setDescriptionText(String.format(AFCTreeGUI.STATUS_HTML, currentFileNumber, totalFiles, baseFileNameCache,
                    currentPercentage * 100, VFSUtils.humanReadableByteCount(bytesPerSec, true)));
            progmon.setProgress((currentFileNumber - 1 + currentPercentage) / totalFiles * 100);
            lastTime = newTime;
            currentAmountOfBytes = 0;
        }
        if (currentPercentage > nextLogPercentage) {
            nextLogPercentage += 0.1F;
            if (AFCTreeGUI.LOG.isDebugEnabled()) {
                AFCTreeGUI.LOG.debug(baseFileNameCache + " - " + String.format("%5.1f %%", currentPercentage * 100) + " - "
                        + VFSUtils.humanReadableByteCount(bytesPerSec, true) + "/s");
            }
        }
    }

    @Override
    public void finished(boolean crashed, Exception e) {
        progmon.close();
        String sourceFileName = task.getSource().getName().getBaseName();
        if (crashed) {
            if (e instanceof InterruptCopyFakeException) {
                JOptionPane.showMessageDialog(progmon.getParent(),
                        "<html>Operation was aborted!<br><b>" + sourceFileName + "</b> was not fully copied</html>",
                        "Operation terminated", JOptionPane.ERROR_MESSAGE);
            } else {
                if (AFCTreeGUI.LOG.isErrorEnabled()) {
                    AFCTreeGUI.LOG.error("Crashed while copying " + sourceFileName, e);
                }
                JOptionPane.showMessageDialog(progmon.getParent(),
                        "<html>Operation has crashed!<br><b>" + sourceFileName + "</b> was not fully copied</html>",
                        "Operation terminated", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(progmon.getParent(),
                    "<html>Operation successfully finished<br><b>" + sourceFileName + "</b> was copied</html>",
                    "Operation terminated", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}