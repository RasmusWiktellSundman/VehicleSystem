package se.rmsit.VehicleSystem.gui;

import se.rmsit.VehicleSystem.authentication.Authentication;
import se.rmsit.VehicleSystem.entities.Customer;
import se.rmsit.VehicleSystem.entities.User;
import se.rmsit.VehicleSystem.entities.vehicles.Car;
import se.rmsit.VehicleSystem.entities.vehicles.Motorcycle;
import se.rmsit.VehicleSystem.entities.vehicles.Vehicle;
import se.rmsit.VehicleSystem.exceptions.NoLoggedInUser;

import javax.swing.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
				error.setText("Ogiltigt datum");
				error.setVisible(true);
				return;
			}

			try {
				if(Vehicle.getByRegistrationNumber(registrationNumberField.getText()) != null) {
					error.setText("Registreringsnumret är upptagen");
					error.setVisible(true);
					return;
				}

				// Hämtar kund
				Customer customer = null;
				if(authentication.getUser() instanceof Customer) {
					customer = (Customer) authentication.getUser();
				}

				// Skapar ny bil
				try {
					if(vehicleType.getSelectedItem().equals("Bil")) {
						Car car = new Car(
								customer,
								registrationNumberField.getText(),
								(int) maximumPassengersSpinner.getValue(),
								(int) wheelSpinner.getValue(),
								constructionDate,
								boughtDate,
								(int) purchasePriceSpinner.getValue(),
								customer.getAddress()
						);
						car.save();
					} else if(vehicleType.getSelectedItem().equals("Motorcykel")) {
						Motorcycle motorcycle = new Motorcycle(
								customer,
								registrationNumberField.getText(),
								(int) maximumPassengersSpinner.getValue(),
								(int) wheelSpinner.getValue(),
								constructionDate,
								boughtDate,
								(int) purchasePriceSpinner.getValue(),
								customer.getAddress()
						);
						motorcycle.save();
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
				error.setText("Du måste vara inloggad");
				error.setVisible(true);
			} catch (IOException ex) {
				error.setText("Misslyckades spara fordon");
				error.setVisible(true);
			}
		});
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
			if(user instanceof Customer) {
				vehicleType.addItem("Bil");
				vehicleType.addItem("Motorcykel");
				if(((Customer) user).isPublicAuthority()) {
					vehicleType.addItem("Buss");
				}
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
