package se.rmsit.VehicleSystem.entities.vehicles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.rmsit.VehicleSystem.Configuration;
import se.rmsit.VehicleSystem.FileHandler;
import se.rmsit.VehicleSystem.TestHelper;
import se.rmsit.VehicleSystem.TestUser;
import se.rmsit.VehicleSystem.TestVehicle;
import se.rmsit.VehicleSystem.exceptions.DuplicateEntityException;
import se.rmsit.VehicleSystem.repositories.UserRepository;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehicleTest {
	TestUser testUser;
	UserRepository userRepository;
	@BeforeEach
	void setup() throws IOException, DuplicateEntityException {
		TestHelper.resetDataFiles();
		userRepository = new UserRepository();
		testUser = new TestUser("1", "dsa", "dasd", "test@testomg.se", "something");
		userRepository.update(testUser);
	}

	@Test
	void canStoreVehicle() {
		Vehicle vehicle = new TestVehicle(testUser, "ABC123", 4, 4, Calendar.getInstance(), Calendar.getInstance(), 100);
		assertDoesNotThrow(() -> FileHandler.storeObject(vehicle, "vehicles"));
		assertTrue(new File(Configuration.getProperty("data_directory")+"/vehicles/ABC123.txt").exists());
	}

	@Test
	void canLoadUser() throws IOException {
		Vehicle vehicle = new TestVehicle(testUser, "ABC123", 4, 4, Calendar.getInstance(), Calendar.getInstance(), 100);
		assertDoesNotThrow(() -> FileHandler.storeObject(vehicle, "vehicles"));
		assertEquals(vehicle, FileHandler.loadObject(new TestVehicle(userRepository), "ABC123","vehicles"));
	}
}