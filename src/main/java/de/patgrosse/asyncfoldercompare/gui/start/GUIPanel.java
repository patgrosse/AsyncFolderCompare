package de.patgrosse.asyncfoldercompare.gui.start;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.patgrosse.asyncfoldercompare.entities.filesystem.result.ResultFolder;
import de.patgrosse.asyncfoldercompare.entities.filesystem.real.RootRealFolder;
import de.patgrosse.asyncfoldercompare.utils.VFSUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.vfs2.FileObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.patgrosse.asyncfoldercompare.gui.compare.AFCTreeGUI;
import de.patgrosse.asyncfoldercompare.utils.FileTreeComparator;

public class GUIPanel extends JPanel implements ActionListener, AFCSettingsHolder {
    private static final long serialVersionUID = -5693281016474396045L;
    private static final Logger LOG = LogManager.getLogger();

    private AFCGUIStarter parent;

    private JRadioButton oldFolder, oldJSON;
    private JRadioButton newFolder, newJSON;
    private JSeparator separatorBeforeOld, separatorBeforeNew, separatorAfterNew;

    private JLabel descriptionLabel;
    private JTextField oldField, newField;
    private JButton startButton, oldSelect, newSelect;

    public GUIPanel(AFCGUIStarter parent) {
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

        oldFolder = new JRadioButton("Enter/select old folder URI");
        oldFolder.setSelected(!parent.getLastSettings().isOldURIIsJSON());
        GridBagConstraints gbcOldFolder = new GridBagConstraints();
        gbcOldFolder.gridx = 0;
        gbcOldFolder.gridy = 2;
        gbcOldFolder.anchor = GridBagConstraints.WEST;
        gbcOldFolder.insets = new Insets(10, 10, 0, 10);
        add(oldFolder, gbcOldFolder);

        oldJSON = new JRadioButton("Enter/select old JSON file URI");
        oldJSON.setSelected(parent.getLastSettings().isOldURIIsJSON());
        GridBagConstraints gbcOldJSON = new GridBagConstraints();
        gbcOldJSON.gridx = 0;
        gbcOldJSON.gridy = 3;
        gbcOldJSON.anchor = GridBagConstraints.WEST;
        gbcOldJSON.insets = new Insets(0, 10, 10, 10);
        add(oldJSON, gbcOldJSON);

        ButtonGroup groupOld = new ButtonGroup();
        groupOld.add(oldFolder);
        groupOld.add(oldJSON);

        oldField = new JTextField(parent.getLastSettings().getOldURI());
        GridBagConstraints gbcOldField = new GridBagConstraints();
        gbcOldField.gridx = 1;
        gbcOldField.gridy = 2;
        gbcOldField.gridheight = 2;
        gbcOldField.weightx = 1.0;
        gbcOldField.fill = GridBagConstraints.HORIZONTAL;
        add(oldField, gbcOldField);

        oldSelect = new JButton("Lokal...");
        oldSelect.addActionListener(this);
        GridBagConstraints gbcOldSelect = new GridBagConstraints();
        gbcOldSelect.gridx = 2;
        gbcOldSelect.gridy = 2;
        gbcOldSelect.gridheight = 2;
        gbcOldSelect.insets = new Insets(10, 0, 10, 10);
        add(oldSelect, gbcOldSelect);

        separatorBeforeNew = new JSeparator();
        GridBagConstraints gbcSepNew = new GridBagConstraints();
        gbcSepNew.gridwidth = 3;
        gbcSepNew.gridx = 0;
        gbcSepNew.gridy = 4;
        gbcSepNew.weightx = 1.0;
        gbcSepNew.fill = GridBagConstraints.HORIZONTAL;
        add(separatorBeforeNew, gbcSepNew);

        newFolder = new JRadioButton("Enter/select new folder URI");
        newFolder.setSelected(!parent.getLastSettings().isNewURIIsJSON());
        GridBagConstraints gbcNewFolder = new GridBagConstraints();
        gbcNewFolder.gridx = 0;
        gbcNewFolder.gridy = 5;
        gbcNewFolder.anchor = GridBagConstraints.WEST;
        gbcNewFolder.insets = new Insets(10, 10, 0, 10);
        add(newFolder, gbcNewFolder);

        newJSON = new JRadioButton("Enter/select new JSON file URI");
        newJSON.setSelected(parent.getLastSettings().isNewURIIsJSON());
        GridBagConstraints gbcNewJSON = new GridBagConstraints();
        gbcNewJSON.gridx = 0;
        gbcNewJSON.gridy = 6;
        gbcNewJSON.anchor = GridBagConstraints.WEST;
        gbcNewJSON.insets = new Insets(0, 10, 10, 10);
        add(newJSON, gbcNewJSON);

        ButtonGroup groupNew = new ButtonGroup();
        groupNew.add(newFolder);
        groupNew.add(newJSON);

        newField = new JTextField(parent.getLastSettings().getNewURI());
        GridBagConstraints gbcNewField = new GridBagConstraints();
        gbcNewField.gridx = 1;
        gbcNewField.gridy = 5;
        gbcNewField.gridheight = 2;
        gbcNewField.weightx = 1.0;
        gbcNewField.fill = GridBagConstraints.HORIZONTAL;
        add(newField, gbcNewField);

        newSelect = new JButton("Lokal...");
        newSelect.addActionListener(this);
        GridBagConstraints gbcNewSelect = new GridBagConstraints();
        gbcNewSelect.gridx = 2;
        gbcNewSelect.gridy = 5;
        gbcNewSelect.gridheight = 2;
        gbcNewSelect.insets = new Insets(10, 0, 10, 10);
        add(newSelect, gbcNewSelect);

        separatorAfterNew = new JSeparator();
        GridBagConstraints gbcSepNewAfter = new GridBagConstraints();
        gbcSepNewAfter.gridwidth = 3;
        gbcSepNewAfter.gridx = 0;
        gbcSepNewAfter.gridy = 7;
        gbcSepNewAfter.weightx = 1.0;
        gbcSepNewAfter.fill = GridBagConstraints.HORIZONTAL;
        add(separatorAfterNew, gbcSepNewAfter);

        startButton = new JButton("Compare folders");
        startButton.addActionListener(this);
        GridBagConstraints gbcStart = new GridBagConstraints();
        gbcStart.gridwidth = 3;
        gbcStart.gridx = 0;
        gbcStart.gridy = 8;
        gbcStart.weightx = 1.0;
        gbcStart.weighty = 1.0;
        gbcStart.fill = GridBagConstraints.NONE;
        gbcStart.anchor = GridBagConstraints.NORTH;
        gbcStart.insets = new Insets(10, 10, 10, 10);
        add(startButton, gbcStart);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == oldSelect) {
            startFileChooser(oldFolder, oldField);
        } else if (e.getSource() == newSelect) {
            startFileChooser(newFolder, newField);
        } else if (e.getSource() == startButton) {
            if (oldField.getText().trim().length() == 0) {
                JOptionPane.showMessageDialog(this, "Please select an old folder/JSON file!", "Incomplete",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newField.getText().trim().length() == 0) {
                JOptionPane.showMessageDialog(this, "Please select a new folder/JSON file!", "Incomplete",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            handleStart();
        }
    }

    private void startFileChooser(JRadioButton folderButton, JTextField field) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        if (folderButton.isSelected()) {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
        } else {
            chooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
        }
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            field.setText(chooser.getSelectedFile().toURI().toString());
        }
    }

