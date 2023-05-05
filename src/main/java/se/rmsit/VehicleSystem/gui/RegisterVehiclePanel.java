package se.rmsit.VehicleSystem.gui;

import se.rmsit.VehicleSystem.authentication.Authentication;
import se.rmsit.VehicleSystem.entities.Admin;
import se.rmsit.VehicleSystem.entities.Customer;
import se.rmsit.VehicleSystem.entities.User;
import se.rmsit.VehicleSystem.entities.vehicles.Bus;
import se.rmsit.VehicleSystem.entities.vehicles.Car;
import se.rmsit.VehicleSystem.entities.vehicles.Motorcycle;
import se.rmsit.VehicleSystem.entities.vehicles.Vehicle;
import se.rmsit.VehicleSystem.exceptions.NoLoggedInUser;

import javax.swing.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class RegisterVehiclePanel extends PanelContainer {
	private final Authentication authentication;
	private JComboBox vehicleType;
	private JPanel panel;
	private JLabel error;
	private JTextField registrationNumberField;
	private JSpinner maximumPassengersSpinner;
	private JSpinner purchasePriceSpinner;
	private JButton registerVehicleBtn;
	private JTextField constructionDateField;
	private JTextField boughtDateField;
	private JSpinner wheelSpinner;
	private JLabel success;
	private JSpinner trunkVolumeSpinner;
	private JLabel trunkVolumeLabel;
	private JTextField ownerField;
	private JLabel ownerLabel;

	public RegisterVehiclePanel(Authentication authentication) {
		this.authentication = authentication;

		registerVehicleBtn.addActionListener(e -> {
			// Återställer felmeddelanden och framgångsmeddelande
			error.setVisible(false);
			success.setVisible(false);

			// Konverterar datum till Calendar objekt
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Calendar boughtDate = Calendar.getInstance();
			Calendar constructionDate = Calendar.getInstance();
			try {
				boughtDate.setTime(dateFormat.parse(boughtDateField.getText()));
				constructionDate.setTime(dateFormat.parse(constructionDateField.getText()));
			} catch (ParseException ex) {
				showError("Ogiltigt datum");
				return;
			}

			try {
				if(Vehicle.getByRegistrationNumber(registrationNumberField.getText()) != null) {
					showError("Registreringsnumret är upptagen");
					return;
				}

				// Hämtar kund
				Customer owner;
				if(authentication.getUser() instanceof Customer) {
					owner = (Customer) authentication.getUser();
				} else {
					// Användaren är en administratör, använder ägarefält för att bestämma bilens ägare
					owner = Customer.getByEmail(ownerField.getText());
					if(owner == null) {
						showError("Finns ingen kund med given email.");
						return;
					}
				}

				try {
					if(vehicleType.getSelectedItem().equals("Bil")) {
						// Skapar ny bil
						Car car = new Car(
								owner,
								registrationNumberField.getText(),
								(int) maximumPassengersSpinner.getValue(),
								(int) wheelSpinner.getValue(),
								constructionDate,
								boughtDate,
								(int) purchasePriceSpinner.getValue(),
								owner.getAddress(),
								(int) trunkVolumeSpinner.getValue()
						);
						car.save();
					} else if(vehicleType.getSelectedItem().equals("Motorcykel")) {
						// Skapar ny motorcykel
						Motorcycle motorcycle = new Motorcycle(
								owner,
								registrationNumberField.getText(),
								(int) maximumPassengersSpinner.getValue(),
								(int) wheelSpinner.getValue(),
								constructionDate,
								boughtDate,
								(int) purchasePriceSpinner.getValue(),
								owner.getAddress()
						);
						motorcycle.save();
					} else if(vehicleType.getSelectedItem().equals("Buss")) {
						// Skapar ny buss, om ägaren är en kommun
						if(!owner.isPublicAuthority()) {
							String errorMessage = "Endast kommuner kan registrera bussar";
							if(authentication.getUser() instanceof Admin) {
								errorMessage = "Ägaren måste vara en kommun för att kunna äga bussen";
							}
							showError(errorMessage);
							return;
						}
						Bus bus = new Bus(
								owner,
								registrationNumberField.getText(),
								(int) maximumPassengersSpinner.getValue(),
								(int) wheelSpinner.getValue(),
								constructionDate,
								boughtDate,
								(int) purchasePriceSpinner.getValue()
						);
						bus.save();
					}
				} catch (IllegalArgumentException ex) {
					error.setText(ex.getMessage());
					error.setVisible(true);
					return;
				}

				// Lyckades skapa fordon
				success.setText("Fordon registrerat!");
				success.setVisible(true);
				clearFields();
			} catch (NoLoggedInUser ex) {
				showError("Fordon registrerat!");
			} catch (IOException ex) {
				showError("Misslyckades spara fordon");
			}
		});

		// Visa olika fält beroende på fordonstyp
		vehicleType.addActionListener(e -> {
			updateVisibleFields();
		});
	}

	/**
	 * Visar error meddelande med hjälp av error JLabel
	 * @param message Error-meddelandet som ska visas
	 */
	private void showError(String message) {
		error.setText(message);
		error.setVisible(true);
	}

	/**
	 * Uppdaterar dynamiskt vilja fält och tillhörande etiketter som ska synas
	 */
	private void updateVisibleFields() {
		trunkVolumeLabel.setVisible(false);
		trunkVolumeSpinner.setVisible(false);
		if(Objects.equals(vehicleType.getSelectedItem(), "Bil")) {
			trunkVolumeLabel.setVisible(true);
			trunkVolumeSpinner.setVisible(true);
		}

		// Visa endast ägarefältet för administratörer
		ownerLabel.setVisible(false);
		ownerField.setVisible(false);
		try {
			if(authentication.getUser() instanceof Admin) {
				ownerLabel.setVisible(true);
				ownerField.setVisible(true);
			}
		// Visar inte ägarefältet ifall ingen är inloggad
		} catch (NoLoggedInUser ignored) {}
	}

	private void clearFields() {
		registrationNumberField.setText("");
		maximumPassengersSpinner.setValue(0);
		purchasePriceSpinner.setValue(0);
		constructionDateField.setText("");
		boughtDateField.setText("");
		wheelSpinner.setValue(0);
		setDynamicFields();
	}

	@Override
	public void render() {
		// Återställer felmeddelanden
		error.setVisible(false);

		// Sätter dynamiska standardvärden
		setDynamicFields();

		try {
			User user = authentication.getUser();
			vehicleType.removeAllItems();
			vehicleType.addItem("Bil");
			vehicleType.addItem("Motorcykel");
			if(user instanceof Admin ||
					user instanceof Customer && ((Customer) user).isPublicAuthority()) {
				vehicleType.addItem("Buss");
			}
		} catch (NoLoggedInUser e) {
			error.setText("Du måste vara inloggad!");
		}
	}

	private void setDynamicFields() {
		String today = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
		boughtDateField.setText(today);
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}
}
