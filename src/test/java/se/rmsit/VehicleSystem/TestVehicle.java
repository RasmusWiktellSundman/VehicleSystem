package se.rmsit.VehicleSystem;

import se.rmsit.VehicleSystem.entities.Customer;
import se.rmsit.VehicleSystem.entities.vehicles.Vehicle;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TestVehicle extends Vehicle {
	public TestVehicle() {}

	public TestVehicle(Customer owner, String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, Calendar boughtDate, Calendar warrantyPeriodEnd, double purchasePrice) {
		super(owner, registrationNumber, maximumPassengers, wheels, constructionDate, boughtDate, warrantyPeriodEnd, purchasePrice);
	}

	public TestVehicle(Customer owner, String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, Calendar boughtDate, double purchasePrice) {
		super(owner, registrationNumber, maximumPassengers, wheels, constructionDate, boughtDate, (Calendar) boughtDate.clone(), purchasePrice);
		getWarrantyPeriodEnd().add(Calendar.YEAR, 10);
	}

	public TestVehicle(String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, double purchasePrice) {
		super(registrationNumber, maximumPassengers, wheels, constructionDate, purchasePrice);
	}
}
