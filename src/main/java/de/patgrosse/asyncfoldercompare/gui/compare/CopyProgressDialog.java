package de.patgrosse.asyncfoldercompare.gui.compare;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;

public class CopyProgressDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = -8826862791563897519L;

    private boolean canceled;
    private ReorderableTableModel tableModel;

    private JProgressBar progressBar;
    private JLabel headerLabel, descriptionLabel, totalPercentageLabel, listLabel;
    private JButton cancelButton;
    private JScrollPane listScrollPane;
    private JTable listTable;

    public CopyProgressDialog(Frame parent) {
        this(parent, false, null);
    }

    public CopyProgressDialog(Frame parent, String[] columnTitles) {
        this(parent, true, columnTitles);
    }

    private CopyProgressDialog(Frame parent, boolean withMultiProgress, String[] columnTitles) {
        super(parent);
        canceled = false;

        Dimension initSize = new Dimension(400, 250);
        setSize(initSize);
        setMinimumSize(initSize);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        headerLabel = new JLabel();
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        GridBagConstraints gbcHeader = new GridBagConstraints();
        gbcHeader.gridx = 0;
        gbcHeader.gridy = 0;
        gbcHeader.weightx = 1.0;
        gbcHeader.gridwidth = 2;
        gbcHeader.fill = GridBagConstraints.HORIZONTAL;
        gbcHeader.insets = new Insets(10, 20, 10, 20);
        getContentPane().add(headerLabel, gbcHeader);

        descriptionLabel = new JLabel();
        descriptionLabel.setVerticalAlignment(JLabel.TOP);
        GridBagConstraints gbcDescription = new GridBagConstraints();
        gbcDescription.gridx = 0;
        gbcDescription.gridy = 1;
        gbcDescription.weightx = 1.0;
        gbcDescription.gridwidth = 2;
        if (withMultiProgress) {
            gbcDescription.fill = GridBagConstraints.HORIZONTAL;
        } else {
            gbcDescription.weighty = 1.0;
            gbcDescription.fill = GridBagConstraints.BOTH;
        }
        gbcDescription.insets = new Insets(0, 20, 0, 20);
        gbcDescription.anchor = GridBagConstraints.NORTH;
        getContentPane().add(descriptionLabel, gbcDescription);

        progressBar = new JProgressBar(0, 100);
        GridBagConstraints gbcProgressBar = new GridBagConstraints();
        gbcProgressBar.gridx = 0;
        gbcProgressBar.gridy = 2;
        gbcProgressBar.weightx = 1.0;
        gbcProgressBar.fill = GridBagConstraints.HORIZONTAL;
        gbcProgressBar.insets = new Insets(20, 20, 10, 20);
        getContentPane().add(progressBar, gbcProgressBar);

        totalPercentageLabel = new JLabel("0,0 %");
        totalPercentageLabel.setHorizontalAlignment(JLabel.CENTER);
        GridBagConstraints gbcTotalPercentage = new GridBagConstraints();
        gbcTotalPercentage.gridx = 1;
        gbcTotalPercentage.gridy = 2;
        gbcTotalPercentage.weightx = 0.3;
        gbcTotalPercentage.fill = GridBagConstraints.NONE;
        gbcTotalPercentage.insets = new Insets(20, 0, 10, 20);
        gbcTotalPercentage.anchor = GridBagConstraints.EAST;
        getContentPane().add(totalPercentageLabel, gbcTotalPercentage);

        cancelButton = new JButton("Cancel");
        cancelButton.setMnemonic('C');
        cancelButton.addActionListener(this);
        GridBagConstraints gbcCancel = new GridBagConstraints();
        gbcCancel.gridx = 0;
        gbcCancel.gridy = 3;
        gbcCancel.gridwidth = 2;
        gbcCancel.fill = GridBagConstraints.NONE;
        gbcCancel.insets = new Insets(0, 20, 10, 20);
        gbcCancel.anchor = GridBagConstraints.CENTER;
        getContentPane().add(cancelButton, gbcCancel);

        if (withMultiProgress) {
            listLabel = new JLabel("Waiting actions:");
            listLabel.setHorizontalAlignment(JLabel.LEFT);
            GridBagConstraints gbcListLabel = new GridBagConstraints();
            gbcListLabel.gridx = 0;
            gbcListLabel.gridy = 4;
            gbcListLabel.gridwidth = 2;
            gbcListLabel.anchor = GridBagConstraints.WEST;
            gbcListLabel.insets = new Insets(0, 20, 10, 20);
            getContentPane().add(listLabel, gbcListLabel);

            tableModel = new ReorderableTableModel();
            tableModel.setDataVector(new String[][]{}, columnTitles);
            tableModel.addColumn("");
            listTable = new JTable(tableModel);
            TableColumn cancelBtnColumn = listTable.getColumn(listTable.getColumnName(columnTitles.length));
            cancelBtnColumn.setCellRenderer(new JButtonRenderer());
            cancelBtnColumn.setCellEditor(new JButtonCellEditor((row, col) -> System.out.println("Buttontext: " + row + " " + col)));
            listTable.setDragEnabled(true);
            listTable.setDropMode(DropMode.INSERT_ROWS);
            listTable.setTransferHandler(new TableRowTransferHandler(listTable));
            listTable.setPreferredScrollableViewportSize(new Dimension(0, 0));
            listScrollPane = new JScrollPane(listTable);
            listScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            GridBagConstraints gbcListSP = new GridBagConstraints();
            gbcListSP.gridx = 0;
            gbcListSP.gridy = 5;
            gbcListSP.weightx = 1.0;
            gbcListSP.weighty = 1.0;
            gbcListSP.gridwidth = 2;
            gbcListSP.fill = GridBagConstraints.BOTH;
            getContentPane().add(listScrollPane, gbcListSP);
        }

        pack();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                userRequestedClose();
            }
        });
    }

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        CopyProgressDialog prog = new CopyProgressDialog(null, true, new String[]{"Name", "Operation"});
        prog.setProgress(20.1F);
        prog.setHeaderText("header");
        prog.setDescriptionText("description");
        prog.setVisible(true);
        prog.setWaitingOperations(new String[][]{{"File 1", "OLDNEW"}, {"FIle 2", "NEWOLD"}});
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelButton) {
            userRequestedClose();
        }
    }

    public void userRequestedClose() {
        int answer = JOptionPane.showConfirmDialog(this, "Do really want to cancel?", "Cancel operation",
                JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            close();
            canceled = true;
        }
    }

    public boolean isCanceled() {
        return canceled;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JLabel getHeaderLabel() {
        return headerLabel;
    }

    public JLabel getDescriptionLabel() {
        return descriptionLabel;
    }

    public JLabel getTotalPercentageLabel() {
        return totalPercentageLabel;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public void setHeaderText(String header) {
        headerLabel.setText(header);
    }

    public void setDescriptionText(String description) {
        descriptionLabel.setText(description);
    }

    public void setProgress(float progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException();
        }
        progressBar.setValue((int) progress);
        totalPercentageLabel.setText(String.format("%5.1f %%", progress));
    }

    public void close() {
        setVisible(false);
        dispose();
    }

    public void setWaitingOperations(String[][] rows) {
        tableModel.setRowCount(0);
        for (String[] row : rows) {
            String[] newRow = new String[row.length + 1];
            System.arraycopy(row, 0, newRow, 0, row.length);
            newRow[row.length] = "Cancel";
            tableModel.addRow(newRow);
        }
    }

}
