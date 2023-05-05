package se.rmsit.VehicleSystem.views;

import se.rmsit.VehicleSystem.entities.Customer;

import javax.swing.*;

public class RegisterUserPanel extends JPanel {
	private JPanel registerUser;
	private JTextField firstnameField;
	private JTextField lastnameField;
	private JTextField emailField;
	private JTextField addressField;
	private JTextField postCityField;
	private JTextField postalCodeField;
	private JPasswordField passwordField;
	private JPasswordField repeatPasswordField;
	private JButton registerCustomerButton;

	public RegisterUserPanel() {
		registerCustomerButton.addActionListener(event -> {
			Customer customer = new Customer()
		});
	}

	public JPanel getRegisterUser() {
		return registerUser;
	}
}
