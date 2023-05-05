package se.rmsit.VehicleSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestHelper {
	public static void resetDataFiles() throws IOException {
		// Remove test data directory
		File dataFolder = new File(Configuration.getProperty("data_directory"));
		if(dataFolder.exists()) {
			for (File directories : dataFolder.listFiles()) {
				if (directories.isDirectory()) {
					for (File file : directories.listFiles()) {
						Files.delete(Path.of(file.toURI()));
					}
				}
			}
		}
		FileHandler.createDataFolders();
	}
}
