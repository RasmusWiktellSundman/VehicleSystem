package se.rmsit.VehicleSystem.entities.vehicles;

import se.rmsit.VehicleSystem.entities.Customer;

import java.util.Calendar;

public class Car extends PrivateVehicle {
	// Bagageutrymme i m^3
	private int trunkVolume;

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
	public Car(Customer owner, String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, Calendar boughtDate, double purchasePrice, String address, int trunkVolume) {
		super(owner, registrationNumber, maximumPassengers, wheels, constructionDate, boughtDate, (Calendar) boughtDate.clone(), purchasePrice, address);
		getWarrantyPeriodEnd().add(Calendar.YEAR, 10);
		setTrunkVolume(trunkVolume);
	}

	public Car(String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, double purchasePrice, String address) {
		super(registrationNumber, maximumPassengers, wheels, constructionDate, purchasePrice, address);
		setTrunkVolume(trunkVolume);
	}

	/**
	 * Standard konstruktor, används för att skapa objekt från persistent lagring.
	 */
	public Car() {}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		Car car = (Car) o;

		return trunkVolume == car.trunkVolume;
	}

	@Override
	public String serialize() {
		String serialized = super.serialize() + "\n";
		serialized += "trunk_volume: " + getTrunkVolume();
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
		if ("trunk_volume".equals(key)) {
			setTrunkVolume(Integer.parseInt(value));
		}
	}

	public int getTrunkVolume() {
		return trunkVolume;
	}

	public void setTrunkVolume(int trunkVolume) {
		if(trunkVolume < 0) {
			throw new IllegalArgumentException("Negative trunk volume");
		}
		this.trunkVolume = trunkVolume;
	}
}
