package se.rmsit.VehicleSystem.entities.vehicles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.rmsit.VehicleSystem.Configuration;
import se.rmsit.VehicleSystem.TestHelper;
import se.rmsit.VehicleSystem.TestVehicle;
import se.rmsit.VehicleSystem.entities.Customer;
import se.rmsit.VehicleSystem.exceptions.DuplicateEntityException;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {
	Customer testCustomer;
	@BeforeEach
	void setup() throws IOException, DuplicateEntityException {
		TestHelper.resetDataFiles();
		testCustomer = new Customer("1", "Test", "Dev", null, null, null, null, false, "test@testing.se", "hej");
		testCustomer.save();
	}

	@Test
	void canStoreVehicle() {
		Vehicle vehicle = new TestVehicle(testCustomer, "ABC123", 4, 4, Calendar.getInstance(), Calendar.getInstance(), 100);
		assertDoesNotThrow(vehicle::save);
		assertTrue(new File(Configuration.getProperty("data_directory")+"/vehicles/ABC123.txt").exists());
	}

	@Test
	void canGetUserFromStorageByRegistrationNumber() throws IOException {
		Vehicle vehicle = new TestVehicle(testCustomer, "ABC123", 4, 4, Calendar.getInstance(), Calendar.getInstance(), 100);
		Vehicle vehicle2 = new TestVehicle(testCustomer, "ABC124", 1, 2, Calendar.getInstance(), Calendar.getInstance(), 100);
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
		Vehicle testVehicle = new TestVehicle(testCustomer, "ABC123", 4, 4, today, today, 100.1);
		Vehicle testVehicle2 = new TestVehicle(testCustomer, "ABC124", 4, 4, today, today, 13000.1);
		testVehicle.save();
		testVehicle2.save();

		assertDoesNotThrow(testVehicle2::delete);
		assertNull(Vehicle.getByRegistrationNumber("ABC124"));
		assertFalse(new File(Configuration.getProperty("data_directory")+"/vehicles/ABC124.txt").exists());
	}

	@Test
	void canGetVehiclesByOwner() throws DuplicateEntityException, IOException {
		Customer testCustomer2 = new Customer("2", "Test2", "something", null, null, null, null, false, "test2@testing.se", "something");
		testCustomer2.save();

		// Skapar fordon
		Calendar today = Calendar.getInstance();
		Vehicle testVehicle = new TestVehicle(testCustomer, "ABC123", 4, 4, today, today, 100.1);
		Vehicle testVehicle2 = new TestVehicle(testCustomer2, "ABC124", 4, 4, today, today, 13000.1);
		Vehicle testVehicle3 = new TestVehicle(testCustomer, "ABC125", 2, 4, today, today, 11000.1);
		testVehicle.save();
		testVehicle2.save();
		testVehicle3.save();

		List<Vehicle> expected = List.of(testVehicle, testVehicle3);
		assertEquals(expected, Vehicle.getByOwner(testCustomer));
	}
}