package se.rmsit.VehicleSystem.entities.vehicles;

import se.rmsit.VehicleSystem.entities.Customer;

import java.util.Calendar;

public class Car extends PrivateVehicle {
	public Car(Customer owner, String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, Calendar boughtDate, Calendar warrantyPeriodEnd, double purchasePrice, String address) {
		super(owner, registrationNumber, maximumPassengers, wheels, constructionDate, boughtDate, warrantyPeriodEnd, purchasePrice, address);
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
	 * @param address Bilens adress
	 */
	public Car(Customer owner, String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, Calendar boughtDate, double purchasePrice, String address) {
		super(owner, registrationNumber, maximumPassengers, wheels, constructionDate, boughtDate, (Calendar) boughtDate.clone(), purchasePrice, address);
		getWarrantyPeriodEnd().add(Calendar.YEAR, 10);
	}

	public Car(String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, double purchasePrice, String address) {
		super(registrationNumber, maximumPassengers, wheels, constructionDate, purchasePrice, address);
	}

	/**
	 * Standard konstruktor, används för att skapa objekt från persistent lagring.
	 */
	public Car() {}
}
