package se.rmsit.VehicleSystem.entities.vehicles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.rmsit.VehicleSystem.Configuration;
import se.rmsit.VehicleSystem.TestHelper;
import se.rmsit.VehicleSystem.TestVehicle;
import se.rmsit.VehicleSystem.entities.Customer;
import se.rmsit.VehicleSystem.entities.RepairLog;
import se.rmsit.VehicleSystem.exceptions.DuplicateEntityException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {
	Customer testCustomer;
	@BeforeEach
	void setup() throws IOException {
		TestHelper.resetDataFiles();
		RepairLog.reloadAllRepairLogsFromStorage();
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

	@Test
	void canAddRepair() throws IOException {
		// Skapar fordon
		Calendar today = Calendar.getInstance();
		Vehicle testVehicle = new TestVehicle(testCustomer, "ABC123", 4, 4, today, today, 100.1);
		testVehicle.save();
		assertDoesNotThrow(() -> testVehicle.addRepair("A description", today));

		RepairLog expected = new RepairLog("1", today, "A description", testCustomer, testVehicle);
		assertEquals(List.of(expected), RepairLog.getAllByVehicle(testVehicle));
	}

	@Test
	void canGetRepairs() throws IOException {
		// Skapar fordon
		Calendar today = Calendar.getInstance();
		Vehicle testVehicle = new TestVehicle(testCustomer, "ABC123", 4, 4, today, today, 100.1);
		testVehicle.save();
		testVehicle.addRepair("A description", today);

		RepairLog expected = new RepairLog("1", today, "A description", testCustomer, testVehicle);
		assertEquals(List.of(expected), testVehicle.getRepairs());
	}

	@Test
	void canGetValue() throws IOException {
		Calendar date = Calendar.getInstance();
		// Kollar att värdet av ett nytt fordon är 100%
		Vehicle testVehicle = new TestVehicle(testCustomer, "ABC123", 4, 4, date, date, 10000);
		testVehicle.save();
		assertEquals(10000, testVehicle.getValue());
	}

	@Test
	void canGetValue2() throws IOException {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.YEAR, -3);
		Vehicle testVehicle = new TestVehicle(testCustomer, "ABC123", 4, 4, date, date, 10000);
		testVehicle.save();

		// Kollar att värdet av ett tre år gammalt fordon är 72.9% (1 * 0.9^3)
		assertEquals(7290, testVehicle.getValue());
	}

	@Test
	void canGetValue3() throws IOException {
		// Kollar att värdet av ett fem år gammalt fordon med två reparation, varav ena under tredje året och andra under början av femte året. Värdet ska vara 85,03056% (1 * 0.9 * 0.9 * 1.2 * 0.9 * 0.9 * 0.9 * 1.2)
		Calendar date = Calendar.getInstance();
		Calendar date2 = Calendar.getInstance();
		Calendar date3 = Calendar.getInstance();
		date.add(Calendar.YEAR, -5);
		date.add(Calendar.SECOND, -10);
		Vehicle testVehicle = new TestVehicle(testCustomer, "ABC123", 4, 4, date, date, 10000);
		testVehicle.save();
		date2.add(Calendar.YEAR, -2);
		date2.add(Calendar.DAY_OF_YEAR, -10);
		testVehicle.addRepair("Test", date2);
		testVehicle.addRepair("Test", date3);

		assertEquals(8503.056, testVehicle.getValue());
	}

	@Test
	void canGetValue4() throws IOException {
		// Kollar att värdet av ett två år gammalt fordon med en reparation, där värdet efter reparationen blir > 100%. Värdet ska vara 90,00% ((1 * 0.9 * 1.2) = 1.08 => 1 * .9 = .9)
		Calendar date = Calendar.getInstance();
		date.add(Calendar.YEAR, -2);
		Calendar date2 = Calendar.getInstance();
		date2.add(Calendar.DAY_OF_YEAR, -10);

		Vehicle testVehicle = new TestVehicle(testCustomer, "ABC123", 4, 4, date, date, 10000);
		testVehicle.save();

		testVehicle.addRepair("Test", date2);

		assertEquals(9000, testVehicle.getValue());
	}

	@Test
	void cantSetInvalidRegistrationNumber() {
		Calendar date = Calendar.getInstance();
		assertThrows(IllegalArgumentException.class, () -> new TestVehicle(testCustomer, "ABCd123", 4, 4, date, date, 10000));
	}
}