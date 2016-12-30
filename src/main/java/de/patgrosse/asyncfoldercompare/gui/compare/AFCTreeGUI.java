package de.patgrosse.asyncfoldercompare.gui.compare;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import de.patgrosse.asyncfoldercompare.constants.CopyAction;
import de.patgrosse.asyncfoldercompare.entities.filesystem.PathObject;
import de.patgrosse.asyncfoldercompare.entities.filesystem.ResultFile;
import de.patgrosse.asyncfoldercompare.entities.filesystem.ResultFolder;
import de.patgrosse.asyncfoldercompare.entities.filesystem.ResultPathObject;
import de.patgrosse.asyncfoldercompare.gui.GUITools;
import de.patgrosse.asyncfoldercompare.gui.treetable.AbstractTreeTableModel;
import de.patgrosse.asyncfoldercompare.gui.treetable.TreeTable;
import de.patgrosse.asyncfoldercompare.utils.FileTreeComparator;
import de.patgrosse.asyncfoldercompare.utils.VFSUtils;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.FileActionThreadPoolManager;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.QueuedCopyTask;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks.CopyCallbackFactory;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks.CopyMultiFileMultiTaskCallback;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks.CopyMultiFileSingleTaskCallback;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AFCTreeGUI extends JFrame implements CopyCallbackFactory {
    private static final long serialVersionUID = -7503353678594701992L;
    static final String STATUS_HTML = "<html><table><tbody>" + "<tr><td></td><td>File %d of %d</td></tr>"
            + "<tr><td>Current file:</td><td>%s</td></tr>"
            + "<tr><td>Current progress:</td><td>%5.1f %%, Speed: %7s/s</td></tr>" + "</tbody></table>";
    private static final String COPY_DIALOG_TEXT = "Do you really want to copy the selected files? This will possibly overwrite existing files.";
    static final Logger LOG = LogManager.getLogger();

    private AFCTreeGUI(final FileTreeComparator comp, final ResultFolder resFolder, final FileObject foRootOld,
                       final FileObject foRootNew) throws FileSystemException {
        super("AsyncFolderCompare");
        setLayout(new GridLayout(0, 1));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        AbstractTreeTableModel treeTableModel = new ResultFolderModel(comp, resFolder);
        final TreeTable myTreeTable = new TreeTable(treeTableModel);
        myTreeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = myTreeTable.rowAtPoint(e.getPoint());
                    int column = myTreeTable.columnAtPoint(e.getPoint());
                    if (!myTreeTable.isRowSelected(row)) {
                        myTreeTable.changeSelection(row, column, false, false);
                    }
                    JPopupMenu pop = new JPopupMenu();
                    ResultPathObject<?> res = (ResultPathObject<?>) myTreeTable.getModel().getValueAt(row, -1);
                    if (res instanceof ResultFile) {
                        ResultFile file = (ResultFile) res;
                        JMenuItem nameItem = new JMenuItem(file.getName());
                        nameItem.setEnabled(false);
                        pop.add(nameItem);
                        pop.addSeparator();
                        JMenuItem sizeItem = new JMenuItem(getSizeString(file));
                        sizeItem.setEnabled(false);
                        pop.add(sizeItem);
                        addCopyActions(pop, file.getCorrespondingOld(), file.getCorrespondingNew(), foRootOld,
                                foRootNew);
                    } else if (res instanceof ResultFolder) {
                        ResultFolder folder = (ResultFolder) res;
                        JMenuItem nameItem = new JMenuItem(folder.getName());
                        nameItem.setEnabled(false);
                        pop.add(nameItem);
                        addCopyActions(pop, folder.getCorrespondingOld(), folder.getCorrespondingNew(), foRootOld,
                                foRootNew);
                    }
                    pop.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        getContentPane().add(new JScrollPane(myTreeTable));
        setSize(1000, 800);
        setLocationRelativeTo(null);
        GUITools.centerFrameInScreen(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (FileActionThreadPoolManager.getInstance().actionsRunning()) {
                    int answer = JOptionPane.showConfirmDialog(AFCTreeGUI.this,
                            "Copy operations are still running. Do you want to abort them?", "Operations running",
                            JOptionPane.OK_CANCEL_OPTION);
                    if (answer == JOptionPane.OK_OPTION) {
                        quit();
                    }
                } else {
                    quit();
                }
            }
        });
    }

    private void quit() {
        dispose();
        System.exit(0);
    }

    public static void startGUI(final FileTreeComparator comp, final ResultFolder resFolder, final FileObject foRootOld,
                                final FileObject foRootNew) {
        Runnable gui = () -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new AFCTreeGUI(comp, resFolder, foRootOld, foRootNew).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        SwingUtilities.invokeLater(gui);
    }

    private String getSizeString(ResultFile file) {
        String str = "Size: ";
        if (file.getCorrespondingOld() != null) {
            str += VFSUtils.humanReadableByteCount(
                    Long.parseLong(file.getCorrespondingOld().getDataStorage().getData("size")), true);
        } else {
            str += " / ";
        }
        str += " -> ";
        if (file.getCorrespondingNew() != null) {
            str += VFSUtils.humanReadableByteCount(
                    Long.parseLong(file.getCorrespondingNew().getDataStorage().getData("size")), true);
        } else {
            str += " / ";
        }
        return str;
    }

    private void addCopyActions(JPopupMenu pop, final PathObject oldObject, final PathObject newObject,
                                final FileObject foRootOld, final FileObject foRootNew) {
        boolean newReadyToCopy = foRootNew != null && newObject != null;
        boolean oldReadyToCopy = foRootOld != null && oldObject != null;
        pop.addSeparator();
        ActionListener copyListener = event -> {
            try {
                if (event.getActionCommand().equals(CopyAction.OLDTONEW.toString())) {
                    int result = JOptionPane.showConfirmDialog(AFCTreeGUI.this, COPY_DIALOG_TEXT, "Start copy",
                            JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        FileObject oldFo = VFSUtils.resolveFile(oldObject, foRootOld);
                        FileObject newFo = VFSUtils.resolveFile(oldObject, foRootNew);
                        LOG.info("User: copy old to new");
                        FileActionThreadPoolManager.getInstance()
                                .startCopy(new QueuedCopyTask(oldFo, newFo, CopyAction.OLDTONEW), AFCTreeGUI.this);
                    }
                } else if (event.getActionCommand().equals(CopyAction.NEWTOOLD.toString())) {
                    int result = JOptionPane.showConfirmDialog(AFCTreeGUI.this, COPY_DIALOG_TEXT, "Start copy",
                            JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        FileObject oldFo = VFSUtils.resolveFile(newObject, foRootOld);
                        FileObject newFo = VFSUtils.resolveFile(newObject, foRootNew);
                        LOG.info("User: copy new to old");
                        FileActionThreadPoolManager.getInstance()
                                .startCopy(new QueuedCopyTask(newFo, oldFo, CopyAction.NEWTOOLD), AFCTreeGUI.this);
                    }
                } else if (event.getActionCommand().equals(CopyAction.OLDTOTARGET.toString())) {
                    int result = JOptionPane.showConfirmDialog(AFCTreeGUI.this, COPY_DIALOG_TEXT, "Start copy",
                            JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        final FileObject oldFo = VFSUtils.resolveFile(oldObject, foRootOld);
                        final FileObject targetFo = VFSUtils.resolveFile(oldObject, VFSUtils.getTargetObject());
                        LOG.info("User: copy old to target");
                        FileActionThreadPoolManager.getInstance().startCopy(
                                new QueuedCopyTask(oldFo, targetFo, CopyAction.OLDTOTARGET), AFCTreeGUI.this);

                    }
                } else if (event.getActionCommand().equals(CopyAction.NEWTOTARGET.toString())) {
                    int result = JOptionPane.showConfirmDialog(AFCTreeGUI.this, COPY_DIALOG_TEXT, "Start copy",
                            JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        FileObject newFo = VFSUtils.resolveFile(newObject, foRootNew);
                        FileObject targetFo = VFSUtils.resolveFile(newObject, VFSUtils.getTargetObject());
                        LOG.info("User: copy new to target");
                        FileActionThreadPoolManager.getInstance().startCopy(
                                new QueuedCopyTask(newFo, targetFo, CopyAction.NEWTOTARGET), AFCTreeGUI.this);
                    }
                }
            } catch (FileSystemException e) {
                e.printStackTrace();
            }
        };
        JMenuItem oldNew = new JMenuItem("copy OLD to NEW");
        oldNew.setActionCommand(CopyAction.OLDTONEW.toString());
        oldNew.setEnabled(oldReadyToCopy && foRootOld != null);
        oldNew.addActionListener(copyListener);
        pop.add(oldNew);
        JMenuItem newOld = new JMenuItem("copy NEW to OLD");
        newOld.setActionCommand(CopyAction.NEWTOOLD.toString());
        newOld.setEnabled(newReadyToCopy && foRootNew != null);
        newOld.addActionListener(copyListener);
        pop.add(newOld);
        JMenuItem oldTarget = new JMenuItem("copy OLD to TARGET");
        oldTarget.setActionCommand(CopyAction.OLDTOTARGET.toString());
        oldTarget.setEnabled(oldReadyToCopy);
        oldTarget.addActionListener(copyListener);
        pop.add(oldTarget);
        JMenuItem newTarget = new JMenuItem("copy NEW to TARGET");
        newTarget.setActionCommand(CopyAction.NEWTOTARGET.toString());
        newTarget.setEnabled(newReadyToCopy);
        newTarget.addActionListener(copyListener);
        pop.add(newTarget);
    }

    @Override
    public CopyMultiFileSingleTaskCallback getSingleTaskCallback(QueuedCopyTask task) {
        return new SingleTaskFileCallbackGUIDisplay(this, task);
    }

    @Override
    public CopyMultiFileMultiTaskCallback getMultiTaskCallback(QueuedCopyTask initialTask) {
        return new MultiTaskFileCallbackGUIDisplay(this, initialTask);
    }
}