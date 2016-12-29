package de.patgrosse.asyncfoldercompare.gui.treetable;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import de.patgrosse.asyncfoldercompare.constants.CompleteObjectCompareResult;
import de.patgrosse.asyncfoldercompare.entities.filesystem.ResultPathObject;

public class TreeTableTreeCellRender extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = 8639002406698837245L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf,
                                                  int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
        if (!sel) {
            if (value instanceof ResultPathObject<?>) {
                ResultPathObject<?> result = (ResultPathObject<?>) value;
                if (result.getCompareResult() == CompleteObjectCompareResult.MATCH) {
                    setForeground(Color.LIGHT_GRAY);
                }
            }
        }
        return this;
    }
}