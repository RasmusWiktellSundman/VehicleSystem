package se.rmsit.VehicleSystem;

import java.io.File;

public class TestHelper {
	public static void resetDataFiles() {
		// Remove test data directory
		File dataFolder = new File(Configuration.getProperty("data_directory"));
		if(dataFolder.exists()) {
			for (File directories : dataFolder.listFiles()) {
				if (directories.isDirectory()) {
					for (File file : directories.listFiles()) {
						file.delete();
					}
				}
			}
		}
		FileHandler.createDataFolders();
	}
}
