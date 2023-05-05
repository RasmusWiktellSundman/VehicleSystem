package se.rmsit.VehicleSystem.entities.vehicles;

import se.rmsit.VehicleSystem.entities.Customer;
import se.rmsit.VehicleSystem.entities.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Car extends PrivateVehicle {
	public Car(Customer owner, String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, Calendar boughtDate, Calendar warrantyPeriodEnd, double purchasePrice) {
		super(owner, registrationNumber, maximumPassengers, wheels, constructionDate, boughtDate, warrantyPeriodEnd, purchasePrice);
	}

	/**
	 * Skapar bil med 10 års garantitid, med start från köpdatum
	 * @param owner Ägare
	 * @param registrationNumber Bilens registreringsnummer
	 * @param maximumPassengers Maximala antalet passagerare
	 * @param wheels Antal hjul
	 * @param constructionDate När bilen byggdes
	 * @param boughtDate När bilen köptes
	 * @param purchasePrice Hur mycket den köptes för
	 */
	public Car(Customer owner, String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, Calendar boughtDate, double purchasePrice) {
		super(owner, registrationNumber, maximumPassengers, wheels, constructionDate, boughtDate, (Calendar) boughtDate.clone(), purchasePrice);
		getWarrantyPeriodEnd().add(Calendar.YEAR, 10);
	}

	public Car(String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, double purchasePrice) {
		super(registrationNumber, maximumPassengers, wheels, constructionDate, purchasePrice);
	}
}
