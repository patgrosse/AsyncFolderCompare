package de.patgrosse.asyncfoldercompare.gui.compare;

import de.patgrosse.asyncfoldercompare.constants.PluginCompareResult;
import de.patgrosse.asyncfoldercompare.entities.filesystem.ResultPathObject;
import de.patgrosse.asyncfoldercompare.entities.filesystem.result.ResultFile;
import de.patgrosse.asyncfoldercompare.entities.filesystem.result.ResultFolder;
import de.patgrosse.asyncfoldercompare.gui.GUITools;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AFCDetailsDialog extends JDialog {
    private ResultPathObject<?> resultPathObject;
    private JTable table;

    public AFCDetailsDialog(AFCTreeGUI parent, ResultPathObject<?> resultPathObject) {
        super(parent, true);
        this.resultPathObject = resultPathObject;
        setSize(new Dimension(600, 600));
        GUITools.centerFrameInScreen(this);
        getContentPane().setLayout(new GridBagLayout());
        {
            DefaultTableModel model = new DefaultTableModel(new Object[][]{},
                    new String[]{"Property", "Old file", "New file", "Compare results"}) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 3) {
                        return PluginCompareResult.class;
                    }
                    return super.getColumnClass(columnIndex);
                }
            };
            table = new JTable(model);
            table.setShowGrid(false);
            table.setDragEnabled(false);
            table.getTableHeader().setReorderingAllowed(false);
            table.setDefaultRenderer(PluginCompareResult.class, new CompareResultTypeRenderer());
        }
        fillTable();
        {
            JScrollPane spTable = new JScrollPane(table);
            GridBagConstraints gbcTable = new GridBagConstraints();
            gbcTable.fill = GridBagConstraints.BOTH;
            gbcTable.weightx = 1;
            gbcTable.weighty = 2;
            getContentPane().add(spTable, gbcTable);
        }
    }

    private void fillTable() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        if (resultPathObject instanceof ResultFile) {
            ResultFile file = (ResultFile) resultPathObject;
            model.addRow(new Object[]{"Total", "", "", file.getCompareResult()});
            file.getFullResult().getPluginResults().forEach((plugin, resultHolder) -> {
                model.addRow(new Object[]{"- " + plugin.getName(), "", "", resultHolder.getTotal()});
                resultHolder.getSubResults().forEach((check, singleResult) -> {
                    String oldData = check.doFormatOutput(
                            file.getCorrespondingOld().getDataStorage().getData(check.getKeyName()
                            ));
                    String newData = check.doFormatOutput(
                            file.getCorrespondingNew().getDataStorage().getData(check.getKeyName()
                            ));
                    model.addRow(new Object[]{"- - " + check.getDisplayName(), oldData, newData, singleResult});
                });
            });
        } else if (resultPathObject instanceof ResultFolder) {
            ResultFolder folder = (ResultFolder) resultPathObject;
            model.addRow(new Object[]{"Total", "", "", folder.getCompareResult()});
        }
    }
}
