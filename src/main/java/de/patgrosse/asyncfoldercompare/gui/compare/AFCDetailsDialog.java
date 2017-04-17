package de.patgrosse.asyncfoldercompare.gui.compare;

import de.patgrosse.asyncfoldercompare.constants.PluginCompareResult;
import de.patgrosse.asyncfoldercompare.entities.filesystem.ResultPathObject;
import de.patgrosse.asyncfoldercompare.entities.filesystem.real.RealFile;
import de.patgrosse.asyncfoldercompare.entities.filesystem.result.ResultFile;
import de.patgrosse.asyncfoldercompare.entities.filesystem.result.ResultFolder;
import de.patgrosse.asyncfoldercompare.gui.GUITools;
import de.patgrosse.asyncfoldercompare.plugins.ComparePlugin;
import de.patgrosse.asyncfoldercompare.plugins.entities.CompareCheck;
import de.patgrosse.asyncfoldercompare.utils.CompareCheckReference;
import de.patgrosse.asyncfoldercompare.utils.FileTreeComparator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

public class AFCDetailsDialog extends JDialog {
    private FileTreeComparator comp;
    private ResultPathObject<?> resultPathObject;
    private JTable table;

    public AFCDetailsDialog(AFCTreeGUI parent, ResultPathObject<?> resultPathObject) {
        super(parent, true);
        this.comp = parent.getFileTreeComparator();
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
            RealFile correspondingOld = file.getCorrespondingOld();
            RealFile correspondingNew = file.getCorrespondingNew();
            model.addRow(new Object[]{"Total", "", "", file.getCompareResult()});
            if (file.getFullResult() != null) {
                file.getFullResult().getPluginResults().forEach((plugin, resultHolder) -> {
                    model.addRow(new Object[]{"- " + plugin.getName(), "", "", resultHolder.getTotal()});
                    resultHolder.getSubResults().forEach((check, singleResult) -> {
                        String oldData = check.doFormatOutput(
                                correspondingOld.getDataStorage().getData(check
                                ));
                        String newData = check.doFormatOutput(
                                correspondingNew.getDataStorage().getData(check
                                ));
                        model.addRow(new Object[]{"- - " + check.getDisplayName(), oldData, newData, singleResult});
                    });
                });
            } else {
                TreeMap<CompareCheckReference, String> sorted = new TreeMap<>((o1, o2) -> {
                    int res1 = o1.getPluginRef().compareTo(o2.getPluginRef());
                    if (res1 == 0) {
                        return o1.getCheckRef().compareTo(o2.getCheckRef());
                    } else {
                        return res1;
                    }
                });
                if (correspondingOld != null) {
                    sorted.putAll(correspondingOld.getDataStorage().getAllData());
                } else {
                    sorted.putAll(correspondingNew.getDataStorage().getAllData());
                }
                ComparePlugin lastPlugin = null;
                for (Map.Entry<CompareCheckReference, String> data : sorted.entrySet()) {
                    CompareCheck check = comp.getCompareCheck(data.getKey());
                    if (check != null) {
                        if (!check.getPlugin().equals(lastPlugin)) {
                            model.addRow(new Object[]{"- " + check.getPlugin().getName(), "", "", ""});
                            lastPlugin = check.getPlugin();
                        }
                        if (correspondingOld != null) {
                            model.addRow(new Object[]{"- - " + check.getDisplayName(), check.doFormatOutput(data.getValue()), "", ""});
                        } else {
                            model.addRow(new Object[]{"- - " + check.getDisplayName(), "", check.doFormatOutput(data.getValue()), ""});
                        }
                    }
                }
            }
        } else if (resultPathObject instanceof ResultFolder) {
            ResultFolder folder = (ResultFolder) resultPathObject;
            model.addRow(new Object[]{"Total", "", "", folder.getCompareResult()});
        }
    }
}
