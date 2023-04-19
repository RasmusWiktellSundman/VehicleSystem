package se.rmsit.VehicleSystem.entities.vehicles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.rmsit.VehicleSystem.entities.User;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class CarTest {
	private Car car;

	@BeforeEach
	void setUp() {
		Calendar today = Calendar.getInstance();
		car = new Car(new TestUser(), "abc", 4, 4, today, today, 50000);
	}

	@Test
	void throwsOnNegativeMaxPassengers() {
		Calendar today = Calendar.getInstance();
		assertThrows(IllegalArgumentException.class, () -> new Car(new TestUser(), "abc", -1, 4, today, today, 10));
		assertThrows(IllegalArgumentException.class, () -> car.setMaximumPassengers(-1));
	}

	@Test
	void throwsOnNegativeWheels() {
		Calendar today = Calendar.getInstance();
		assertThrows(IllegalArgumentException.class, () -> new Car(new TestUser(), "abc", 1, -4, today, today, 10));
		assertThrows(IllegalArgumentException.class, () -> car.setWheels(-1));
	}

	@Test
	void throwsOnBoughDateOlderThanConstructionDate() {
		Calendar today = Calendar.getInstance();
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DATE, -1);

		assertThrows(IllegalArgumentException.class, () -> new Car(new TestUser(), "abc", 1, 4, today, yesterday, 10));
		assertThrows(IllegalArgumentException.class, () -> car.setBoughtDate(yesterday));
	}

	private class TestUser extends User {
		public TestUser() {
			super(1, "Test", "test@testing.se", "test");
		}
	}
}