package se.rmsit.VehicleSystem.entities.vehicles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.rmsit.VehicleSystem.Configuration;
import se.rmsit.VehicleSystem.FileHandler;
import se.rmsit.VehicleSystem.TestHelper;
import se.rmsit.VehicleSystem.TestUser;
import se.rmsit.VehicleSystem.TestVehicle;
import se.rmsit.VehicleSystem.entities.User;
import se.rmsit.VehicleSystem.exceptions.DuplicateEntityException;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {
	TestUser testUser;
	@BeforeEach
	void setup() throws IOException, DuplicateEntityException {
		TestHelper.resetDataFiles();
		testUser = new TestUser("1", "dsa", "dasd", "test@testomg.se", "something");
		testUser.save();
	}

	@Test
	void canStoreVehicle() {
		Vehicle vehicle = new TestVehicle(testUser, "ABC123", 4, 4, Calendar.getInstance(), Calendar.getInstance(), 100);
		assertDoesNotThrow(vehicle::save);
		assertTrue(new File(Configuration.getProperty("data_directory")+"/vehicles/ABC123.txt").exists());
	}

	@Test
	void canGetUserFromStorageByRegistrationNumber() throws IOException {
		Vehicle vehicle = new TestVehicle(testUser, "ABC123", 4, 4, Calendar.getInstance(), Calendar.getInstance(), 100);
		Vehicle vehicle2 = new TestVehicle(testUser, "ABC124", 1, 2, Calendar.getInstance(), Calendar.getInstance(), 100);
		assertDoesNotThrow(vehicle::save);
		assertDoesNotThrow(vehicle2::save);
		assertEquals(vehicle, Vehicle.getByRegistrationNumber("ABC123"));
		assertEquals(vehicle2, Vehicle.getByRegistrationNumber("ABC124"));
	}

	@Test
	void cantGetNonExistingVehicle() throws IOException {
		assertNull(Vehicle.getByRegistrationNumber("A"));
	}

	@Test
	void canDeleteVehicle() throws IOException {
		Calendar today = Calendar.getInstance();
		Vehicle testVehicle = new TestVehicle(testUser, "ABC123", 4, 4, today, today, 100.1);
		Vehicle testVehicle2 = new TestVehicle(testUser, "ABC124", 4, 4, today, today, 13000.1);
		testVehicle.save();
		testVehicle2.save();

		assertDoesNotThrow(testVehicle2::delete);
		assertNull(Vehicle.getByRegistrationNumber("ABC124"));
		assertFalse(new File(Configuration.getProperty("data_directory")+"/vehicles/ABC124.txt").exists());
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
		testVehicle.save();
		testVehicle2.save();
		testVehicle3.save();

		List<Vehicle> expected = List.of(testVehicle, testVehicle3);
		assertEquals(expected, Vehicle.getByOwner(testUser));
	}
}