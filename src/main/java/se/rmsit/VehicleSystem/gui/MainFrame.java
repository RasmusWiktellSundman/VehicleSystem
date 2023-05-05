package se.rmsit.VehicleSystem.gui;

import se.rmsit.VehicleSystem.authentication.Authentication;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {
	private JButton registerCustomerBtn;
	private JButton loginBtn;
	private JPanel content;
	private final CardLayout cardLayout = new CardLayout();
	private JPanel mainPanel;
	private JButton vehicleBtn;
	private JButton logoutBtn;
	private JButton registerVehicleBtn;

	private List<JButton> buttons = new ArrayList<>();
	private Map<Panels, PanelContainer> panelContainers = new HashMap<>();

	private Authentication authentication;
	private VehiclesPanel vehiclesPanel;

	public MainFrame(String title, Authentication authentication) {
		// Initiera JFrame
		super(title);

		// Sparar authentication i en medlemsvariabel
		this.authentication = authentication;

		// Populerar array med alla knappar
		addButtonsToArray();

		// Sätt innehållet för JFrame till mainframe (.form filen)
		setContentPane(mainPanel);
		// Ändrar JFrames standard storlek
		setSize(900, 700);
		// center window
		setLocationRelativeTo(null);
		// Stänger av programmet när användaren stänger programmet. Annars kör den i bakgrunden.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Laddar om GUI:t för att endast visa rätt knappar
		reloadGUI();

		// Visar JFramen
		setVisible(true);

		// Sätter Panel till att vara CardLayout
		content.setLayout(cardLayout);

		// Lägger till paneler till innehållspanelen
		panelContainers.put(Panels.LOGIN, new LoginPanel(this, authentication));
		panelContainers.put(Panels.REGISTER_CUSTOMER, new RegisterCustomerPanel());
		panelContainers.put(Panels.VEHICLES, new VehiclesPanel(authentication));
		panelContainers.put(Panels.REGISTER_VEHICLE, new RegisterVehiclePanel(authentication));
		panelContainers.forEach((panel, panelContainer) -> content.add(panelContainer.getPanel(), panel.name()));
		setContentPanel(Panels.LOGIN);

		// Aktiverar lyssnare (knappar)
		registerListeners();
	}

	private void addButtonsToArray() {
		buttons.add(vehicleBtn);
		buttons.add(loginBtn);
		buttons.add(registerCustomerBtn);
		buttons.add(logoutBtn);
		buttons.add(registerVehicleBtn);
	}

	private void registerListeners() {
		registerCustomerBtn.addActionListener(e -> setContentPanel(Panels.REGISTER_CUSTOMER));
		loginBtn.addActionListener(e -> setContentPanel(Panels.LOGIN));
		vehicleBtn.addActionListener(e -> setContentPanel(Panels.VEHICLES));
		registerVehicleBtn.addActionListener(e -> setContentPanel(Panels.REGISTER_VEHICLE));
		logoutBtn.addActionListener(e -> {
			authentication.logout();
			setContentPanel(Panels.LOGIN);
			reloadGUI();
		});
	}

	/**
	 * Ändrar vilken panel som visas
	 */
	public void setContentPanel(Panels panel) {
		// Renderar den angivna panelen
		panelContainers.get(panel).render();
		cardLayout.show(content, panel.name());
	}

	/**
	 * Laddar om innehållet i GUI:t
	 */
	public void reloadGUI() {
		// Döljer alla knappar
		for (JButton button : buttons) {
			button.setVisible(false);
		}

		if(authentication.isLoggedIn()) {
			// Visar knappar som endast ska synas när man är inloggad
			vehicleBtn.setVisible(true);
			logoutBtn.setVisible(true);
			registerVehicleBtn.setVisible(true);
		} else {
			// Visar knappar som endast syns när man är utloggad
			loginBtn.setVisible(true);
			registerCustomerBtn.setVisible(true);
		}
	}


}
