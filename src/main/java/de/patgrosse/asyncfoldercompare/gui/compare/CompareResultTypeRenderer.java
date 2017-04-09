package de.patgrosse.asyncfoldercompare.gui.compare;

import de.patgrosse.asyncfoldercompare.constants.CompleteObjectCompareResult;
import de.patgrosse.asyncfoldercompare.constants.PluginCompareResult;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CompareResultTypeRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 5147296913944333229L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof CompleteObjectCompareResult) {
            CompleteObjectCompareResult result = (CompleteObjectCompareResult) value;
            switch (result) {
                case MATCH:
                    setForeground(Color.LIGHT_GRAY);
                    break;
                case NEW:
                    setForeground(Color.GREEN);
                    break;
                case DELETED:
                    setForeground(Color.RED);
                    break;
                case PREFERNEW:
                case PREFEROLD:
                case DIFFER:
                case UNDEFINED:
                    setForeground(Color.BLUE);
                    break;
            }
        } else if (value instanceof PluginCompareResult) {
            PluginCompareResult result = (PluginCompareResult) value;
            switch (result) {
                case MATCH:
                    setForeground(Color.LIGHT_GRAY);
                    break;
                case PREFERNEW:
                case PREFEROLD:
                case DIFFER:
                case UNDEFINED:
                    setForeground(Color.BLUE);
                    break;
            }
        }
        return this;
    }

}
