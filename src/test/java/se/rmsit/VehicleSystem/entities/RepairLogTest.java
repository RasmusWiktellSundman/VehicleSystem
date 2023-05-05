package se.rmsit.VehicleSystem.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.rmsit.VehicleSystem.Configuration;
import se.rmsit.VehicleSystem.TestHelper;
import se.rmsit.VehicleSystem.TestVehicle;
import se.rmsit.VehicleSystem.entities.vehicles.Vehicle;
import se.rmsit.VehicleSystem.exceptions.CustomerDoesntExistException;
import se.rmsit.VehicleSystem.exceptions.VehicleDoesntExistException;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepairLogTest {

	Customer testCustomer;
	Vehicle testVehicle;
	@BeforeEach
	void setup() throws IOException {
		TestHelper.resetDataFiles();
		RepairLog.reloadAllRepairLogsFromStorage();
		testCustomer = new Customer("1", "cust", "test", null, null, null, null, false, "test@testing.se", "password");
		testCustomer.save();
		Calendar today = Calendar.getInstance();
		testVehicle = new TestVehicle(testCustomer, "ABC123", 4, 4, today, today, 1000);
		testVehicle.save();
	}

	@Test
	void canStoreRepairLog() {
		Calendar today = Calendar.getInstance();
		RepairLog repairLog = new RepairLog("1", today, "Testing", testCustomer, testVehicle);
		assertDoesNotThrow(repairLog::save);
		assertTrue(new File(Configuration.getProperty("data_directory")+"/repairs.txt").exists());
	}

	@Test
	void canGetRepairLog() throws IOException {
		Calendar today = Calendar.getInstance();
		RepairLog repairLog = new RepairLog("1", today, "Testing", testCustomer, testVehicle);
		repairLog.save();

		assertEquals(repairLog, RepairLog.getById("1"));

		// Laddar om från disk
		RepairLog.reloadAllRepairLogsFromStorage();
		assertEquals(repairLog, RepairLog.getById("1"));
	}

	@Test
	void cantGetNonExistingRepairLog() throws IOException {
		assertNull(RepairLog.getById("1"));

		// Laddar om från disk
		RepairLog.reloadAllRepairLogsFromStorage();
		assertNull(RepairLog.getById("1"));
	}

	@Test
	void canGetCustomer() throws IOException, CustomerDoesntExistException {
		Calendar today = Calendar.getInstance();
		RepairLog repairLog = new RepairLog("1", today, "Testing", testCustomer, testVehicle);
		repairLog.save();

		assertEquals(testCustomer, repairLog.getCustomer());
		assertEquals(testCustomer, RepairLog.getById("1").getCustomer());

		// Laddar om från disk
		RepairLog.reloadAllRepairLogsFromStorage();
		assertEquals(testCustomer, RepairLog.getById("1").getCustomer());
	}

	@Test
	void canGetVehicle() throws IOException, VehicleDoesntExistException {
		Calendar today = Calendar.getInstance();
		RepairLog repairLog = new RepairLog("1", today, "Testing", testCustomer, testVehicle);
		repairLog.save();

		assertEquals(testVehicle, repairLog.getVehicle());
		assertEquals(testVehicle, RepairLog.getById("1").getVehicle());

		// Laddar om från disk
		RepairLog.reloadAllRepairLogsFromStorage();
		assertEquals(testVehicle, RepairLog.getById("1").getVehicle());
	}

	@Test
	void cantGetDonExistingCustomer() throws IOException {
		Calendar today = Calendar.getInstance();
		RepairLog repairLog = new RepairLog("1", today, "Testing", testCustomer, testVehicle);
		repairLog.save();

		// Raderar kund
		testCustomer.delete();

		assertThrows(CustomerDoesntExistException.class, repairLog::getCustomer);
	}

	@Test
	void cantGetDonExistingVehicle() throws IOException {
		Calendar today = Calendar.getInstance();
		RepairLog repairLog = new RepairLog("1", today, "Testing", testCustomer, testVehicle);
		repairLog.save();

		// Raderar fordon
		testVehicle.delete();

		assertThrows(VehicleDoesntExistException.class, repairLog::getVehicle);
	}

	@Test
	void canGetAllRepairs() throws IOException {
		Calendar today = Calendar.getInstance();
		RepairLog repairLog = new RepairLog("1", today, "Testing", testCustomer, testVehicle);
		RepairLog repairLog2 = new RepairLog("2", today, "Testing2", testCustomer, testVehicle);
		repairLog.save();
		repairLog2.save();

		List<RepairLog> expected = List.of(repairLog, repairLog2);
		assertEquals(expected, RepairLog.getAll());

		// Laddar om från disk
		RepairLog.reloadAllRepairLogsFromStorage();
		assertEquals(expected, RepairLog.getAll());
	}

	@Test
	void updatesRepairLogIfUsingSameId() throws IOException {
		Calendar today = Calendar.getInstance();
		RepairLog repairLog = new RepairLog("1", today, "Testing", testCustomer, testVehicle);
		repairLog.save();
		repairLog.setDescription("Testing2");
		repairLog.save();

		assertEquals(repairLog, RepairLog.getById("1"));

		// Laddar om från disk
		RepairLog.reloadAllRepairLogsFromStorage();
		assertEquals(repairLog, RepairLog.getById("1"));
	}

	@Test
	void canGetRepairLogsByVehicle() throws IOException {
		Calendar today = Calendar.getInstance();
		Vehicle testVehicle2 = new TestVehicle(testCustomer, "ABC124", 4, 4, today, today, 1000);
		testVehicle2.save();

		RepairLog repairLog = new RepairLog("1", today, "Testing", testCustomer, testVehicle);
		repairLog.save();
		RepairLog repairLog2 = new RepairLog("2", today, "Testing3", testCustomer, testVehicle2);
		repairLog2.save();
		RepairLog repairLog3 = new RepairLog("3", today, "Testing3", testCustomer, testVehicle);
		repairLog3.save();

		List<RepairLog> expected = List.of(repairLog, repairLog3);
		assertEquals(expected, RepairLog.getAllByVehicle(testVehicle));
	}

	@Test
	void canGetNextId() throws IOException {
		Calendar today = Calendar.getInstance();
		RepairLog repairLog = new RepairLog("1", today, "Testing", testCustomer, testVehicle);
		repairLog.save();
		RepairLog repairLog2 = new RepairLog("2", today, "Testing", testCustomer, testVehicle);
		repairLog2.save();
		RepairLog repairLog3 = new RepairLog("3", today, "Testing", testCustomer, testVehicle);
		repairLog3.save();

		assertEquals("4", RepairLog.getNextId());
	}

	@Test
	void canAddRepairsWithAutomaticId() throws IOException {
		Calendar today = Calendar.getInstance();
		RepairLog repairLog = new RepairLog(today, "Testing", testCustomer, testVehicle);
		repairLog.save();
		RepairLog repairLog2 = new RepairLog(today, "Testing", testCustomer, testVehicle);
		repairLog2.save();
		RepairLog repairLog3 = new RepairLog(today, "Testing", testCustomer, testVehicle);
		repairLog3.save();

		RepairLog repairLogExpected1 = new RepairLog("1", today, "Testing", testCustomer, testVehicle);
		RepairLog repairLogExpected2 = new RepairLog("2", today, "Testing", testCustomer, testVehicle);
		RepairLog repairLogExpected3 = new RepairLog("3", today, "Testing", testCustomer, testVehicle);
		List<RepairLog> expected = List.of(repairLogExpected1, repairLogExpected2, repairLogExpected3);
		assertEquals(expected, RepairLog.getAll());
	}
}