package de.patgrosse.asyncfoldercompare.gui.start;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import de.patgrosse.asyncfoldercompare.entities.storage.LastSettings;
import de.patgrosse.asyncfoldercompare.gui.GUITools;
import de.patgrosse.asyncfoldercompare.startup.AFCEnviroment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.patgrosse.asyncfoldercompare.utils.GsonUtils;

public class AFCGUIStarter extends JFrame implements ActionListener {
    private static final long serialVersionUID = -6151375830605793263L;
    private static final Logger LOG = LogManager.getLogger();

    private LastSettings lastSettings;

    private JLabel descriptionLabel;
    private JButton startCredentials;
    private GUIPanel guiPanel;
    private JSONPanel jsonPanel;
    private CredentialsDialog credDialog;

    public AFCGUIStarter() {
        lastSettings = GsonUtils.readLastSettings();

        setTitle("AsyncFolderCompare");
        setLayout(new GridBagLayout());
        Dimension initSize = new Dimension(700, 500);
        setMinimumSize(initSize);
        setSize(initSize);
        GUITools.centerFrameInScreen(this);

        descriptionLabel = new JLabel("Description");
        GridBagConstraints gbcDescription = new GridBagConstraints();
        gbcDescription.gridx = 0;
        gbcDescription.gridy = 0;
        gbcDescription.weightx = 1.0;
        gbcDescription.fill = GridBagConstraints.HORIZONTAL;
        gbcDescription.anchor = GridBagConstraints.NORTHWEST;
        gbcDescription.insets = new Insets(10, 10, 10, 10);
        add(descriptionLabel, gbcDescription);

        startCredentials = new JButton("Change credentials");
        startCredentials.addActionListener(this);
        GridBagConstraints gbcStartCredentials = new GridBagConstraints();
        gbcStartCredentials.gridx = 1;
        gbcStartCredentials.gridy = 0;
        gbcStartCredentials.insets = new Insets(10, 10, 10, 10);
        add(startCredentials, gbcStartCredentials);

        JTabbedPane pane = new JTabbedPane();
        guiPanel = new GUIPanel(this);
        pane.add("Compare folders", guiPanel);
        jsonPanel = new JSONPanel(this);
        pane.add("Generate JSON file", jsonPanel);
        GridBagConstraints gbcPane = new GridBagConstraints();
        gbcPane.gridx = 0;
        gbcPane.gridy = 2;
        gbcPane.gridwidth = 2;
        gbcPane.weightx = 1.0;
        gbcPane.weighty = 1.0;
        gbcPane.fill = GridBagConstraints.BOTH;
        getContentPane().add(pane, gbcPane);

        pack();
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    quit();
                } catch (IOException e1) {
                    LOG.error("Error while quitting", e1);
                }
            }
        });
    }

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        AFCEnviroment.initJVM();
        new AFCGUIStarter();
    }

    public LastSettings getLastSettings() {
        return lastSettings;
    }

    public void quit() throws IOException {
        dispose();
        if (credDialog != null) {
            credDialog.dispose();
            credDialog.saveSettings();
        }
        guiPanel.saveSettings();
        jsonPanel.saveSettings();
        GsonUtils.saveLastSettings(lastSettings);
    }

    public void showCredentials() {
        credDialog = new CredentialsDialog(this);
        credDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                credDialog = null;
            }
        });
        credDialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startCredentials) {
            showCredentials();
        }
    }

}
