package se.rmsit.VehicleSystem.entities.vehicles;

import se.rmsit.VehicleSystem.entities.Customer;

import java.util.Calendar;

public class PublicAuthorityVehicle extends Vehicle {
	public PublicAuthorityVehicle(Customer owner, String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, Calendar boughtDate, Calendar warrantyPeriodEnd, double purchasePrice) {
		super(owner, registrationNumber, maximumPassengers, wheels, constructionDate, boughtDate, warrantyPeriodEnd, purchasePrice);
	}

	public PublicAuthorityVehicle(String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, double purchasePrice) {
		super(registrationNumber, maximumPassengers, wheels, constructionDate, purchasePrice);
	}

	/**
	 * Standard konstruktor, används för att skapa objekt från persistent lagring.
	 */
	public PublicAuthorityVehicle() {}

	@Override
	public void setOwner(Customer owner) {
		if(!owner.isPublicAuthority()) {
			throw new IllegalArgumentException("Bus owner must be a public authority!");
		}
		super.setOwner(owner);
	}
}
