package se.rmsit.VehicleSystem.gui;

import se.rmsit.VehicleSystem.authentication.Authentication;
import se.rmsit.VehicleSystem.entities.Customer;
import se.rmsit.VehicleSystem.entities.User;
import se.rmsit.VehicleSystem.entities.vehicles.Vehicle;
import se.rmsit.VehicleSystem.exceptions.NoLoggedInUser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ReportsPanel extends PanelContainer {
	private Authentication authentication;
	private JPanel panel;
	private JPanel adminButtonPanel;
	private JTextField yearReportCustomerEmailField;
	private JTextField yearReportAdminYearField;
	private JTextField boughtHistoryCustomerEmailField;
	private JButton yearReportAdminBtn;
	private JButton boughtHistoryAdminBtn;
	private JPanel customerButtonPanel;
	private JButton customerBoughtHistoryBtn;
	private JButton yearReportCustomerBtn;
	private JTextField yearReportCustomerYearField;
	private JLabel error;
	private JTable table;

	public ReportsPanel(Authentication authentication) {
		this.authentication = authentication;
		// Knapp som admin trycker på för att visa köphistorik för en kund
		boughtHistoryAdminBtn.addActionListener(e -> {
			// Gömmer felmeddelanden, så gamla felmeddelanden inte är kvar
			error.setVisible(false);
			// Nollställer tabellen.
			((DefaultTableModel) table.getModel()).setRowCount(0);

			// Hämtar angiven kund
			try {
				Customer customer = getCustomer(boughtHistoryCustomerEmailField);
				if(customer != null) {
					showBoughtHistory(customer);
				}
			} catch (IOException ex) {
				showError("Misslyckades läsa/spara fil");
			}
		});
		// Knapp som kund trycker på för att visa köphistorik
		customerBoughtHistoryBtn.addActionListener(e -> {
			// Gömmer felmeddelanden, så gamla felmeddelanden inte är kvar
			error.setVisible(false);
			// Nollställer tabellen.
			((DefaultTableModel) table.getModel()).setRowCount(0);

			try {
				Customer customer = getLoggedInCustomer();
				if(customer != null) {
					showBoughtHistory(customer);
				}
			} catch (IOException exception) {
				showError("Misslyckades läsa/spara fil");
			}
		});
		// Knapp som admin trycker på för att visa årsrapport om en kund
		yearReportAdminBtn.addActionListener(e -> {
			// Hämtar angiven kund
			try {
				Customer customer = getCustomer(yearReportCustomerEmailField);
				if(customer != null) {
					showYearReport(customer, Integer.parseInt(yearReportAdminYearField.getText()));
				}
			} catch (IOException ex) {
				showError("Misslyckades läsa/spara fil");
			} catch (NumberFormatException ex) {
				showError("Ogiltigt årtal");
			}
		});
		// Knapp som kund trycker på för att visa årsrapport
		yearReportCustomerBtn.addActionListener(e -> {
			try {
				// Hämtar angiven kund
				Customer customer = getLoggedInCustomer();
				if(customer != null) {
					showYearReport(customer, Integer.parseInt(yearReportCustomerYearField.getText()));
				}
			} catch (IOException ex) {
				showError("Misslyckades läsa/spara fil");
			} catch (NumberFormatException ex) {
				showError("Ogiltigt årtal");
			}
		});
	}

	private Customer getLoggedInCustomer() {
		try {
			User user = authentication.getUser();
			if(!(user instanceof Customer)) {
				showError("Du måste vara en kund!");
				return null;
			}
			return (Customer) user;
		} catch (NoLoggedInUser ex) {
			showError("Du måste vara inloggad!");
			return null;
		}
	}

	private Customer getCustomer(JTextField customerField) throws IOException {
		Customer customer = Customer.getByEmail(customerField.getText());
		// Kontrollerar att kunden finns
		if(customer == null) {
			showError("Kunden finns inte");
			return null;
		}

		return customer;
	}

	/**
	 * Visar fordon kunden äger och när det köptes i tabell
	 * @param customer Kunden att visa köphistorik för
	 */
	private void showBoughtHistory(Customer customer) throws IOException {
		// Sätter rubriker för tabellen
		String[] columnNames = {"Fordonstyp", "Registreringsnummer", "Köpdatum"};
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);

		// Lägger till data i tabellen
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		for (Vehicle vehicle : Vehicle.getByOwner(customer)) {
			model.addRow(new String[]{
					vehicle.getClass().getSimpleName(),
					vehicle.getRegistrationNumber(),
					dateFormat.format(vehicle.getBoughtDate().getTime())
			});
		}
		table.setModel(model);
	}

	/**
	 * Visar årsrapport i tabell
	 * @param customer Kunden att generera rapport för
	 */
	private void showYearReport(Customer customer, int year) throws IOException {
		// Sätter rubriker för tabellen
		String[] columnNames = {"Fordonstyp", "Registreringsnummer", "Köpdatum", "Tid kvar på garanti"};
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);

		// Skapar variabel för angivet års slut
		Calendar year_end = Calendar.getInstance();
		year_end.set(Calendar.YEAR, year);
		year_end.set(Calendar.MONTH, 12);
		year_end.set(Calendar.DAY_OF_MONTH, 31);

		// Lägger till data i tabellen, endast fordon som kunden ägde innan årets slut listas
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Vehicle.getByOwner(customer).stream()
			.filter(vehicle -> vehicle.getBoughtDate().before(year_end))
			.forEach(vehicle -> {
				// Kalkylerar tid kvar av garanti
				String warrantyTimeLeft;
				if(year_end.after(vehicle.getWarrantyPeriodEnd())) {
					warrantyTimeLeft = "Utgått";
				} else {
					long timeLeftInMilliseconds = vehicle.getWarrantyPeriodEnd().getTimeInMillis() - year_end.getTimeInMillis();
					long daysLeft = TimeUnit.MILLISECONDS.toDays(timeLeftInMilliseconds);
					warrantyTimeLeft = (int) (daysLeft / 365) + " år och " + (daysLeft % 365) + " dagar";
				}
				model.addRow(new String[]{
						vehicle.getClass().getSimpleName(),
						vehicle.getRegistrationNumber(),
						dateFormat.format(vehicle.getBoughtDate().getTime()),
						warrantyTimeLeft
				});
			});
		table.setModel(model);
	}

	@Override
	public void render() {
		// Gömmer felmeddelanden, så gamla felmeddelanden inte är kvar
		error.setVisible(false);
		// Nollställer tabellen.
		((DefaultTableModel) table.getModel()).setRowCount(0);

		// döljer paneler, för att sedan visa rätt beroende på användartyp
		adminButtonPanel.setVisible(false);
		customerButtonPanel.setVisible(false);

		// Visar adminpanelen för admin användare
		if(authentication.isAdmin()) {
			adminButtonPanel.setVisible(true);
		}

		// Visar kundpanelen för kunder
		try {
			if(authentication.getUser() instanceof Customer) {
				customerButtonPanel.setVisible(true);
			}
		} catch (NoLoggedInUser e) {
			showError("Du måste vara inloggad!");
		}
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
