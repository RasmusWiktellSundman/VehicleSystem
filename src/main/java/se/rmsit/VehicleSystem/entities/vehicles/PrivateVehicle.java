package se.rmsit.VehicleSystem.entities.vehicles;

import se.rmsit.VehicleSystem.entities.Customer;

import java.util.Calendar;

public abstract class PrivateVehicle extends Vehicle {
	private String address;

	public PrivateVehicle(Customer owner, String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, Calendar boughtDate, Calendar warrantyPeriodEnd, double purchasePrice, String address) {
		super(owner, registrationNumber, maximumPassengers, wheels, constructionDate, boughtDate, warrantyPeriodEnd, purchasePrice);
		setAddress(address);
	}

	public PrivateVehicle(String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, double purchasePrice, String address) {
		super(registrationNumber, maximumPassengers, wheels, constructionDate, purchasePrice);
		setAddress(address);
	}

	/**
	 * Standard konstruktor, används för att skapa objekt från persistent lagring.
	 */
	public PrivateVehicle() {}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		if(address == null) {
			throw new IllegalArgumentException("Address cant be null");
		}
		this.address = address;
	}
}
