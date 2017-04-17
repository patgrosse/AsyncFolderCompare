package de.patgrosse.asyncfoldercompare.gui.compare;

import de.patgrosse.asyncfoldercompare.constants.CopyAction;
import de.patgrosse.asyncfoldercompare.entities.filesystem.PathObject;
import de.patgrosse.asyncfoldercompare.entities.filesystem.ResultPathObject;
import de.patgrosse.asyncfoldercompare.entities.filesystem.result.ResultFile;
import de.patgrosse.asyncfoldercompare.entities.filesystem.result.ResultFolder;
import de.patgrosse.asyncfoldercompare.utils.CompareCheckReference;
import de.patgrosse.asyncfoldercompare.utils.VFSUtils;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.FileActionThreadPoolManager;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.QueuedCopyTask;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AFCPopupMenu extends JPopupMenu implements ActionListener {
    private static final Logger LOG = LoggerFactory.getLogger(AFCPopupMenu.class);
    private static final String COPY_DIALOG_TEXT = "Do you really want to copy the selected files? This will possibly overwrite existing files.";

    private AFCTreeGUI parent;
    private ResultPathObject<?> resultPathObject;

    public AFCPopupMenu(AFCTreeGUI parent, ResultPathObject<?> resultPathObject) {
        this.parent = parent;
        this.resultPathObject = resultPathObject;
        if (resultPathObject instanceof ResultFile) {
            ResultFile file = (ResultFile) resultPathObject;
            JMenuItem nameItem = new JMenuItem(file.getName());
            nameItem.setEnabled(false);
            add(nameItem);
            addSeparator();
            JMenuItem sizeItem = new JMenuItem(getSizeString(file));
            sizeItem.setEnabled(false);
            add(sizeItem);
        } else if (resultPathObject instanceof ResultFolder) {
            ResultFolder folder = (ResultFolder) resultPathObject;
            JMenuItem nameItem = new JMenuItem(folder.getName());
            nameItem.setEnabled(false);
            add(nameItem);
        }
        addCopyActions();
    }

    private void addCopyActions() {
        PathObject oldObject = resultPathObject.getCorrespondingOld();
        PathObject newObject = resultPathObject.getCorrespondingNew();
        boolean oldReadyToCopy = parent.getFoRootOld() != null && oldObject != null;
        boolean newReadyToCopy = parent.getFoRootNew() != null && newObject != null;
        addSeparator();
        {
            JMenuItem details = new JMenuItem("Compare details...");
            details.setActionCommand("details");
            details.addActionListener(this);
            add(details);
        }
        addSeparator();
        {
            JMenuItem oldNew = new JMenuItem("copy OLD to NEW");
            oldNew.setActionCommand(CopyAction.OLDTONEW.toString());
            oldNew.setEnabled(oldReadyToCopy && parent.getFoRootOld() != null);
            oldNew.addActionListener(this);
            add(oldNew);
        }
        {
            JMenuItem newOld = new JMenuItem("copy NEW to OLD");
            newOld.setActionCommand(CopyAction.NEWTOOLD.toString());
            newOld.setEnabled(newReadyToCopy && parent.getFoRootNew() != null);
            newOld.addActionListener(this);
            add(newOld);
        }
        {
            JMenuItem oldTarget = new JMenuItem("copy OLD to TARGET");
            oldTarget.setActionCommand(CopyAction.OLDTOTARGET.toString());
            oldTarget.setEnabled(oldReadyToCopy);
            oldTarget.addActionListener(this);
            add(oldTarget);
        }
        {
            JMenuItem newTarget = new JMenuItem("copy NEW to TARGET");
            newTarget.setActionCommand(CopyAction.NEWTOTARGET.toString());
            newTarget.setEnabled(newReadyToCopy);
            newTarget.addActionListener(this);
            add(newTarget);
        }
    }

    private String getSizeString(ResultFile file) {
        String str = "Size: ";
        if (file.getCorrespondingOld() != null) {
            str += VFSUtils.humanReadableByteCount(
                    Long.parseLong(file.getCorrespondingOld().getDataStorage().getData(new CompareCheckReference("SizePlugin", "size"))), true);
        } else {
            str += " / ";
        }
        str += " -> ";
        if (file.getCorrespondingNew() != null) {
            str += VFSUtils.humanReadableByteCount(
                    Long.parseLong(file.getCorrespondingNew().getDataStorage().getData(new CompareCheckReference("SizePlugin", "size"))), true);
        } else {
            str += " / ";
        }
        return str;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        PathObject oldObject = resultPathObject.getCorrespondingOld();
        PathObject newObject = resultPathObject.getCorrespondingNew();
        try {
            if (event.getActionCommand().equals("details")) {
                AFCDetailsDialog dialog = new AFCDetailsDialog(parent, resultPathObject);
                dialog.setVisible(true);
            } else if (event.getActionCommand().equals(CopyAction.OLDTONEW.toString())) {
                int result = JOptionPane.showConfirmDialog(parent, COPY_DIALOG_TEXT, "Start copy",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    FileObject oldFo = VFSUtils.resolveFile(oldObject, parent.getFoRootOld());
                    FileObject newFo = VFSUtils.resolveFile(oldObject, parent.getFoRootNew());
                    LOG.info("User: copy old to new");
                    FileActionThreadPoolManager.getInstance()
                            .startCopy(new QueuedCopyTask(oldFo, newFo, CopyAction.OLDTONEW), parent);
                }
            } else if (event.getActionCommand().equals(CopyAction.NEWTOOLD.toString())) {
                int result = JOptionPane.showConfirmDialog(parent, COPY_DIALOG_TEXT, "Start copy",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    FileObject oldFo = VFSUtils.resolveFile(newObject, parent.getFoRootOld());
                    FileObject newFo = VFSUtils.resolveFile(newObject, parent.getFoRootNew());
                    LOG.info("User: copy new to old");
                    FileActionThreadPoolManager.getInstance()
                            .startCopy(new QueuedCopyTask(newFo, oldFo, CopyAction.NEWTOOLD), parent);
                }
            } else if (event.getActionCommand().equals(CopyAction.OLDTOTARGET.toString())) {
                int result = JOptionPane.showConfirmDialog(parent, COPY_DIALOG_TEXT, "Start copy",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    final FileObject oldFo = VFSUtils.resolveFile(oldObject, parent.getFoRootOld());
                    final FileObject targetFo = VFSUtils.resolveFile(oldObject, VFSUtils.getTargetObject());
                    LOG.info("User: copy old to target");
                    FileActionThreadPoolManager.getInstance().startCopy(
                            new QueuedCopyTask(oldFo, targetFo, CopyAction.OLDTOTARGET), parent);

                }
            } else if (event.getActionCommand().equals(CopyAction.NEWTOTARGET.toString())) {
                int result = JOptionPane.showConfirmDialog(parent, COPY_DIALOG_TEXT, "Start copy",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    FileObject newFo = VFSUtils.resolveFile(newObject, parent.getFoRootNew());
                    FileObject targetFo = VFSUtils.resolveFile(newObject, VFSUtils.getTargetObject());
                    LOG.info("User: copy new to target");
                    FileActionThreadPoolManager.getInstance().startCopy(
                            new QueuedCopyTask(newFo, targetFo, CopyAction.NEWTOTARGET), parent);
                }
            }
        } catch (FileSystemException e) {
            e.printStackTrace();
        }
    }
}
