package de.patgrosse.asyncfoldercompare.gui.treetable;

import javax.swing.ListSelectionModel;
import javax.swing.tree.DefaultTreeSelectionModel;

public class TreeTableSelectionModel extends DefaultTreeSelectionModel {
    private static final long serialVersionUID = -233277525984640818L;

    public TreeTableSelectionModel() {
        getListSelectionModel().addListSelectionListener(e -> {
        });
    }

    protected ListSelectionModel getListSelectionModel() {
        return listSelectionModel;
    }
}