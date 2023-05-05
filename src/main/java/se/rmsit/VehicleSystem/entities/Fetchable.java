package se.rmsit.VehicleSystem.entities;

import java.io.BufferedReader;
import java.io.IOException;

public interface Fetchable {
	/**
	 * Spara en entitet i persistent lagring.
	 */
	String serialize();

	/**
	 * Laddar in objektet från BufferedRader
	 * @param reader BufferedReader med data att skapa object från.
	 */
	void load(BufferedReader reader) throws IOException;
	String getId();
}
