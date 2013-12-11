package gui.panels;
/**
 * This is the main control panel for SimCity 201
 */

import java.util.*;
import java.awt.*;
import java.util.List;

import javax.swing.*;

import person.PersonAgent;
import gui.main.*;
import gui.subpanels.*;

public class CityControlPanel extends JPanel {
	
	private String title = " CITY CONTROL PANEL ";
	public static final int WIDTH = CityAnimationPanel.WIDTH + BuildingAnimationPanel.WIDTH;
	public static final int HEIGHT = 310;
	public SimCityGUI simcitygui; 
	
	// SUB PANEL REFERENCES
	public AddPersonPanel addPPanel = new AddPersonPanel(this);
	public InteractPersonPanel interactPanel = new InteractPersonPanel(this);
	public TracePanel tracePanel = new TracePanel(this);
	public ScenarioPanel scenarioPanel = new ScenarioPanel(this);

	public CityControlPanel(SimCityGUI c) {
		// PANEL SETUP
		simcitygui = c; 
		simcitygui.cityAnimPanel.tracePanel = tracePanel;
		this.setBorder(BorderFactory.createTitledBorder(title));
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		this.setBackground(Color.GRAY);
		scenarioPanel.tracePanel = tracePanel;
		simcitygui.tracePanel = tracePanel;
		PersonAgent.setTracePanel(tracePanel);
		
		// Panel size initiations
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		// ADD COMPONENTS
		this.add(addPPanel);
		this.add(interactPanel);
		this.add(tracePanel);
		this.add(scenarioPanel);
		
		addPPanel.setSimCityGUI(simcitygui);
		interactPanel.setSimCityGUI(simcitygui);
		scenarioPanel.setSimCityGUI(simcitygui);
	}
}
