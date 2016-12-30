package de.patgrosse.asyncfoldercompare.gui.start;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.patgrosse.asyncfoldercompare.utils.VFSUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.patgrosse.asyncfoldercompare.utils.FileTreeComparator;
import de.patgrosse.asyncfoldercompare.utils.GsonUtils;

public class JSONPanel extends JPanel implements AFCSettingsHolder, ActionListener {
    private static final long serialVersionUID = -6418194212783524372L;
    private static final Logger LOG = LogManager.getLogger();

    private AFCGUIStarter parent;

    private JLabel descriptionLabel, folderLabel, jsonLabel;
    private JSeparator separatorBeforeOld, separatorBeforeNew, separatorAfterNew;
    private JTextField folderField, jsonField;
    private JButton startButton, folderSelect, jsonSelect;

    public JSONPanel(AFCGUIStarter parent) {
        this.parent = parent;
        setLayout(new GridBagLayout());

        descriptionLabel = new JLabel("Description");
        GridBagConstraints gbcDescription = new GridBagConstraints();
        gbcDescription.gridwidth = 3;
        gbcDescription.gridx = 0;
        gbcDescription.gridy = 0;
        gbcDescription.weightx = 1.0;
        gbcDescription.fill = GridBagConstraints.HORIZONTAL;
        gbcDescription.anchor = GridBagConstraints.NORTHWEST;
        gbcDescription.insets = new Insets(10, 10, 10, 10);
        add(descriptionLabel, gbcDescription);

        separatorBeforeOld = new JSeparator();
        GridBagConstraints gbcSepOld = new GridBagConstraints();
        gbcSepOld.gridwidth = 3;
        gbcSepOld.gridx = 0;
        gbcSepOld.gridy = 1;
        gbcSepOld.weightx = 1.0;
        gbcSepOld.fill = GridBagConstraints.HORIZONTAL;
        add(separatorBeforeOld, gbcSepOld);

        folderLabel = new JLabel("Choose/select folder");
        GridBagConstraints gbcFolderLabel = new GridBagConstraints();
        gbcFolderLabel.gridx = 0;
        gbcFolderLabel.gridy = 2;
        gbcFolderLabel.anchor = GridBagConstraints.WEST;
        gbcFolderLabel.insets = new Insets(10, 10, 10, 10);
        add(folderLabel, gbcFolderLabel);

        folderField = new JTextField(parent.getLastSettings().getMapjsonFolderURI());
        GridBagConstraints gbcFolderField = new GridBagConstraints();
        gbcFolderField.gridx = 1;
        gbcFolderField.gridy = 2;
        gbcFolderField.weightx = 1.0;
        gbcFolderField.fill = GridBagConstraints.HORIZONTAL;
        add(folderField, gbcFolderField);

        folderSelect = new JButton("Lokal...");
        folderSelect.addActionListener(this);
        GridBagConstraints gbcFolderSelect = new GridBagConstraints();
        gbcFolderSelect.gridx = 2;
        gbcFolderSelect.gridy = 2;
        gbcFolderSelect.insets = new Insets(10, 0, 10, 10);
        add(folderSelect, gbcFolderSelect);

        separatorBeforeNew = new JSeparator();
        GridBagConstraints gbcSepNew = new GridBagConstraints();
        gbcSepNew.gridwidth = 3;
        gbcSepNew.gridx = 0;
        gbcSepNew.gridy = 3;
        gbcSepNew.weightx = 1.0;
        gbcSepNew.fill = GridBagConstraints.HORIZONTAL;
        add(separatorBeforeNew, gbcSepNew);

        jsonLabel = new JLabel("Choose/select target JSON file");
        GridBagConstraints gbcJsonLabel = new GridBagConstraints();
        gbcJsonLabel.gridx = 0;
        gbcJsonLabel.gridy = 4;
        gbcJsonLabel.anchor = GridBagConstraints.WEST;
        gbcJsonLabel.insets = new Insets(10, 10, 10, 10);
        add(jsonLabel, gbcJsonLabel);

        jsonField = new JTextField(parent.getLastSettings().getMapjsonJSONURI());
        GridBagConstraints gbcJsonField = new GridBagConstraints();
        gbcJsonField.gridx = 1;
        gbcJsonField.gridy = 4;
        gbcJsonField.weightx = 1.0;
        gbcJsonField.fill = GridBagConstraints.HORIZONTAL;
        add(jsonField, gbcJsonField);

        jsonSelect = new JButton("Lokal...");
        jsonSelect.addActionListener(this);
        GridBagConstraints gbcJsonSelect = new GridBagConstraints();
        gbcJsonSelect.gridx = 2;
        gbcJsonSelect.gridy = 4;
        gbcJsonSelect.insets = new Insets(10, 0, 10, 10);
        add(jsonSelect, gbcJsonSelect);

        separatorAfterNew = new JSeparator();
        GridBagConstraints gbcSepNewAfter = new GridBagConstraints();
        gbcSepNewAfter.gridwidth = 3;
        gbcSepNewAfter.gridx = 0;
        gbcSepNewAfter.gridy = 5;
        gbcSepNewAfter.weightx = 1.0;
        gbcSepNewAfter.fill = GridBagConstraints.HORIZONTAL;
        add(separatorAfterNew, gbcSepNewAfter);

        startButton = new JButton("Generate JSON file");
        startButton.addActionListener(this);
        GridBagConstraints gbcStart = new GridBagConstraints();
        gbcStart.gridwidth = 3;
        gbcStart.gridx = 0;
        gbcStart.gridy = 6;
        gbcStart.weightx = 1.0;
        gbcStart.weighty = 1.0;
        gbcStart.fill = GridBagConstraints.NONE;
        gbcStart.anchor = GridBagConstraints.NORTH;
        gbcStart.insets = new Insets(10, 10, 10, 10);
        add(startButton, gbcStart);
    }

    @Override
    public void saveSettings() {
        parent.getLastSettings().setMapjsonFolderURI(folderField.getText());
        parent.getLastSettings().setMapjsonJSONURI(jsonField.getText());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == folderSelect) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                folderField.setText(chooser.getSelectedFile().toURI().toString());
            }
        } else if (e.getSource() == jsonSelect) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                jsonField.setText(chooser.getSelectedFile().toURI().toString());
            }
        } else if (e.getSource() == startButton) {
            if (folderField.getText().trim().length() == 0) {
                JOptionPane.showMessageDialog(this, "Please select a folder!", "Incomplete", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (jsonField.getText().trim().length() == 0) {
                JOptionPane.showMessageDialog(this, "Please select a JSON file!", "Incomplete",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            handleStart();
        }
    }

    private void handleStart() {
        FileTreeComparator tc = VFSUtils.createTreeComparator();
        try {
            LOG.info("Starting mapping");
            GsonUtils.saveFolderToJSON(tc, folderField.getText(), parent.getLastSettings().getCredentialsOld(),
                    jsonField.getText(), parent.getLastSettings().getCredentialsNew());
            LOG.info("Finished mapping");
        } catch (IOException e) {
            LOG.error("Error while mapping directories", e);
            String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            JOptionPane.showMessageDialog(this,
                    "Error while accessing file system! Check paths and rights/credentials!\nError message: " + message,
                    "Could not map folder", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Successfully mapped folder to JSON file", "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

}
