package se.rmsit.VehicleSystem.entities.vehicles;

import se.rmsit.VehicleSystem.FileHandler;
import se.rmsit.VehicleSystem.entities.Customer;
import se.rmsit.VehicleSystem.entities.Fetchable;
import se.rmsit.VehicleSystem.entities.RepairLog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public abstract class Vehicle implements Fetchable {
	// Sparar endast ägarens id och inte hela objektet för att undvika synkroniseringsfel mellan persistent lagring och minne
	private String ownerId;
	private String registrationNumber;
	private int maximumPassengers;
	private int wheels;
	private Calendar constructionDate;
	private Calendar boughtDate;
	private double purchasePrice;
	private Calendar warrantyPeriodEnd;

	public Vehicle(Customer owner, String registrationNumber, int maximumPassengers, int wheels, Calendar constructionDate, Calendar boughtDate, Calendar warrantyPeriodEnd, double purchasePrice) {
		setOwner(owner);
		setRegistrationNumber(registrationNumber);
		setMaximumPassengers(maximumPassengers);
		setWheels(wheels);
		setConstructionDate(constructionDate);
		setBoughtDate(boughtDate);
		setWarrantyPeriodEnd(warrantyPeriodEnd);
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

	public static Vehicle getByRegistrationNumber(String registrationNumber) throws IOException {
		// Hämtar användare från persistent lagring
		return (Vehicle) FileHandler.loadObject(registrationNumber, "vehicles");
	}

	public static List<Vehicle> getByOwner(Customer owner) throws IOException {
		List<Vehicle> vehicles = new ArrayList<>();
		for (Fetchable fetchable : FileHandler.getAllObjects("vehicles")) {
			// Kollar om e-posten från den inlästa användaren är samma som den givna e-posten
			if(((Vehicle) fetchable).getOwnerId().equals(owner.getId())) {
				vehicles.add((Vehicle) fetchable);
			}
		}
		return vehicles;
	}

	/**
	 * Sparar objektet till persistent lagring. Skriver över ifall fordon med samma registreringsnummer redan finns.
	 * @throws IOException
	 */
	public void save() throws IOException {
		FileHandler.storeObject(this, "vehicles");
	}

	/**
	 * Raderar fordon från persistent lagring
	 * @throws IOException
	 */
	public void delete() throws IOException {
		FileHandler.deleteObject(getId(), "vehicles");
	}

	/**
	 * Kalkylerar fordonets värde
	 * @return Fordonets värde
	 */
	public double getValue() {
		double value = getPurchasePrice();
		List<RepairLog> repairs = RepairLog.getAllByVehicle(this);
		Calendar now = Calendar.getInstance();

		// Start och slut beräknas från när bilen är konstruerad, inte kalenderår
		Calendar year_start = Calendar.getInstance();
		year_start.setTimeInMillis(getConstructionDate().getTimeInMillis());
		Calendar year_end = Calendar.getInstance();
		year_end.setTimeInMillis(getConstructionDate().getTimeInMillis());
		year_end.add(Calendar.YEAR, 1);

		// Loopar så länge datumet är före nu, varje varv motsvarar ett fullständigt år
		while(year_end.before(now) || year_end.equals(now)) {
			// Beräknar antal reparationer under året
			long amountOfRepairs = repairs.stream().filter(
					repairLog -> year_start.equals(repairLog.getDate()) ||
							(year_start.before(repairLog.getDate()) && year_end.after(repairLog.getDate()))
			).count();

			// Ökar värdet med 20% för varje reparation
			value *= Math.pow(1.2, amountOfRepairs);

			// Kollar om värdet är mer än 100%
			if(value > getPurchasePrice()) {
				value = getPurchasePrice();
			}

			// Subtraherar 10% av värdet
			value *= 0.9;

			// Uppdaterar variabler för kommande år
			year_start.add(Calendar.YEAR, 1);
			year_end.add(Calendar.YEAR, 1);
		}

		// Lägger till värdet för reparationer som utförts under senaste ofullständiga år
		long amountOfRepairs = repairs.stream().filter(
				repairLog -> year_start.equals(repairLog.getDate()) || year_start.before(repairLog.getDate())
		).count();
		// Ökar värdet med 20% för varje reparation
		value *= Math.pow(1.2, amountOfRepairs);

		// Kollar om värdet är mer än 100%
		if(value > getPurchasePrice()) {
			value = getPurchasePrice();
		}

		return value;
	}

	public void addRepair(String description, Calendar date) throws IOException {
		RepairLog repairLog = new RepairLog(date, description, getOwner(), this);
		repairLog.save();
	}

	@Override
	public String serialize() {
		return "owner_id: " + getOwner().getId() + "\n" +
				"registration_number: " + getRegistrationNumber() + "\n" +
				"maximum_passengers: " + getMaximumPassengers() + "\n" +
				"wheels: " + getWheels() + "\n" +
				"construction_date: " + getConstructionDate().getTimeInMillis() + "\n" +
				"bought_date: " + getBoughtDate().getTimeInMillis() + "\n" +
				"warranty_period_end: " + getWarrantyPeriodEnd().getTimeInMillis() + "\n" +
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
			case "warranty_period_end" -> {
				setWarrantyPeriodEnd(Calendar.getInstance());
				getWarrantyPeriodEnd().setTimeInMillis(Long.parseLong(value));
			}
			case "purchase_price" -> setPurchasePrice(Double.parseDouble(value));
		}
	}

	@Override
	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return "Vehicle{" +
				"ownerId='" + ownerId + '\'' +
				", registrationNumber='" + registrationNumber + '\'' +
				", maximumPassengers=" + maximumPassengers +
				", wheels=" + wheels +
				", constructionDate=" + (constructionDate != null ? dateFormat.format(constructionDate.getTime()) : null) +
				", boughtDate=" + (boughtDate != null ? dateFormat.format(boughtDate.getTime()) : null) +
				", warrantyPeriodEnd=" + (warrantyPeriodEnd != null ? dateFormat.format(warrantyPeriodEnd.getTime()) : null) +
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
		if (!Objects.equals(ownerId, vehicle.ownerId)) return false;
		if (!Objects.equals(registrationNumber, vehicle.registrationNumber)) return false;
		if (!Objects.equals(constructionDate, vehicle.constructionDate)) return false;
		if (!Objects.equals(warrantyPeriodEnd, vehicle.warrantyPeriodEnd)) return false;
		return Objects.equals(boughtDate, vehicle.boughtDate);
	}

	// Getters och setters

	// Krävs för Fetchable
	@Override
	public String getId() {
		return getRegistrationNumber();
	}

	public Customer getOwner() {
		try {
			return Customer.getById(ownerId);
		} catch (IOException e) {
			throw new RuntimeException("Tried getting Vehicle owner for a user id that doesn't exist");
		}
	}

	public void setOwner(Customer owner) {
		this.ownerId = owner.getId();
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

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public Calendar getWarrantyPeriodEnd() {
		return warrantyPeriodEnd;
	}

	public void setWarrantyPeriodEnd(Calendar warrantyPeriodEnd) {
		this.warrantyPeriodEnd = warrantyPeriodEnd;
	}

	public List<RepairLog> getRepairs() {
		return RepairLog.getAllByVehicle(this);
	}
}
