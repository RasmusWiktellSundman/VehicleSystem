package se.rmsit.VehicleSystem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationTest {

	@Test
	void canGetFromTestPropertiesFile() {
		assertDoesNotThrow(() -> Configuration.getProperty("test"));
		assertEquals(Configuration.getProperty("isTest"), "true");
	}
}