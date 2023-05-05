package se.rmsit.VehicleSystem.gui;

import se.rmsit.VehicleSystem.authentication.Authentication;
import se.rmsit.VehicleSystem.exceptions.InvalidLoginCredentials;

import javax.swing.*;
import java.io.IOException;

public class LoginPanel extends PanelContainer {
	private JTextField emailField;
	private JButton loggaInButton;
	private JPasswordField passwordField;
	private JLabel error;
	private JPanel panel;

	public LoginPanel(MainFrame mainFrame, Authentication authentication) {
		loggaInButton.addActionListener(e -> {
			// Försöker logga in användaren
			try {
				authentication.login(emailField.getText(), new String(passwordField.getPassword()));
			} catch (InvalidLoginCredentials ex) {
				error.setText("E-post eller lösenord är felaktig");
				error.setVisible(true);
				return;
			} catch (IOException ex) {
				error.setText("Misslyckades läsa från fil");
				error.setVisible(true);
				return;
			}
			clearFields();
			error.setVisible(false);
			mainFrame.reloadGUI();
			mainFrame.setContentPanel(Panels.VEHICLES);
		});
	}

	private void clearFields() {
		emailField.setText("");
		passwordField.setText("");
	}

	@Override
	public void render() {
		clearFields();
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}
}
