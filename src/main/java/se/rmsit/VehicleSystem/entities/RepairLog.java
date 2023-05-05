package se.rmsit.VehicleSystem.entities;

import se.rmsit.VehicleSystem.FileHandler;
import se.rmsit.VehicleSystem.entities.vehicles.Vehicle;
import se.rmsit.VehicleSystem.exceptions.CustomerDoesntExistException;
import se.rmsit.VehicleSystem.exceptions.VehicleDoesntExistException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class RepairLog implements Fetchable {
	private static List<RepairLog> repairs = new ArrayList<>();

	private String id;
	private Calendar date;
	private String description;
	// Sparar endast id:n för relaterade objekt, för att inte behöva läsa in dessa objekt ifall data från de inte behövs
	private String customerId;
	private String vehicleRegistrationNumber;

	static {
		try {
			reloadAllRepairLogsFromStorage();
		} catch (IOException e) {
			throw new RuntimeException("Failed loading repairs file");
		}
	}

	/**
	 * Standard konstruktor, används för att skapa objekt från persistent lagring.
	 */
	public RepairLog() {}

	public RepairLog(Calendar date, String description, Customer customer, Vehicle vehicle) {
		this(RepairLog.getNextId(), date, description, customer,vehicle);
	}

	public RepairLog(String id, Calendar date, String description, Customer customer, Vehicle vehicle) {
		setId(id);
		setDate(date);
		setDescription(description);
		setCustomer(customer);
		setVehicle(vehicle);
	}

	public static RepairLog getById(String id) {
		for (RepairLog repair : repairs) {
			if(repair.getId().equals(id))
				return repair;
		}
		return null;
	}

	public static List<RepairLog> getAll() {
		return repairs;
	}

	public static List<RepairLog> getAllByVehicle(Vehicle vehicle) {
		return repairs.stream().filter(
				repairLog -> repairLog.getVehicleRegistrationNumber().equals(vehicle.getRegistrationNumber())
		).toList();
	}

	public static String getNextId() {
		// Alla idn är av typen string, då Fetchable kräver det, men för reparationer är idn numeriska
		return String.valueOf(repairs.size() + 1);
	}

	public void save() throws IOException {
		if(!repairs.contains(this)) {
			repairs.add(this);
			FileHandler.appendObjectToFile(this, "repairs");
		}
	}

	@Override
	public String serialize() {
		return "repair_id: " + getId() + "\n" +
				"date: " + getDate().getTimeInMillis() + "\n" +
				"description: " + getDescription() + "\n" +
				"customer_id: " + getCustomerId() + "\n" +
				"vehicle_registration_number: " + getVehicleRegistrationNumber();
	}

	/**
	 * Laddar in ett nyckel-värde par till objektet
	 * @param key Nyckeln för värdet
	 * @param value Värdet
	 */
	@Override
	public void loadData(String key, String value) {
		switch (key) {
			case "repair_id" -> setId(value);
			case "date" -> {
				setDate(Calendar.getInstance());
				getDate().setTimeInMillis(Long.parseLong(value));
			}
			case "description" -> setDescription(value);
			case "customer_id" -> setCustomerId(value);
			case "vehicle_registration_number" -> setVehicleRegistrationNumber(value);
		}
	}

	/**
	 * Läser in och sparar alla reparationsloggar från loggfil
	 */
	public static void reloadAllRepairLogsFromStorage() throws IOException {
		repairs.clear();
		for (Fetchable fetchable : FileHandler.getAllObjectsFromOneFile("repairs")) {
			// Sparar i array, ifall objektet från fil inte är RepairLog är någon data fel och inte denna klass ansvar
			repairs.add((RepairLog) fetchable);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RepairLog repairLog = (RepairLog) o;

		if (!Objects.equals(id, repairLog.id)) return false;
		if (!Objects.equals(date, repairLog.date)) return false;
		if (!Objects.equals(description, repairLog.description))
			return false;
		if (!Objects.equals(customerId, repairLog.customerId)) return false;
		return Objects.equals(vehicleRegistrationNumber, repairLog.vehicleRegistrationNumber);
	}

	@Override
	public String toString() {
		return "RepairLog{" +
				"id='" + id + '\'' +
				", date=" + date +
				", description='" + description + '\'' +
				", customerId='" + customerId + '\'' +
				", vehicleRegistrationNumber='" + vehicleRegistrationNumber + '\'' +
				'}';
	}

	// Getters och setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	// Metoden är privat, då ett Customer-objekt ska användas utifrån, med setCustomer(customer)
	private void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomer(Customer customer) {
		this.customerId = customer.getCustomerId();
	}

	/**
	 * Hämtar kunden för en reparation
	 * @return Kunden som beställde reparationen
	 * @throws CustomerDoesntExistException Ges ifall kunden inte längre finns
	 */
	public Customer getCustomer() throws CustomerDoesntExistException {
		try {
			return Objects.requireNonNull((Customer) User.getById(customerId));
		} catch (IOException | NullPointerException e) {
			throw new CustomerDoesntExistException();
		}
	}

	// Metoden är privat, då ett fordonsobjekt ska användas utifrån, med setVehicle(vehicle)
	private void setVehicleRegistrationNumber(String vehicleRegistrationNumber) {
		this.vehicleRegistrationNumber = vehicleRegistrationNumber;
	}

	public String getVehicleRegistrationNumber() {
		return vehicleRegistrationNumber;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicleRegistrationNumber = vehicle.getRegistrationNumber();
	}

	public Vehicle getVehicle() throws VehicleDoesntExistException {
		try {
			return Objects.requireNonNull(Vehicle.getByRegistrationNumber(vehicleRegistrationNumber));
		} catch (IOException | NullPointerException e) {
			throw new VehicleDoesntExistException();
		}
	}
}
