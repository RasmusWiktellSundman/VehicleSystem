package se.rmsit.VehicleSystem;

import se.rmsit.VehicleSystem.entities.User;
import se.rmsit.VehicleSystem.entities.vehicles.Vehicle;
import se.rmsit.VehicleSystem.repositories.UserRepository;

import java.util.Calendar;

public class TestVehicle extends Vehicle {
	public TestVehicle() {}

	public TestVehicle(User owner, String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, Calendar boughtDate, double purchasePrice) {
		super(owner, registrationNumber, maximumPassengers, wheels, constructionDate, boughtDate, purchasePrice);
	}

	public TestVehicle(String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, double purchasePrice) {
		super(registrationNumber, maximumPassengers, wheels, constructionDate, purchasePrice);
	}
}
