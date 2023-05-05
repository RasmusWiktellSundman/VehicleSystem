package se.rmsit.VehicleSystem.gui;

import se.rmsit.VehicleSystem.entities.Customer;
import se.rmsit.VehicleSystem.entities.User;

import javax.swing.*;
import java.io.IOException;
import java.util.Objects;

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
					showError("E-posten är upptagen");
					return;
				}

				// Validerar att obligatorisk information är given, avbryter annars
				if(!validateRequired()) {
					return;
				}

				// Validerar att lösenord och upprepat lösenord är samma
				if(!(new String(passwordField.getPassword()).equals(new String(repeatPasswordField.getPassword())))) {
					showError("Lösenord och upprepat lösenord behöver vara samma");
					return;
				}

				try {
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
				} catch (IllegalArgumentException e) {
					showError(e.getMessage());
				}
			} catch (IOException e) {
				showError("Skapande av kund misslyckades");
			}
		});
	}

	private boolean validateRequired() {
		if(Objects.equals(firstnameField.getText(), "")) {
			showError("Förnamn är obligatoriskt");
			return false;
		}
		if(Objects.equals(emailField.getText(), "")) {
			showError("E-post är obligatoriskt");
			return false;
		}
		if(Objects.equals(addressField.getText(), "")) {
			showError("Adress är obligatoriskt");
			return false;
		}
		if(Objects.equals(postTownField.getText(), "")) {
			showError("Postort är obligatoriskt");
			return false;
		}
		if(Objects.equals(postcodeField.getText(), "")) {
			showError("Postnummer är obligatoriskt");
			return false;
		}
		if(new String(passwordField.getPassword()).equals("")) {
			showError("Lösenord är obligatoriskt");
			return false;
		}
		return true;
	}

	private void showError(String s) {
		error.setText(s);
		error.setVisible(true);
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
