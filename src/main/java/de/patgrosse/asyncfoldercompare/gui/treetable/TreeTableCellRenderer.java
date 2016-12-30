package de.patgrosse.asyncfoldercompare.gui.treetable;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeModel;

public class TreeTableCellRenderer extends JTree implements TableCellRenderer {
    private static final long serialVersionUID = -3772025820479202468L;
    private int lastRenderedRow;
    private TreeTable treeTable;

    public TreeTableCellRenderer(TreeTable treeTable, TreeModel model) {
        super(model);
        this.treeTable = treeTable;
        this.setCellRenderer(new TreeTableTreeCellRender());
        // explicit call to synchronize heights between tree and table
        setRowHeight(getRowHeight());
    }

    @Override
    public void setRowHeight(int rowHeight) {
        if (rowHeight > 0) {
            super.setRowHeight(rowHeight);
            if (treeTable != null && treeTable.getRowHeight() != rowHeight) {
                treeTable.setRowHeight(getRowHeight());
            }
        }
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, 0, w, treeTable.getHeight());
    }

    @Override
    public void paint(Graphics g) {
        g.translate(0, -lastRenderedRow * getRowHeight());
        super.paint(g);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        lastRenderedRow = row;
        return this;
    }
}