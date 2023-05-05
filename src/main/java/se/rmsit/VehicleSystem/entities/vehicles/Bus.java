package se.rmsit.VehicleSystem.entities.vehicles;

import se.rmsit.VehicleSystem.entities.Customer;

import java.util.Calendar;

public class Bus extends PublicAuthorityVehicle {
	public Bus(Customer owner, String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, Calendar boughtDate, Calendar warrantyPeriodEnd, double purchasePrice) {
		super(owner, registrationNumber, maximumPassengers, wheels, constructionDate, boughtDate, warrantyPeriodEnd, purchasePrice);
	}

	public Bus(Customer owner, String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, Calendar boughtDate, double purchasePrice) {
		super(owner, registrationNumber, maximumPassengers, wheels, constructionDate, boughtDate, (Calendar) boughtDate.clone(), purchasePrice);
		getWarrantyPeriodEnd().add(Calendar.YEAR, 15);
		System.out.println(this);
	}

	public Bus(String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, double purchasePrice) {
		super(registrationNumber, maximumPassengers, wheels, constructionDate, purchasePrice);
	}

	/**
	 * Standard konstruktor, används för att skapa objekt från persistent lagring.
	 */
	public Bus() {}
}
