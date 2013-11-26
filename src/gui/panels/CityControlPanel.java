package gui.panels;
/**
 * This is the main control panel for SimCity 201
 */

import java.util.*;
import java.awt.*;
import java.util.List;

import javax.swing.*;
import gui.main.*;

import gui.subpanels.*;

public class CityControlPanel extends JPanel {
	
	private String title = " CITY CONTROL PANEL ";
	public static final int WIDTH = CityAnimationPanel.WIDTH + BuildingAnimationPanel.WIDTH;
	public static final int HEIGHT = 310;
	public SimCityGUI simcitygui; 
	
	// SUB PANEL REFERENCES
	AddPersonPanel addPPanel = new AddPersonPanel(this);
	InteractPersonPanel interactPanel = new InteractPersonPanel(this);
	TracePanel tracePanel = new TracePanel(this);

	public CityControlPanel(SimCityGUI c) {
		// PANEL SETUP
		simcitygui = c; 
		this.setBorder(BorderFactory.createTitledBorder(title));
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		this.setBackground(Color.GRAY);
		
		// Panel size initiations
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		// ADD COMPONENTS
		this.add(addPPanel);
		this.add(interactPanel);
		this.add(tracePanel);
		
		addPPanel.setSimCityGUI(simcitygui);
	}
}