package de.patgrosse.asyncfoldercompare.gui.treetable;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellEditor;

public class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor {
    private static final long serialVersionUID = 5140332425284739567L;
    private JTree tree;
    private JTable table;

    public TreeTableCellEditor(JTree tree, JTable table) {
        this.tree = tree;
        this.table = table;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c) {
        return tree;
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent) {
            // expand/collapse tree
            int colunm1 = 0;
            MouseEvent me = (MouseEvent) e;
            int doubleClick = 2;
            MouseEvent newME = new MouseEvent(tree, me.getID(), me.getWhen(), me.getModifiers(),
                    me.getX() - table.getCellRect(0, colunm1, true).x, me.getY(), doubleClick, me.isPopupTrigger());
            tree.dispatchEvent(newME);
        }
        return false;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

}