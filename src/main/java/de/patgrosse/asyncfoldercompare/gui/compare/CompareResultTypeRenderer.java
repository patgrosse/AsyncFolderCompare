package de.patgrosse.asyncfoldercompare.gui.compare;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import de.patgrosse.asyncfoldercompare.constants.CompleteObjectCompareResult;

public class CompareResultTypeRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 5147296913944333229L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof CompleteObjectCompareResult) {
            CompleteObjectCompareResult result = (CompleteObjectCompareResult) value;
            if (result == CompleteObjectCompareResult.MATCH) {
                setForeground(Color.LIGHT_GRAY);
            } else {
                setForeground(Color.RED);
            }
        }
        return this;
    }

}
