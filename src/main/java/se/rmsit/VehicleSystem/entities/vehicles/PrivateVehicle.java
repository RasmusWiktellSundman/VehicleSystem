package se.rmsit.VehicleSystem.entities.vehicles;

import se.rmsit.VehicleSystem.entities.Customer;

import java.util.Calendar;
import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		PrivateVehicle that = (PrivateVehicle) o;

		return Objects.equals(address, that.address);
	}

	@Override
	public String serialize() {
		String serialized = super.serialize() + "\n";
		serialized += "address: " + getAddress() + "\n";
		return serialized;
	}

	/**
	 * Laddar in ett nyckel-värde par till objektet
	 * @param key Nyckeln för värdet
	 * @param value Värdet
	 */
	@Override
	public void loadData(String key, String value) {
		super.loadData(key, value);
		if ("address".equals(key)) {
			setAddress(value);
		}
	}
}
