package de.patgrosse.asyncfoldercompare.gui.treetable;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public abstract class AbstractTreeTableModel implements TreeModel {
    private Object root;
    private EventListenerList listenerList = new EventListenerList();
    private static final int CHANGED = 0;
    private static final int INSERTED = 1;
    private static final int REMOVED = 2;
    private static final int STRUCTURE_CHANGED = 3;

    public AbstractTreeTableModel(Object root) {
        this.root = root;
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return 0;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }

    private void fireTreeNode(int changeType, Object source, Object[] path, int[] childIndices, Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = new TreeModelEvent(source, path, childIndices, children);
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                switch (changeType) {
                    case CHANGED:
                        ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
                        break;
                    case INSERTED:
                        ((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
                        break;
                    case REMOVED:
                        ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
                        break;
                    case STRUCTURE_CHANGED:
                        ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
        fireTreeNode(CHANGED, source, path, childIndices, children);
    }

    protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
        fireTreeNode(INSERTED, source, path, childIndices, children);
    }

    protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children) {
        fireTreeNode(REMOVED, source, path, childIndices, children);
    }

    protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
        fireTreeNode(STRUCTURE_CHANGED, source, path, childIndices, children);
    }

    /**
     * Returns the number of available columns.
     *
     * @return Number of Columns
     */
    public abstract int getColumnCount();

    /**
     * Returns the column name.
     *
     * @param column Column number
     * @return Column name
     */
    public abstract String getColumnName(int column);

    /**
     * Returns the type (class) of a column.
     *
     * @param column Column number
     * @return Class
     */
    public abstract Class<?> getColumnClass(int column);

    /**
     * Returns the value of a node in a column.
     *
     * @param node   Node
     * @param column Column number
     * @return Value of the node in the column
     */
    public abstract Object getValueAt(Object node, int column);

    /**
     * Check if a cell of a node in one column is editable.
     *
     * @param node   Node
     * @param column Column number
     * @return true/false
     */
    public abstract boolean isCellEditable(Object node, int column);

    /**
     * Sets a value for a node in one column.
     *
     * @param aValue New value
     * @param node   Node
     * @param column Column number
     */
    public abstract void setValueAt(Object aValue, Object node, int column);

}