    private void handleStart() {
        FileTreeComparator tc = VFSUtils.createTreeComparator();
        Pair<RootRealFolder, FileObject> oldFolder, newFolder;
        ResultFolder resFolder;
        try {
            oldFolder = VFSUtils.parseUserInput(tc, oldField.getText(), oldJSON.isSelected(),
                    parent.getLastSettings().getCredentialsOld());
            newFolder = VFSUtils.parseUserInput(tc, newField.getText(), newJSON.isSelected(),
                    parent.getLastSettings().getCredentialsNew());
            LOG.info("Starting compare");
            resFolder = tc.compareFolders(oldFolder.getLeft(), newFolder.getLeft());
            LOG.info("Finished compare");
        } catch (IOException e) {
            LOG.error("Error while comparing directories", e);
            String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            JOptionPane.showMessageDialog(this,
                    "Error while accessing file system! Check paths and rights/credentials!\nError message: " + message,
                    "Could not compare folders", JOptionPane.ERROR_MESSAGE);
            return;
        }
        AFCTreeGUI.startGUI(tc, resFolder, oldFolder.getRight(), newFolder.getRight());
        try {
            parent.quit();
        } catch (IOException e) {
            LOG.error("Error while closing starter frame", e);
        }
    }

    @Override
    public void saveSettings() {
        parent.getLastSettings().setOldURI(oldField.getText());
        parent.getLastSettings().setOldURIIsJSON(oldJSON.isSelected());
        parent.getLastSettings().setNewURI(newField.getText());
        parent.getLastSettings().setNewURIIsJSON(newJSON.isSelected());
    }

}
