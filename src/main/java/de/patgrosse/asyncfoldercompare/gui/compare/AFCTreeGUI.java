package de.patgrosse.asyncfoldercompare.gui.compare;

import de.patgrosse.asyncfoldercompare.entities.filesystem.ResultPathObject;
import de.patgrosse.asyncfoldercompare.entities.filesystem.result.ResultFolder;
import de.patgrosse.asyncfoldercompare.gui.GUITools;
import de.patgrosse.asyncfoldercompare.gui.treetable.AbstractTreeTableModel;
import de.patgrosse.asyncfoldercompare.gui.treetable.TreeTable;
import de.patgrosse.asyncfoldercompare.utils.FileTreeComparator;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.FileActionThreadPoolManager;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.QueuedCopyTask;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks.CopyCallbackFactory;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks.CopyMultiFileMultiTaskCallback;
import de.patgrosse.asyncfoldercompare.utils.fsthreads.callbacks.CopyMultiFileSingleTaskCallback;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AFCTreeGUI extends JFrame implements CopyCallbackFactory {
    private static final long serialVersionUID = -7503353678594701992L;
    static final String STATUS_HTML = "<html><table><tbody>" + "<tr><td></td><td>File %d of %d</td></tr>"
            + "<tr><td>Current file:</td><td>%s</td></tr>"
            + "<tr><td>Current progress:</td><td>%5.1f %%, Speed: %7s/s</td></tr>" + "</tbody></table>";
    private FileObject foRootOld, foRootNew;

    private AFCTreeGUI(final FileTreeComparator comp, final ResultFolder resFolder, final FileObject foRootOld,
                       final FileObject foRootNew) throws FileSystemException {
        super("AsyncFolderCompare");
        this.foRootOld = foRootOld;
        this.foRootNew = foRootNew;
        setLayout(new GridLayout(0, 1));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        AbstractTreeTableModel treeTableModel = new ResultFolderModel(comp, resFolder);
        final TreeTable myTreeTable = new TreeTable(treeTableModel);
        myTreeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                checkPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                checkPopup(e);
            }

            private void checkPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = myTreeTable.rowAtPoint(e.getPoint());
                    int column = myTreeTable.columnAtPoint(e.getPoint());
                    if (!myTreeTable.isRowSelected(row)) {
                        myTreeTable.changeSelection(row, column, false, false);
                    }
                    ResultPathObject<?> res = (ResultPathObject<?>) myTreeTable.getModel().getValueAt(row, -1);
                    AFCPopupMenu pop = new AFCPopupMenu(AFCTreeGUI.this, res);
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

    @Override
    public CopyMultiFileSingleTaskCallback getSingleTaskCallback(QueuedCopyTask task) {
        return new SingleTaskFileCallbackGUIDisplay(this, task);
    }

    @Override
    public CopyMultiFileMultiTaskCallback getMultiTaskCallback(QueuedCopyTask initialTask) {
        return new MultiTaskFileCallbackGUIDisplay(this, initialTask);
    }

    public FileObject getFoRootOld() {
        return foRootOld;
    }

    public FileObject getFoRootNew() {
        return foRootNew;
    }
}