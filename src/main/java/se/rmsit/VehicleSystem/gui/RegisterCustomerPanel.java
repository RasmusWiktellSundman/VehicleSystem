package se.rmsit.VehicleSystem.gui;

import se.rmsit.VehicleSystem.entities.Customer;
import se.rmsit.VehicleSystem.entities.User;

import javax.swing.*;
import java.io.IOException;

public class RegisterCustomerPanel extends PanelContainer {
	private JPanel panel;
	private JTextField firstnameField;
	private JTextField lastnameField;
	private JTextField emailField;
	private JTextField addressField;
	private JTextField postTownField;
	private JTextField postcodeField;
	private JPasswordField passwordField;
	private JPasswordField repeatPasswordField;
	private JButton registerCustomerButton;
	private JTextField phoneNumberField;
	private JCheckBox publicAuthorityCheckBox;
	private JLabel error;
	private JLabel success;

	public RegisterCustomerPanel() {
		registerCustomerButton.addActionListener(event -> {
			try {
				success.setVisible(false);

				// Validera data
				if(User.getByEmail(emailField.getText()) != null) {
					error.setText("E-posten är upptagen");
					error.setVisible(true);
					return;
				}

				Customer customer = new Customer(
						firstnameField.getText(),
						lastnameField.getText(),
						addressField.getText(),
						postTownField.getText(),
						postcodeField.getText(),
						phoneNumberField.getText(),
						publicAuthorityCheckBox.isSelected(),
						emailField.getText(),
						new String(passwordField.getPassword())
				);

				customer.save();
				success.setText("Kund skapad!");
				success.setVisible(true);
				error.setVisible(false);
				clearFields();
			} catch (IOException e) {
				error.setText("Skapande av kund misslyckades");
				error.setVisible(true);
			}
		});
	}

	private void clearFields() {
		firstnameField.setText("");
		lastnameField.setText("");
		addressField.setText("");
		postTownField.setText("");
		postcodeField.setText("");
		phoneNumberField.setText("");
		emailField.setText("");
		passwordField.setText("");
		repeatPasswordField.setText("");
		publicAuthorityCheckBox.setSelected(false);
	}

	// Gör inget då ingen information laddas in dynamiskt
	@Override
	public void render() {}

	@Override
	public JPanel getPanel() {
		return panel;
	}
}
