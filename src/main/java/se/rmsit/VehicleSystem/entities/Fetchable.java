package se.rmsit.VehicleSystem.entities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public interface Fetchable {
	/**
	 * Spara en entitet i persistent lagring.
	 * Första raden ska innehålla "class: class-name"
	 * @param printWriter Används för att skriva till persistent lagring
	 */
	void store(PrintWriter printWriter);

	/**
	 * Laddar in objektet från BufferedRader
	 * @param reader BufferedReader med data att skapa object från.
	 */
	void load(BufferedReader reader) throws IOException;
	String getId();
}
