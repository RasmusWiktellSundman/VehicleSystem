package se.rmsit.VehicleSystem.entities;

import se.rmsit.VehicleSystem.FileHandler;

import java.io.BufferedReader;
import java.io.IOException;

public abstract class Entity implements Fetchable {

	/**
	 * Laddar in ett nyckel-data par från persistent lagring
	 * @param key Nyckeln för värdet
	 * @param value Värdet
	 */
	public abstract void loadData(String key, String value);

	/**
	 * Laddar in data för en entitet
	 * @param reader BufferedReader med data att skapa object från.
	 * @throws IOException
	 */
	@Override
	public void load(BufferedReader reader) throws IOException {
		// Skapar objekt från reader data (samma som store metoden)
		while (true) {
			String line = reader.readLine();
			if(line == null) {
				break;
			}
			// Delar upp raden i nyckel-data par
			String[] tokens = line.split(": ");
			String data = tokens[1];
			if(data.equals("null")) {
				data = null;
			}
			loadData(tokens[0], data);
		}
	}
}
