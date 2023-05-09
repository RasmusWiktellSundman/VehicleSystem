package se.rmsit.VehicleSystem.gui;

import se.rmsit.VehicleSystem.authentication.Authentication;
import se.rmsit.VehicleSystem.entities.Customer;
import se.rmsit.VehicleSystem.entities.RepairLog;
import se.rmsit.VehicleSystem.entities.vehicles.Vehicle;
import se.rmsit.VehicleSystem.exceptions.CustomerDoesntExistException;
import se.rmsit.VehicleSystem.exceptions.NoLoggedInUser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RepairLogPanel extends PanelContainer {
	private Authentication authentication;
	private JTable table;
	private JTextField createLogRegistrationNumberField;
	private JTextField descriptionField;
	private JTextField customerField;
	private JTextField dateField;
	private JButton createLogButton;
	private JTextField showLogsRegistrationNumberField;
	private JButton showLogsBtn;
	private JPanel filterRepairLogsPanel;
	private JPanel createRepairLogPanel;
	private JPanel panel;
	private JLabel error;
	private JLabel createRepairError;
	private JLabel success;

	public RepairLogPanel(Authentication authentication) {
		this.authentication = authentication;
		createLogButton.addActionListener(e -> createRepairLog());
		showLogsBtn.addActionListener(e -> updateTable());
	}

	private void createRepairLog() {
		// Endast administratörer kan skapa reparationer
		if(!authentication.isAdmin()) {
			showCreateRepairError("Åtkomst nekad!");
			return;
		}

		// Validerar datumformat
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Calendar date = Calendar.getInstance();
		try {
			date.setTime(dateFormat.parse(dateField.getText()));
		} catch (ParseException ex) {
			showCreateRepairError("Ogiltigt datum");
			return;
		}

		// Kontrollerar att kund med given e-post finns
		Customer customer;
		try {
			customer = Customer.getByEmail(customerField.getText());
			if(customer == null) {
				showCreateRepairError("Finns ingen kund med given e-post");
				return;
			}
		} catch (IOException ex) {
			showCreateRepairError("Misslyckades att hämta kund");
			return;
		}

		// Kontrollerar att fordon med givet registreringsnummer finns
		Vehicle vehicle;
		try {
			vehicle = Vehicle.getByRegistrationNumber(createLogRegistrationNumberField.getText());
			if(vehicle == null) {
				showCreateRepairError("Finns inget fordon med givet registreringsnummer");
				return;
			}
		} catch (IOException ex) {
			showCreateRepairError("Misslyckades att hämta fordon");
			return;
		}

		try {
			RepairLog repairLog = new RepairLog(date, descriptionField.getText(), customer, vehicle);
			repairLog.save();
		} catch (IllegalArgumentException ex) {
			showCreateRepairError(ex.getMessage());
			return;
		} catch (IOException exception) {
			showCreateRepairError("Misslyckades spara reparation");
			return;
		}

		createRepairError.setVisible(false);
		success.setText("Reparation skapad!");
		success.setVisible(true);

		// Uppdaterar tabellen, för att visa nya reparationen
		updateTable();
	}

	@Override
	public void render() {
		// Endast administratörer kan registrera nya reparationer
		createRepairLogPanel.setVisible(authentication.isAdmin());

		// Sätter datum till dagens datum
		String today = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime());
		dateField.setText(today);

		// Uppdaterar tabellen med reparationer
		updateTable();
	}

	private void updateTable() {
		// Gömmer felmeddelanden, så gamla felmeddelanden inte är kvar
		error.setVisible(false);
		// Nollställer tabellen.
		((DefaultTableModel) table.getModel()).setRowCount(0);

		// Kontrollerar att användaren har rätt att visa reparationer
		// Endast administratörer kan visa reparationer för alla fordon
		String registrationNumber = showLogsRegistrationNumberField.getText();
		Vehicle vehicle = null;
		if(registrationNumber.isEmpty() && !authentication.isAdmin()) {
			showError("Endast administratörer kan visa alla reparationer.");
			return;
		} else if(!registrationNumber.isEmpty()) {
			// Kontrollerar för ett specifikt fordon
			try {
				vehicle = Vehicle.getByRegistrationNumber(registrationNumber);
				if(vehicle == null) {
					showError("Finns inget fordon med angivet registreringsnummer");
					return;
				}
				if(!vehicle.getOwner().equals(authentication.getUser()) && !authentication.isAdmin()) {
					showError("Åtkomst nekad");
					return;
				}
			} catch (IOException e) {
				showError("Misslyckades hämta fordon");
				return;
			} catch (NoLoggedInUser noLoggedInUser) {
				showError("Du måste vara inloggad!");
				return;
			}
		}

		// Användaren har behörighet, visar information i tabell
		String[] columnNames = {"Registreringsnummer", "Kund", "Beskrivning", "Datum"};
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		// Visar alla reparationer
		if(registrationNumber.isEmpty()) {
			RepairLog.getAll().forEach(repairLog -> {
				model.addRow(generateTableRow(dateFormat, repairLog));
			});
		} else {
			// Visar reparationer för ett fordon
			RepairLog.getAllByVehicle(vehicle).forEach(repairLog -> {
				model.addRow(generateTableRow(dateFormat, repairLog));
			});
		}
		table.setModel(model);
	}

	/**
	 * Genererar tabellrad som string array
	 * @param dateFormat Format på datum
	 * @param repairLog Reparationen att konvertera till tabell
	 * @return String[] som kan adderas till TableModel
	 */
	private String[] generateTableRow(SimpleDateFormat dateFormat, RepairLog repairLog) {
		try {
			return new String[]{
					repairLog.getVehicleRegistrationNumber(),
					repairLog.getCustomer().getFullName(),
					repairLog.getDescription(),
					dateFormat.format(repairLog.getDate().getTime())
			};
		} catch (CustomerDoesntExistException e) {
			return new String[]{
					repairLog.getVehicleRegistrationNumber(),
					"Raderad kund",
					repairLog.getDescription(),
					dateFormat.format(repairLog.getDate().getTime())
			};
		}
	}

	private void showError(String message) {
		error.setText(message);
		error.setVisible(true);
	}

	private void showCreateRepairError(String message) {
		createRepairError.setText(message);
		createRepairError.setVisible(true);
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}
}
