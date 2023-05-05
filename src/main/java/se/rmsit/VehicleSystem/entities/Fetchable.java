package se.rmsit.VehicleSystem.entities;

import java.io.BufferedReader;
import java.io.IOException;

public interface Fetchable {
	/**
	 * Spara en entitet i persistent lagring.
	 */
	String serialize();

	/**
	 * Laddar in ett nyckel-data par från persistent lagring
	 * @param key Nyckeln för värdet
	 * @param value Värdet
	 */
	void loadData(String key, String value);
	String getId();
}
