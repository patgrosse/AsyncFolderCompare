package de.patgrosse.asyncfoldercompare.gui.compare;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

class JButtonRenderer implements TableCellRenderer {
    private JButton button;

    public JButtonRenderer() {
        this.button = new JButton();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);
        button.setText(value.toString());
        return button;
    }
}