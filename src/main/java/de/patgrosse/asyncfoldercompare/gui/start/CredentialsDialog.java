package de.patgrosse.asyncfoldercompare.gui.start;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class CredentialsDialog extends JDialog implements AFCSettingsHolder {
    private static final long serialVersionUID = 6593740400698562171L;

    private AFCGUIStarter parent;

    private JLabel oldDesc, newDesc;
    private CredentialPanel oldCred, newCred;
    private JButton closeButton;

    public CredentialsDialog(AFCGUIStarter parent) {
        super(parent, true);
        this.parent = parent;
        setLayout(new GridBagLayout());
        setTitle("Change credentials");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        oldDesc = new JLabel("<html><b>Credentials for old folder:</b></html>");
        GridBagConstraints gbcOldDesc = new GridBagConstraints();
        gbcOldDesc.gridx = 0;
        gbcOldDesc.gridy = 0;
        gbcOldDesc.weightx = 1.0;
        gbcOldDesc.anchor = GridBagConstraints.WEST;
        gbcOldDesc.insets = new Insets(10, 10, 10, 10);
        add(oldDesc, gbcOldDesc);

        oldCred = new CredentialPanel(parent.getLastSettings().getCredentialsOld().getDomain(),
                parent.getLastSettings().getCredentialsOld().getUser(),
                parent.getLastSettings().getCredentialsOld().getPassword());
        GridBagConstraints gbcOldCred = new GridBagConstraints();
        gbcOldCred.gridx = 0;
        gbcOldCred.gridy = 1;
        gbcOldCred.weightx = 1.0;
        gbcOldCred.fill = GridBagConstraints.HORIZONTAL;
        gbcOldCred.anchor = GridBagConstraints.WEST;
        gbcOldCred.insets = new Insets(0, 10, 10, 10);
        add(oldCred, gbcOldCred);

        newDesc = new JLabel("<html><b>Credentials for new folder:</b></html>");
        GridBagConstraints gbcNewDesc = new GridBagConstraints();
        gbcNewDesc.gridx = 0;
        gbcNewDesc.gridy = 2;
        gbcNewDesc.weightx = 1.0;
        gbcNewDesc.anchor = GridBagConstraints.WEST;
        gbcNewDesc.insets = new Insets(10, 10, 10, 10);
        add(newDesc, gbcNewDesc);

        newCred = new CredentialPanel(parent.getLastSettings().getCredentialsNew().getDomain(),
                parent.getLastSettings().getCredentialsNew().getUser(),
                parent.getLastSettings().getCredentialsNew().getPassword());
        GridBagConstraints gbcNewCred = new GridBagConstraints();
        gbcNewCred.gridx = 0;
        gbcNewCred.gridy = 3;
        gbcNewCred.weightx = 1.0;
        gbcNewCred.fill = GridBagConstraints.HORIZONTAL;
        gbcNewCred.anchor = GridBagConstraints.WEST;
        gbcNewCred.insets = new Insets(0, 10, 10, 10);
        add(newCred, gbcNewCred);

        closeButton = new JButton("Close");
        closeButton.setIcon(UIManager.getIcon("InternalFrame.closeIcon"));
        closeButton.addActionListener(e -> dispatchEvent(new WindowEvent(CredentialsDialog.this, WindowEvent.WINDOW_CLOSING)));
        GridBagConstraints gbcCloseButton = new GridBagConstraints();
        gbcCloseButton.gridx = 0;
        gbcCloseButton.gridy = 4;
        gbcNewCred.fill = GridBagConstraints.HORIZONTAL;
        gbcNewCred.insets = new Insets(10, 10, 10, 10);
        add(closeButton, gbcCloseButton);

        pack();
        setLocationRelativeTo(parent);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveSettings();
            }
        });
    }

    @Override
    public void saveSettings() {
        parent.getLastSettings().getCredentialsOld().setDomain(oldCred.getDomain());
        parent.getLastSettings().getCredentialsOld().setUser(oldCred.getUser());
        parent.getLastSettings().getCredentialsOld().setPassword(oldCred.getPassword());
        parent.getLastSettings().getCredentialsNew().setDomain(newCred.getDomain());
        parent.getLastSettings().getCredentialsNew().setUser(newCred.getUser());
        parent.getLastSettings().getCredentialsNew().setPassword(newCred.getPassword());
    }

    private static class CredentialPanel extends JPanel {
        private static final long serialVersionUID = 7826038409001435494L;

        private JLabel domainLabel, userLabel, passwordLabel;
        private JTextField domainField, userField, passwordField;

        public CredentialPanel(String domain, String user, String password) {
            setLayout(new GridBagLayout());

            domainLabel = new JLabel("Domain:");
            GridBagConstraints gbcDomainLabel = new GridBagConstraints();
            gbcDomainLabel.gridx = 0;
            gbcDomainLabel.gridy = 0;
            gbcDomainLabel.anchor = GridBagConstraints.WEST;
            add(domainLabel, gbcDomainLabel);

            domainField = new JTextField(domain);
            GridBagConstraints gbcDomainField = new GridBagConstraints();
            gbcDomainField.gridx = 1;
            gbcDomainField.gridy = 0;
            gbcDomainField.weightx = 1.0;
            gbcDomainField.fill = GridBagConstraints.HORIZONTAL;
            gbcDomainField.anchor = GridBagConstraints.WEST;
            add(domainField, gbcDomainField);

            userLabel = new JLabel("User:");
            GridBagConstraints gbcUserLabel = new GridBagConstraints();
            gbcUserLabel.gridx = 0;
            gbcUserLabel.gridy = 1;
            gbcUserLabel.anchor = GridBagConstraints.WEST;
            add(userLabel, gbcUserLabel);

            userField = new JTextField(user);
            GridBagConstraints gbcUserField = new GridBagConstraints();
            gbcUserField.gridx = 1;
            gbcUserField.gridy = 1;
            gbcUserField.weightx = 1.0;
            gbcUserField.fill = GridBagConstraints.HORIZONTAL;
            gbcUserField.anchor = GridBagConstraints.WEST;
            add(userField, gbcUserField);

            passwordLabel = new JLabel("Password:");
            GridBagConstraints gbcPasswordLabel = new GridBagConstraints();
            gbcPasswordLabel.gridx = 0;
            gbcPasswordLabel.gridy = 2;
            gbcPasswordLabel.anchor = GridBagConstraints.WEST;
            add(passwordLabel, gbcPasswordLabel);

            passwordField = new JTextField(password);
            GridBagConstraints gbcPasswordField = new GridBagConstraints();
            gbcPasswordField.gridx = 1;
            gbcPasswordField.gridy = 2;
            gbcPasswordField.weightx = 1.0;
            gbcPasswordField.fill = GridBagConstraints.HORIZONTAL;
            gbcPasswordField.anchor = GridBagConstraints.WEST;
            add(passwordField, gbcPasswordField);
        }

        public String getDomain() {
            return domainField.getText();
        }

        public String getUser() {
            return userField.getText();
        }

        public String getPassword() {
            return passwordField.getText();
        }
    }

}
