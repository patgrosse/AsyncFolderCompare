package de.patgrosse.asyncfoldercompare.gui.compare;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

class JButtonCellEditor extends AbstractCellEditor implements TableCellEditor {
    private static final long serialVersionUID = -6150751817746930839L;

    private JButton button;
    private int row, col;

    public JButtonCellEditor(final JButtonCellListener listener) {
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> listener.handleButtonPressed(row, col));
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }

    @Override
    public boolean stopCellEditing() {
        return super.stopCellEditing();
    }

    @Override
    public void cancelCellEditing() {
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
        this.row = row;
        this.col = col;
        String txt = (value == null) ? "" : value.toString();
        button.setText(txt);
        return button;
    }

    public interface JButtonCellListener {
        void handleButtonPressed(int row, int col);
    }
}