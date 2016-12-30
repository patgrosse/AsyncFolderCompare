package de.patgrosse.asyncfoldercompare.gui.compare;

import java.util.LinkedList;
import java.util.List;

import de.patgrosse.asyncfoldercompare.plugins.entities.CompareCheck;
import org.apache.commons.lang3.tuple.Pair;

import de.patgrosse.asyncfoldercompare.constants.CompleteObjectCompareResult;
import de.patgrosse.asyncfoldercompare.entities.compareresults.CompleteFileCompareResultHolder;
import de.patgrosse.asyncfoldercompare.entities.compareresults.PluginFileCompareResultHolder;
import de.patgrosse.asyncfoldercompare.entities.filesystem.ResultFile;
import de.patgrosse.asyncfoldercompare.entities.filesystem.ResultFolder;
import de.patgrosse.asyncfoldercompare.gui.treetable.AbstractTreeTableModel;
import de.patgrosse.asyncfoldercompare.plugins.ComparePlugin;
import de.patgrosse.asyncfoldercompare.utils.FileTreeComparator;

public class ResultFolderModel extends AbstractTreeTableModel {
    private List<String> columnNames;
    private List<Class<?>> columnTypes;
    private List<Pair<ComparePlugin, CompareCheck>> checkColumns;

    public ResultFolderModel(FileTreeComparator comp, ResultFolder resultFolder) {
        super(resultFolder);
        columnNames = new LinkedList<>();
        columnTypes = new LinkedList<>();
        columnNames.add("Dateibaum");
        columnTypes.add(AbstractTreeTableModel.class);
        columnNames.add("Gesamtergebnis");
        columnTypes.add(CompleteObjectCompareResult.class);
        checkColumns = comp.getPluginCompareResultColumns();
        for (Pair<ComparePlugin, CompareCheck> column : checkColumns) {
            columnNames.add(column.getRight().getDisplayName());
            columnTypes.add(CompleteObjectCompareResult.class);
        }
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent instanceof ResultFolder) {
            ResultFolder parentF = (ResultFolder) parent;
            if (index < parentF.getContainedFolders().size()) {
                int i = 0;
                for (ResultFolder rf : parentF.getContainedFolders()) {
                    if (i == index) {
                        return rf;
                    }
                    i++;
                }
            } else {
                int runningIndex = index - parentF.getContainedFolders().size();
                int i = 0;
                for (ResultFile rf : parentF.getContainedFiles()) {
                    if (i == runningIndex) {
                        return rf;
                    }
                    i++;
                }
            }
            return null;
        } else {
            return null;
        }
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof ResultFolder) {
            ResultFolder folder = (ResultFolder) parent;
            return folder.getContainedFiles().size() + folder.getContainedFolders().size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isLeaf(Object node) {
        return node instanceof ResultFile;
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames.get(column);
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return columnTypes.get(column);
    }

    @Override
    public Object getValueAt(Object node, int column) {
        if (node instanceof ResultFolder) {
            switch (column) {
                case -1:
                    return node;
                case 0:
                    return ((ResultFolder) node).getName();
                case 1:
                    return ((ResultFolder) node).getCompareResult();
                default:
                    break;
            }
        } else if (node instanceof ResultFile) {
            switch (column) {
                case -1:
                    return node;
                case 0:
                    return ((ResultFile) node).getName();
                case 1:
                    return ((ResultFile) node).getCompareResult();
                default:
                    int checkNumber = column - 2;
                    Pair<ComparePlugin, CompareCheck> check = checkColumns.get(checkNumber);
                    if (check != null) {
                        CompleteFileCompareResultHolder fullResult = ((ResultFile) node).getFullResult();
                        if (fullResult != null) {
                            PluginFileCompareResultHolder pluginResult = fullResult.getPluginResults(check.getLeft().getName());
                            if (pluginResult != null) {
                                return pluginResult.getSubResults().get(check.getRight().getKeyName());
                            }
                        }
                    }
                    break;
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, Object node, int column) {
    }

}