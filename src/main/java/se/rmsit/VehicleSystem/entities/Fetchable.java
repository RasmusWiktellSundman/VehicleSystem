package se.rmsit.VehicleSystem.entities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public interface Fetchable {
	void store(PrintWriter printWriter);

	/**
	 * Laddar in objektet från BufferedRader
	 * @param reader BufferedReader med data att skapa object från.
	 */
	void load(BufferedReader reader) throws IOException;
	long getId();
}
