package gui.subpanels;
import person.Location;
import person.Location.LocationType;
import person.PersonAgent;
import person.Position;
import person.SimEvent;
import person.SimEvent.EventType;
import person.SimWorldClock;
import person.gui.PersonGui; 
import gui.panels.CityControlPanel;
import gui.main.SimCityGUI; 

import java.awt.*;
import java.awt.event.*;

import javax.swing.Timer;

import java.util.*;
import java.util.List;

import javax.swing.*;

/**
 * This panel allows the user to interact with a person
 * in SimCity
 */

public class ScenarioPanel extends JPanel implements ActionListener{

	private SimCityGUI simcitygui; 
	
	// Reference to the sim world clock
	private SimWorldClock clock;
	
	// Sets world clock
	public void setClock(SimWorldClock c) {
		clock = c;
	}
	
	// Sets the sim city gui
	public void setSimCityGUI(SimCityGUI sc) {
		simcitygui = sc;
	}

	private String title = " Scenario Person Panel ";
	private static final int WIDTH = 275;
	private static final int BUTTONWIDTH = 170;
	private static final int HEIGHT = 310;
	private final int f_width = WIDTH;
	private final int bf_width = BUTTONWIDTH;
	private final int f_height = 200;
	private Dimension size = new Dimension(WIDTH, HEIGHT);
	
	// Main control panel reference
	private CityControlPanel cntrlPanel;
	
	// Formatting panels
	private JPanel searchPanel = new JPanel();
	private JPanel runPanel = new JPanel(); // To contain the run button
	
	// To keep track of what scenario button was pressed
	private JButton chosen;
	
	// COMPONENT DECLARATIONS
	// List of scenario buttons 
	private List<JButton> scenarios = Collections.synchronizedList(new ArrayList<JButton>());
	
	private JButton restaurantsOrderFromMarket = new JButton("Restaurants ordering from Market");
	private JButton robBank = new JButton("Robber robs Bank");
	private JButton carAccident = new JButton("Vehicle collision");
	private JButton personAccident = new JButton("Person Vehicle collision");
	
	// Run scenario button
	private JButton run = new JButton("Run");
	
	// Scroll pane for the scenarios
	private JScrollPane scenariosToRun;
	
	public ScenarioPanel(CityControlPanel cp) {
		run.setEnabled(false);
		
		cntrlPanel = cp;
		scenariosToRun = new JScrollPane(searchPanel);
		
		// PANEL SETUP
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createTitledBorder(title));
		this.setBackground(Color.GRAY);
		
		// Panel size initiations
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		
		// ACTION LISTENERS FOR BUTTONS
		restaurantsOrderFromMarket.addActionListener(this);
		robBank.addActionListener(this);
		carAccident.addActionListener(this);
		personAccident.addActionListener(this);
		run.addActionListener(this);

		// COMPONENT INITIALIZATIONS
		// Formatting panel setup
		Dimension fpSize = new Dimension(bf_width, f_height);
		
		searchPanel.setLayout(new GridLayout(4, 1));
		searchPanel.setBackground(Color.GRAY);
		searchPanel.setPreferredSize(fpSize);
		searchPanel.setMaximumSize(fpSize);
		
		// Formatting scroll pane
		Dimension scSize = new Dimension(f_width, f_height);
	
		scenariosToRun.setBackground(Color.GRAY);
		scenariosToRun.setPreferredSize(scSize);
		scenariosToRun.setMaximumSize(scSize);
		
		// Formatting run panel
		Dimension runSize = new Dimension(f_width, 40);
		
		runPanel.setLayout(new GridLayout(1, 1));
		runPanel.setBackground(Color.GRAY);
		runPanel.setPreferredSize(runSize);
		runPanel.setMaximumSize(runSize);
		
		// Adds run button to run panel
		runPanel.add(run);
		
		// Add buttons to the scenarios list
		scenarios.add(restaurantsOrderFromMarket);
		scenarios.add(robBank);
		scenarios.add(carAccident);
		scenarios.add(personAccident);
		
		for (JButton b : scenarios) {
			searchPanel.add(b);
		}
		
		add(scenariosToRun);
		add(runPanel);
	}

	public void actionPerformed(ActionEvent e) {
//		if (e.getSource() == run) {
//			for (PersonAgent p : simcitygui.people) {
//				if (p.getName().equals(nameText.getText().trim())) {
//					selectedPerson = p;
//					name.setText("  Person: " + selectedPerson.getName());
//					currentRole.setText(selectedPerson.getActiveRoleName());
//					event.setEnabled(true);
//				}
//			}
//		}
		if (e.getSource() == run) {
			if (chosen.getText().trim().equals("Restaurants ordering from Market")) {
				// Here we will run the scenario where all restaurants order from the market
				//FIX
				System.err.println("Restaurants order from market");
			}
			else if (chosen.getText().trim().equals("Robber robs Bank")) {
				// Here we will run the scenario where all restaurants order from the market
				//FIX
				System.err.println("Robber robs bank");
			}
			else if (chosen.getText().trim().equals("Vehicle collision")) {
				// Here we will run the scenario where all restaurants order from the market
				//FIX
				System.err.println("Vehicle collision");
			}
			else if (chosen.getText().trim().equals("Person Vehicle collision")) {
				// Here we will run the scenario where all restaurants order from the market
				//FIX
				System.err.println("Person Vehicle collision");
			}
			run.setEnabled(false);
		}
		
		for (JButton sc : scenarios) {
			if (e.getSource() == sc) {
				run.setEnabled(true);
				chosen = sc;
			}
		}
		
	}
	
	
	
}
