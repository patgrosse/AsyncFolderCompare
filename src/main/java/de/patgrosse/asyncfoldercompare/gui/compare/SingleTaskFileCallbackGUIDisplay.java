package de.patgrosse.asyncfoldercompare.gui.compare;

import de.patgrosse.asyncfoldercompare.utils.InterruptCopyFakeException;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.QueuedCopyTask;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks.CopyMultiFileSingleTaskCallback;

import javax.swing.*;
import java.awt.*;

public class SingleTaskFileCallbackGUIDisplay extends CopyMultiFileSingleTaskCallbackImpl implements CopyMultiFileSingleTaskCallback {
    private QueuedCopyTask task;

    public SingleTaskFileCallbackGUIDisplay(Frame parent, QueuedCopyTask task) {
        this.task = task;
        setProgmon(new CopyProgressDialog(parent));
        getProgmon().setTitle("Copy in progress");
        getProgmon().setHeaderText("Copying " + task.getSource().getName().getBaseName());
        getProgmon().setVisible(true);
    }

    @Override
    public void finished(boolean crashed, Exception e) {
        getProgmon().close();
        String sourceFileName = task.getSource().getName().getBaseName();
        if (crashed) {
            if (e instanceof InterruptCopyFakeException) {
                JOptionPane.showMessageDialog(getProgmon().getParent(),
                        "<html>Operation was aborted!<br><b>" + sourceFileName + "</b> was not fully copied</html>",
                        "Operation terminated", JOptionPane.ERROR_MESSAGE);
            } else {
                if (AFCTreeGUI.LOG.isErrorEnabled()) {
                    AFCTreeGUI.LOG.error("Crashed while copying " + sourceFileName, e);
                }
                JOptionPane.showMessageDialog(getProgmon().getParent(),
                        "<html>Operation has crashed!<br><b>" + sourceFileName + "</b> was not fully copied</html>",
                        "Operation terminated", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(getProgmon().getParent(),
                    "<html>Operation successfully finished<br><b>" + sourceFileName + "</b> was copied</html>",
                    "Operation terminated", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}