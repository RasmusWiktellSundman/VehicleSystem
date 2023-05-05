package se.rmsit.VehicleSystem.entities.vehicles;

import se.rmsit.VehicleSystem.entities.Entity;
import se.rmsit.VehicleSystem.entities.Fetchable;
import se.rmsit.VehicleSystem.entities.User;
import se.rmsit.VehicleSystem.repositories.UserRepository;

import java.util.Calendar;
import java.util.Objects;

public abstract class Vehicle extends Entity implements Fetchable {
	/**
	 * Används endast för att initiera fordon från persistent lagring
	 */
	private UserRepository userRepository;
	// Används av VehicleRepository för att hämta User objekt
	private String ownerId;
	private User owner;
	private String registrationNumber;
	private int maximumPassengers;
	private int wheels;
	private Calendar constructionDate;
	private Calendar boughtDate;
	private double purchasePrice;

	public Vehicle(User owner, String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, Calendar boughtDate, double purchasePrice) {
		setOwner(owner);
		setRegistrationNumber(registrationNumber);
		setMaximumPassengers(maximumPassengers);
		setWheels(wheels);
		setConstructionDate(constructionDate);
		setBoughtDate(boughtDate);
		setPurchasePrice(purchasePrice);
	}

	public Vehicle(String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, double purchasePrice) {
		setRegistrationNumber(registrationNumber);
		setMaximumPassengers(maximumPassengers);
		setWheels(wheels);
		setConstructionDate(constructionDate);
		setPurchasePrice(purchasePrice);
	}

	/**
	 * Standard konstruktor, används för att skapa objekt från persistent lagring.
	 */
	public Vehicle() {}

	@Override
	public String serialize() {
		return "owner_id: " + getOwner().getId() + "\n" +
				"registration_number: " + getRegistrationNumber() + "\n" +
				"maximum_passengers: " + getMaximumPassengers() + "\n" +
				"wheels: " + getWheels() + "\n" +
				"construction_date: " + getConstructionDate().getTimeInMillis() + "\n" +
				"bought_date: " + getBoughtDate().getTimeInMillis() + "\n" +
				"purchase_price: " + getPurchasePrice();
	}

	@Override
	public void loadData(String key, String value) {
		switch (key) {
			case "owner_id" -> ownerId = value;
			case "registration_number" -> setRegistrationNumber(value);
			case "maximum_passengers" -> setMaximumPassengers(Integer.parseInt(value));
			case "wheels" -> setWheels(Integer.parseInt(value));
			case "construction_date" -> {
				setConstructionDate(Calendar.getInstance());
				getConstructionDate().setTimeInMillis(Long.parseLong(value));
			}
			case "bought_date" -> {
				setBoughtDate(Calendar.getInstance());
				getBoughtDate().setTimeInMillis(Long.parseLong(value));
			}
			case "purchase_price" -> setPurchasePrice(Double.parseDouble(value));
		}
	}

	@Override
	public String toString() {
		return "Vehicle{" +
				"owner=" + owner +
				", registrationNumber='" + registrationNumber + '\'' +
				", maximumPassengers=" + maximumPassengers +
				", wheels=" + wheels +
				", constructionDate=" + constructionDate +
				", boughtDate=" + boughtDate +
				", purchasePrice=" + purchasePrice +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Vehicle vehicle = (Vehicle) o;

		if (maximumPassengers != vehicle.maximumPassengers) return false;
		if (wheels != vehicle.wheels) return false;
		if (Double.compare(vehicle.purchasePrice, purchasePrice) != 0) return false;
		if (!Objects.equals(owner, vehicle.owner)) return false;
		if (!Objects.equals(registrationNumber, vehicle.registrationNumber)) return false;
		if (!Objects.equals(constructionDate, vehicle.constructionDate)) return false;
		return Objects.equals(boughtDate, vehicle.boughtDate);
	}

	// Getters och setters

	// Krävs för Fetchable
	@Override
	public String getId() {
		return getRegistrationNumber();
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public int getMaximumPassengers() {
		return maximumPassengers;
	}

	public void setMaximumPassengers(int maximumPassengers) {
		if(maximumPassengers < 0) {
			throw new IllegalArgumentException("Negative amount of passengers");
		}
		this.maximumPassengers = maximumPassengers;
	}

	public int getWheels() {
		return wheels;
	}

	public void setWheels(int wheels) {
		if(wheels < 0) {
			throw new IllegalArgumentException("Negative amount of wheels");
		}
		this.wheels = wheels;
	}

	public Calendar getConstructionDate() {
		return constructionDate;
	}

	public void setConstructionDate(Calendar constructionDate) {
		this.constructionDate = constructionDate;
	}

	public Calendar getBoughtDate() {
		return boughtDate;
	}

	public void setBoughtDate(Calendar boughtDate) {
		if(boughtDate.before(constructionDate)) {
			throw new IllegalArgumentException("boughtDate can't be before constructionDate");
		}
		this.boughtDate = boughtDate;
	}

	public double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public String getOwnerId() {
		return ownerId;
	}
}
