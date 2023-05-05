package se.rmsit.VehicleSystem;

import se.rmsit.VehicleSystem.authentication.Authentication;
import se.rmsit.VehicleSystem.views.MainFrame;

public class VehicleSystem {
	public static void main(String[] args) {
		FileHandler.createDataFolders();

		Authentication authentication = new Authentication();

		// Startar GUI
		new MainFrame("VehicleSystem", authentication);
	}
}
