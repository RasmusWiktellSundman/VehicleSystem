package se.rmsit.VehicleSystem;

import java.io.*;
import java.net.URL;
import java.util.Properties;

public class Configuration {
	private static final Properties properties = new Properties();
	private static File appConfig;

	static {
		System.out.println("Testing");
		try {
			InputStream propsInput;
			if(System.getProperty("test.mode") != null){
				// Hanterar initiering för tester
				propsInput = Configuration.class.getClassLoader().getResourceAsStream("config.properties");
			} else {
				// Hanterar initiering på normalt sätt
				copyDefaultPropertiesFile();

				String currentUsersHomeDir = System.getProperty("user.home");
				System.out.println(currentUsersHomeDir);
				String configFilePath = currentUsersHomeDir+"/.config/VehicleSystem/config.properties";
				appConfig = new File(configFilePath);
				propsInput = new FileInputStream(configFilePath);
			}

			properties.load(propsInput);
		} catch (IOException e) {
			e.printStackTrace();
			// Utnyttjar RuntimeException då static inte kan kasta fel.
			throw new RuntimeException("Failed loading config file");
		}

	}

	/**
	 * Hämta ett värde från konfig filen
	 * @param key Nyckeln att hämta värde för
	 * @return Värdet som korresponderar med nyckeln
	 */
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 * Sätt/uppdatera ett värde i konfig-filen
	 * @param key Nyckeln att sätta/uppdatera
	 * @param value Värdet för nyckeln
	 */
	public static void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}

	public static void save() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(appConfig));
			properties.store(writer, "");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed saving config file");
		}
	}


	/**
	 * Kopierar standard config.properties och ersätter den sparade på hårddisk, ifall den inte redan finns
	 */
	private static void copyDefaultPropertiesFile() throws IOException {
		String currentUsersHomeDir = System.getProperty("user.home");
		String configFilePath = currentUsersHomeDir+"/.config/VehicleSystem/config.properties";
		System.out.println(currentUsersHomeDir);

		// Kollar om config.properties redan finns
		if(new File(configFilePath).exists())
			return;

		// Skapar mapp i användarens .config mapp i hemma-katalogen (om den inte finns)
		File configDir = new File(currentUsersHomeDir+"/.config/VehicleSystem");
		if(!configDir.exists()) {
			if(!configDir.mkdirs()) {
				throw new RuntimeException("Failed creating directory " + currentUsersHomeDir+"/.config/VehicleSystem, for config.properties");
			}
		}

		// Skapar config.properties fil
		new File(configFilePath).createNewFile();

		// Kopierar config.properties till disk
		// Hämtar config.properties från src/main/resources
		URL url = Configuration.class.getClassLoader().getResource("config.properties");

		BufferedReader reader = new BufferedReader(new FileReader(url.getFile()));
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(configFilePath)));

		while(true) {
			String line = reader.readLine();
			if(line == null) {
				break;
			}

			writer.println(line);
		}
		reader.close();
		writer.close();
	}
}
