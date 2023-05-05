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

public class VehiclesPanel extends PanelContainer {
	private final Authentication authentication;
	private JTable table;
	private JPanel panel;
	private JLabel error;

	public VehiclesPanel(Authentication authentication) {
		this.authentication = authentication;
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
			error.setText("Misslyckades läsa in fordon");
			error.setVisible(true);
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
			error.setText("Misslyckades läsa in fordon");
			error.setVisible(true);
			return;
		}

		table.setModel(model);
	}

	public JPanel getPanel() {
		return panel;
	}
}
