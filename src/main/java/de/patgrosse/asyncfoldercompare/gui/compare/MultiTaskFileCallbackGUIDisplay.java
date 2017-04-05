package de.patgrosse.asyncfoldercompare.gui.compare;

import de.patgrosse.asyncfoldercompare.utils.InterruptCopyFakeException;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.QueuedCopyTask;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks.CopyMultiFileMultiTaskCallback;

import javax.swing.*;
import java.awt.*;

public class MultiTaskFileCallbackGUIDisplay extends CopyMultiFileSingleTaskCallbackImpl implements CopyMultiFileMultiTaskCallback {
    private int currentTaskNumber;
    private int totalTasks;

    private QueuedCopyTask currentTask;

    public MultiTaskFileCallbackGUIDisplay(Frame parent, QueuedCopyTask initialTask) {
        currentTask = initialTask;
        setProgmon(new CopyProgressDialog(parent, new String[]{"Name", "Operation"}));
        getProgmon().setTitle("Copy in progress");
        getProgmon().setHeaderText("Copying " + currentTask.getSource().getName().getBaseName() + " (Task 1/1)");
        getProgmon().setVisible(true);
    }

    @Override
    public void nextTask(int currentTaskNumber, int totalTasks, QueuedCopyTask task) {
        currentTask = task;
        setCurrentFileNumber(0);
        this.currentTaskNumber = currentTaskNumber;
        this.totalTasks = totalTasks;
        getProgmon().setHeaderText("Copying " + currentTask.getSource().getName().getBaseName() + " (Task "
                + currentTaskNumber + "/" + totalTasks + ")");
    }

    @Override
    public void taskListChanged(QueuedCopyTask[] tasks) {
        String[][] values = new String[tasks.length][];
        for (int i = 0; i < tasks.length; i++) {
            values[i] = new String[]{tasks[i].getSource().getName().getBaseName(), tasks[i].getAction().toString()};
        }
        getProgmon().setWaitingOperations(values);
        totalTasks = currentTaskNumber + tasks.length;
        getProgmon().setHeaderText("Copying " + currentTask.getSource().getName().getBaseName() + " (Task "
                + currentTaskNumber + "/" + totalTasks + ")");
    }

    @Override
    public void finished(boolean crashed, Exception e) {
        getProgmon().close();
        String sourceFileName = currentTask.getSource().getName().getBaseName();
        if (crashed) {
            if (e instanceof InterruptCopyFakeException) {
                JOptionPane.showMessageDialog(getProgmon().getParent(),
                        "<html>Operation was aborted!<br><b>" + sourceFileName + "</b> was not fully copied and "
                                + (totalTasks - currentTaskNumber) + " have not been started</html>",
                        "Operation terminated", JOptionPane.ERROR_MESSAGE);
            } else {
                if (AFCTreeGUI.LOG.isErrorEnabled()) {
                    AFCTreeGUI.LOG.error("Crashed while copying " + sourceFileName, e);
                }
                JOptionPane.showMessageDialog(getProgmon().getParent(),
                        "<html>Operation has crashed!<br><b>" + sourceFileName + "</b> was not fully copied and "
                                + (totalTasks - currentTaskNumber) + " have not been started</html>",
                        "Operation terminated", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(getProgmon().getParent(),
                    "<html>Operation successfully finished<br><b>" + sourceFileName + "</b> was copied and "
                            + (currentTaskNumber - 1) + " other tasks have completed</html>",
                    "Operation terminated", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}