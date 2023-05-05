package se.rmsit.VehicleSystem.repositories;

import se.rmsit.VehicleSystem.FileHandler;
import se.rmsit.VehicleSystem.entities.Fetchable;
import se.rmsit.VehicleSystem.entities.User;
import se.rmsit.VehicleSystem.entities.vehicles.Vehicle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehicleRepository {
	private UserRepository userRepository;
	private List<Vehicle> vehicles = new ArrayList<>();

	public VehicleRepository(UserRepository userRepository) throws IOException {
		this.userRepository = userRepository;
		loadVehicles();
	}

	private void loadVehicles() throws IOException {
		// Populerar users array med data från persistent lagring
		for (Fetchable fetchable : FileHandler.getAllObjects("vehicles")) {
			if(fetchable instanceof Vehicle) {
				Vehicle vehicle = (Vehicle) fetchable;
				vehicle.setOwner(userRepository.getById(vehicle.getOwnerId()).get());
				vehicles.add(vehicle);
			}
		}
	}

	public Optional<Vehicle> getByRegistrationNumber(String registrationNumber) {
		for (Vehicle vehicle : vehicles) {
			if(vehicle.getRegistrationNumber().equals(registrationNumber)) {
				return Optional.of(vehicle);
			}
		}
		return Optional.empty();
	}

	public void update(Vehicle vehicle) throws IOException {
		if(!vehicles.contains(vehicle)) {
			vehicles.add(vehicle);
		}
		FileHandler.storeObject(vehicle,"vehicles");
	}

	public List<Vehicle> getAll() {
		return vehicles;
	}

	public void delete(Vehicle vehicle) throws IOException {
		vehicles.remove(vehicle);
		FileHandler.deleteObject(vehicle.getRegistrationNumber(), "vehicles");
	}

	public List<Vehicle> getByOwner(User owner) {
		return vehicles.stream().filter(vehicle -> vehicle.getOwner().equals(owner)).toList();
	}
}
