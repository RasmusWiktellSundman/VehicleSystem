package se.rmsit.VehicleSystem.gui;

import se.rmsit.VehicleSystem.authentication.Authentication;
import se.rmsit.VehicleSystem.entities.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;

public class CustomersPanel extends PanelContainer {
	private JPanel panel;
	private JTable table;
	private JTextField customerIdField;
	private JButton filterBtn;
	private JLabel error;
	private JTextField customerEmailField;
	private Authentication authentication;

	public CustomersPanel(Authentication authentication) {
		this.authentication = authentication;
		filterBtn.addActionListener(event -> {
			// Renderar om tabellen
			updateTable();
		});
	}

	@Override
	public void render() {
		error.setVisible(false);

		// Endast administratörer får se kundlista
		if(!authentication.isAdmin()) {
			showError("Åtkomst nekad, du måste vara admin");
			clearTable();
			return;
		}
		updateTable();
	}

	private void updateTable() {
		String[] columnNames = {"Kund nummer", "Förnamn", "Efternamn", "E-post", "Address", "Postkod", "Postort", "Telefonnummer", "Offentlig verksamhet"};
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);

		// Lägger till data
		try {
			String customerIdFilter = customerIdField.getText();
			String customerEmailFilter = customerEmailField.getText();
			Customer.getAll().stream()
					.filter(customer -> {
						// Endast visa kunder vars id stämmer eller om filtret inte är satt
						if(!customerIdFilter.isEmpty() && !customer.getId().equals(customerIdFilter)) {
							return false;
						}
						// Endast visa kunder vars e-post stämmer eller om filtret inte är satt
						return customerEmailFilter.isEmpty() || customer.getEmail().equals(customerEmailFilter);
					})
					.forEach(customer -> {
						model.addRow(new String[]{
								customer.getId(),
								customer.getFirstName(),
								customer.getLastName(),
								customer.getEmail(),
								customer.getAddress(),
								customer.getPostcode(),
								customer.getPostTown(),
								customer.getPhoneNumber(),
								customer.isPublicAuthority() ? "Ja" : "Nej"
						});
					});
			table.setModel(model);
		} catch (IOException e) {
			showError("Misslyckades läsa in kunder");
		}
	}

	private void clearTable() {
		table.setModel(new DefaultTableModel());
	}

	private void showError(String message) {
		error.setText(message);
		error.setVisible(true);
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}


}
