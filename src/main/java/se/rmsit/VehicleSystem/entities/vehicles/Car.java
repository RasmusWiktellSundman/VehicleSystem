package se.rmsit.VehicleSystem.entities.vehicles;

import se.rmsit.VehicleSystem.entities.Customer;
import se.rmsit.VehicleSystem.entities.User;

import java.util.Calendar;

public class Car extends PrivateVehicle {
	public Car(Customer owner, String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, Calendar boughtDate, double purchasePrice) {
		super(owner, registrationNumber, maximumPassengers, wheels, constructionDate, boughtDate, purchasePrice);
	}

	public Car(String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, double purchasePrice) {
		super(registrationNumber, maximumPassengers, wheels, constructionDate, purchasePrice);
	}
}
