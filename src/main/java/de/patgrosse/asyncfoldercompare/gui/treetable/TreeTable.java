package de.patgrosse.asyncfoldercompare.gui.treetable;

import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.JTree;

import de.patgrosse.asyncfoldercompare.constants.CompleteObjectCompareResult;
import de.patgrosse.asyncfoldercompare.gui.compare.CompareResultTypeRenderer;

public class TreeTable extends JTable {
    private static final long serialVersionUID = 1527982461133088399L;
    private TreeTableCellRenderer tree;

    public TreeTable(AbstractTreeTableModel treeTableModel) {
        tree = new TreeTableCellRenderer(this, treeTableModel);
        setModel(new TreeTableModelAdapter(treeTableModel, tree));
        TreeTableSelectionModel selectionModel = new TreeTableSelectionModel();
        tree.setSelectionModel(selectionModel);
        setSelectionModel(selectionModel.getListSelectionModel());
        setDefaultRenderer(AbstractTreeTableModel.class, tree);
        setDefaultRenderer(CompleteObjectCompareResult.class, new CompareResultTypeRenderer());
        setDefaultEditor(AbstractTreeTableModel.class, new TreeTableCellEditor(tree, this));
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        expandAllNodes(tree, 0, tree.getRowCount());
    }

    public void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            if (getModel().getValueAt(i, 1) == CompleteObjectCompareResult.DIFFER) {
                tree.expandRow(i);
            }
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }
}