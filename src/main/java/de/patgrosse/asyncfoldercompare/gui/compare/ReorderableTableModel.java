package de.patgrosse.asyncfoldercompare.gui.compare;

import javax.swing.table.DefaultTableModel;

public class ReorderableTableModel extends DefaultTableModel implements Reorderable {
    private static final long serialVersionUID = -2887245619769751609L;

    @SuppressWarnings("unchecked")
    @Override
    public void reorder(int from, int to) {
        if (from != to) {
            Object o = getDataVector().remove(from);
            getDataVector().add(from < to ? to - 1 : to, o);
            fireTableDataChanged();
        }
    }
}