package se.rmsit.VehicleSystem.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.rmsit.VehicleSystem.Configuration;
import se.rmsit.VehicleSystem.TestHelper;
import se.rmsit.VehicleSystem.TestUser;
import se.rmsit.VehicleSystem.TestVehicle;
import se.rmsit.VehicleSystem.entities.User;
import se.rmsit.VehicleSystem.entities.vehicles.Vehicle;
import se.rmsit.VehicleSystem.exceptions.DuplicateEntityException;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class VehicleRepositoryTest {

	private TestUser testUser;
	private VehicleRepository vehicleRepository;

	@BeforeEach
	void setup() throws IOException {
		TestHelper.resetDataFiles();
		testUser = new TestUser("1", "dsa", "dasd", "test@testomg.se", "something");
		testUser.save();
		vehicleRepository = new VehicleRepository();
	}

	@Test
	void repositoryCanStoreVehicle() {
		Calendar today = Calendar.getInstance();
		Vehicle testVehicle = new TestVehicle(testUser, "ABC123", 4, 4, today, today, 100.1);
		assertDoesNotThrow(() -> vehicleRepository.update(testVehicle));
		assertTrue(new File(Configuration.getProperty("data_directory")+"/vehicles/ABC123.txt").exists());
	}

	@Test
	void canGetVehicleByRegistrationNumberFromRepository() throws IOException, DuplicateEntityException {
		Calendar today = Calendar.getInstance();
		Vehicle testVehicle = new TestVehicle(testUser, "ABC123", 4, 4, today, today, 100.1);
		Vehicle testVehicle2 = new TestVehicle(testUser, "ABC124", 4, 4, today, today, 100.1);
		vehicleRepository.update(testVehicle);
		vehicleRepository.update(testVehicle2);

		assertEquals(testVehicle, vehicleRepository.getByRegistrationNumber("ABC123").get());
		assertEquals(testVehicle2, vehicleRepository.getByRegistrationNumber("ABC124").get());
	}

	@Test
	void cantGetNonExistingVehicle() {
		assertThrows(NoSuchElementException.class, () -> vehicleRepository.getByRegistrationNumber("1").get());
	}

	@Test
	void canGetAllVehiclesFromRepository() throws IOException, DuplicateEntityException {
		Calendar today = Calendar.getInstance();
		Vehicle testVehicle = new TestVehicle(testUser, "ABC123", 4, 4, today, today, 100.1);
		Vehicle testVehicle2 = new TestVehicle(testUser, "ABC124", 4, 4, today, today, 13000.1);
		vehicleRepository.update(testVehicle);
		vehicleRepository.update(testVehicle2);

		List<Vehicle> expected = List.of(testVehicle, testVehicle2);
		assertEquals(expected, vehicleRepository.getAll());
	}

	@Test
	void canDeleteVehicle() throws IOException, DuplicateEntityException {
		Calendar today = Calendar.getInstance();
		Vehicle testVehicle = new TestVehicle(testUser, "ABC123", 4, 4, today, today, 100.1);
		Vehicle testVehicle2 = new TestVehicle(testUser, "ABC124", 4, 4, today, today, 13000.1);
		vehicleRepository.update(testVehicle);
		vehicleRepository.update(testVehicle2);

		assertDoesNotThrow(() -> vehicleRepository.delete(testVehicle2));
		assertFalse(vehicleRepository.getAll().contains(testVehicle2));
		assertTrue(() -> vehicleRepository.getByRegistrationNumber("ABC124").isEmpty());
		assertFalse(new File(Configuration.getProperty("data_directory")+"/vehicles/ABC124.txt").exists());
	}

	@Test
	void canLoadAllFromFile() throws IOException {
		Calendar today = Calendar.getInstance();
		Vehicle testVehicle = new TestVehicle(testUser, "ABC123", 4, 4, today, today, 100.1);
		Vehicle testVehicle2 = new TestVehicle(testUser, "ABC124", 4, 4, today, today, 13000.1);
		vehicleRepository.update(testVehicle);
		vehicleRepository.update(testVehicle2);

		// Skapar nytt repository för att ladda in från fil
		vehicleRepository = new VehicleRepository();

		List<Vehicle> expected = List.of(testVehicle, testVehicle2);
		assertIterableEquals(expected, vehicleRepository.getAll());
	}

	@Test
	void canGetVehiclesByOwner() throws DuplicateEntityException, IOException {
		User testUser2 = new TestUser("2", "Test2", "something", "test2@testing.se", "aHash");
		testUser2.save();

		// Skapar fordon
		Calendar today = Calendar.getInstance();
		Vehicle testVehicle = new TestVehicle(testUser, "ABC123", 4, 4, today, today, 100.1);
		Vehicle testVehicle2 = new TestVehicle(testUser2, "ABC124", 4, 4, today, today, 13000.1);
		Vehicle testVehicle3 = new TestVehicle(testUser, "ABC125", 2, 4, today, today, 11000.1);
		vehicleRepository.update(testVehicle);
		vehicleRepository.update(testVehicle2);
		vehicleRepository.update(testVehicle3);

		List<Vehicle> expected = List.of(testVehicle, testVehicle3);
		assertEquals(expected, vehicleRepository.getByOwner(testUser));
	}
}