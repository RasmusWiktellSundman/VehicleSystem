package se.rmsit.VehicleSystem.gui;

import se.rmsit.VehicleSystem.authentication.Authentication;
import se.rmsit.VehicleSystem.entities.Admin;
import se.rmsit.VehicleSystem.entities.Customer;
import se.rmsit.VehicleSystem.entities.User;
import se.rmsit.VehicleSystem.entities.vehicles.Vehicle;
import se.rmsit.VehicleSystem.exceptions.NoLoggedInUser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class VehiclesPanel extends PanelContainer {
	private final Authentication authentication;
	private JTable table;
	private JPanel panel;
	private JLabel error;
	private JButton filterBtn;
	private JTextField constructionYearField;
	private JLabel filterError;
	private JTextField registrationNumberField;
	private JTextField newOwnerField;
	private JButton changeOwnerBtn;
	private JLabel success;

	public VehiclesPanel(Authentication authentication) {
		this.authentication = authentication;
		filterBtn.addActionListener(event -> {
			// Renderar om informationen
			if(!constructionYearField.getText().isEmpty()) {
				try {
					Integer.valueOf(constructionYearField.getText());
				} catch (NumberFormatException e) {
					filterError.setText("Ogiltigt årtal");
					filterError.setVisible(true);
					return;
				}
			}
			render();
		});

		changeOwnerBtn.addActionListener(e -> changeOwner());
	}

	private void changeOwner() {
		// Återställ status meddelanden
		error.setVisible(false);
		success.setVisible(false);

		try {
			Vehicle vehicle = Vehicle.getByRegistrationNumber(registrationNumberField.getText());
			if(vehicle == null) {
				showError("Finns inget fordon med angivet registreringsnummer");
				return;
			}

			// Kontrollerar att användaren har behörighet att ändra ägare (äger fordonet eller är admin)
			if(!vehicle.getOwnerId().equals(authentication.getUser().getId()) && !authentication.isAdmin()) {
				showError("Åtkomst nekad!");
				return;
			}

			// Hämtar ny ägare
			Customer customer = Customer.getByEmail(newOwnerField.getText());
			if(customer == null) {
				showError("Det finns ingen ägare med angiven e-post");
				return;
			}

			try {
				vehicle.setOwner(customer);
				vehicle.save();
			} catch (IllegalArgumentException ex) {
				showError(ex.getMessage());
				return;
			}

			// Lyckades ändra ägare
			success.setText("Ägare ändrad!");
			success.setVisible(true);
			render();
		} catch (IOException e) {
			showError("Misslyckades läsa från fil");
		} catch (NoLoggedInUser noLoggedInUser) {
			showError("Du måste vara inloggad!");
		}

	}

	@Override
	public void render() {
		// Visa inga fordon ifall användaren inte är inloggad
		if(!authentication.isLoggedIn()) return;

		User user = null;
		try {
			user = authentication.getUser();
		} catch (NoLoggedInUser ignored) {}

		if(user instanceof Customer) {
			// Renderar tabell med fordon för en specifik kund
			renderCustomerVehicles((Customer) user);
		} else if(user instanceof Admin) {
			renderAllVehicles();
		}
	}


	private void renderCustomerVehicles(Customer customer) {
		// Sätter rubriker för tabellen
		String[] columnNames = {"Fordonstyp", "Registreringsnummer", "Passagerare", "Hjul", "Tillverkningsdatum", "Köpdatum", "Inköpspris", "Giltighet upphör"};
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);

		// Lägger till data
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			for (Vehicle vehicle : Vehicle.getByOwner(customer)) {
				// Visa endast fordon som tillverkats under givet filtreringsår. Visa alla fordon ifall filtrering är tom
				String filterYear = constructionYearField.getText();
				if(!filterYear.isEmpty() &&
						vehicle.getConstructionDate().get(Calendar.YEAR) != Integer.parseInt(filterYear))
					continue;
				model.addRow(new String[]{
						vehicle.getClass().getSimpleName(),
						vehicle.getRegistrationNumber(),
						String.valueOf(vehicle.getMaximumPassengers()),
						String.valueOf(vehicle.getWheels()),
						dateFormat.format(vehicle.getConstructionDate().getTime()),
						dateFormat.format(vehicle.getBoughtDate().getTime()),
						String.valueOf(vehicle.getPurchasePrice()),
						dateFormat.format(vehicle.getWarrantyPeriodEnd().getTime())
				});
			}
		} catch (IOException e) {
			showError("Misslyckades läsa in fordon");
			return;
		}


		table.setModel(model);
	}

	private void renderAllVehicles() {
		// Sätter rubriker för tabellen
		String[] columnNames = {"Ägare", "Fordonstyp", "Registreringsnummer", "Passagerare", "Hjul", "Tillverkningsdatum", "Köpdatum", "Inköpspris", "Giltighet upphör"};
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);

		// Lägger till data
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			for (Vehicle vehicle : Vehicle.getAll()) {
				// Visa endast fordon som tillverkats under givet filtreringsår. Visa alla fordon ifall filtrering är tom
				String filterYear = constructionYearField.getText();
				if(!filterYear.isEmpty() &&
						vehicle.getConstructionDate().get(Calendar.YEAR) != Integer.parseInt(filterYear))
					continue;
				String ownerName = vehicle.getOwner().getFirstName() + " " + (vehicle.getOwner().getLastName() != null ? vehicle.getOwner().getLastName() : "");
						model.addRow(new String[]{
						ownerName,
						vehicle.getClass().getSimpleName(),
						vehicle.getRegistrationNumber(),
						String.valueOf(vehicle.getMaximumPassengers()),
						String.valueOf(vehicle.getWheels()),
						dateFormat.format(vehicle.getConstructionDate().getTime()),
						dateFormat.format(vehicle.getBoughtDate().getTime()),
						String.valueOf(vehicle.getPurchasePrice()),
						dateFormat.format(vehicle.getWarrantyPeriodEnd().getTime())
				});
			}
		} catch (IOException e) {
			showError("Misslyckades läsa in fordon");
			return;
		}

		table.setModel(model);
	}

	private void showError(String message) {
		error.setText(message);
		error.setVisible(true);
	}

	public JPanel getPanel() {
		return panel;
	}
}
