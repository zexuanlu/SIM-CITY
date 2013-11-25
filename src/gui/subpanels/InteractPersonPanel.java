package gui.subpanels;

import gui.panels.CityControlPanel;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

/**
 * This panel allows the user to interact with a person
 * in SimCity
 */

public class InteractPersonPanel extends JPanel {

	private String title = " Interact Person Panel ";
	private static final int WIDTH = 275;
	private static final int HEIGHT = 310;
	private Dimension size = new Dimension(WIDTH, HEIGHT);
	
	// Main control panel reference
	CityControlPanel cntrlPanel;
	
	// COMPONENT DECLARATIONS
	// Labels
	JLabel name;
	JLabel currentRole;
	
	// ComboBoxes
	JComboBox<String> event;
	JComboBox<Integer> startTime;
	
	public InteractPersonPanel(CityControlPanel cp) {
		cntrlPanel = cp;
		
		// PANEL SETUP
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createTitledBorder(title));
		this.setBackground(Color.GRAY);
		
		// Panel size initiations
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		
		// COMPONENT INITIALIZATIONS
		// Labels
		name = new JLabel("Person: Name");
		name.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		currentRole = new JLabel("Bank Employee");
		
		// ComboBoxes
		event = new JComboBox<String>();
		event.addItem("Event 1");
		event.addItem("Event 2");
		event.addItem("Event 3");

		startTime = new JComboBox<Integer>();
		startTime.addItem(900);
		startTime.addItem(1000);
		startTime.addItem(1100);
		
		// ADD COMPONENTS
		this.add(name);
		this.add(currentRole);
		this.add(new JLabel("Choose event: "));
		this.add(event);
		this.add(new JLabel("Choose start time: "));
		this.add(startTime);
	}

}
