package gui.subpanels;
import person.Location;
import person.PersonAgent;
import person.Position;
import person.SimEvent;
import person.SimEvent.EventType;
import person.gui.PersonGui; 
import gui.panels.CityControlPanel;
import gui.main.SimCityGUI; 

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

/**
 * This panel allows the user to interact with a person
 * in SimCity
 */

public class InteractPersonPanel extends JPanel implements ActionListener{
	
	private CityControlPanel citycontrolpanel; 
	private SimCityGUI simcitygui; 
	
	private PersonAgent selectedPerson;
	
	// Sets the sim city gui
	public void setSimCityGUI(SimCityGUI sc) {
		simcitygui = sc;
	}

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
	JComboBox<String> person;
	JComboBox<Integer> startTime;
	
	
	// TextField for name
	JLabel personName = new JLabel("Name: ");
	JTextField nameText = new JTextField(10);
	JButton search = new JButton("Search");
	
	
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
		//name.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		currentRole = new JLabel("Role");
		
		// ComboBoxes
		event = new JComboBox<String>();
		event.addItem("Go to Bank");
		event.addItem("Go to Market");
		event.addItem("Go to Restaurant");
		
		event.addActionListener(this);
		event.setEnabled(false);

		// Maybe unnecessary for v1
		/*startTime = new JComboBox<Integer>();
		startTime.addItem(900);
		startTime.addItem(1000);
		startTime.addItem(1100);
		startTime.setEnabled(false);*/
		search.addActionListener(this); 
		
		// ADD COMPONENTS
		this.add(personName);
		this.add(nameText);
		this.add(search);
		this.add(name);
		this.add(currentRole);
		this.add(new JLabel("Choose event: "));
		this.add(event);
		//this.add(new JLabel("Choose start time: "));
		//this.add(startTime);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == search) {
			for (PersonAgent p : simcitygui.people) {
				if (p.getName().equals(nameText.getText().trim())) {
					selectedPerson = p;
					event.setEnabled(true);
				}
			}
		}
		else {
			JComboBox comboBox = (JComboBox)e.getSource();
            Object selected = comboBox.getSelectedItem();
            if (selected.toString().equals("Go to Bank")) {
            	selectedPerson.msgAddEvent(new SimEvent(new Location("Bank", Location.LocationType.Bank, new Position(140, 160)), 1, 7, SimEvent.EventType.CustomerEvent));
            }
            else if (selected.toString().equals("Go Buy From Market")) {
            	selectedPerson.msgAddEvent(new SimEvent(new Location("Market", Location.LocationType.Market, new Position(500, 60)), 1, 7, SimEvent.EventType.CustomerEvent));
            }
            else {
            	selectedPerson.msgAddEvent(new SimEvent(new Location("Restaurant", Location.LocationType.Restaurant, new Position(220, 80)), 1, 7, SimEvent.EventType.CustomerEvent));
            }
        }
	}
	
	public void displayInfo(PersonAgent p) {
		// Labels
		/*name = new JLabel("Person: " + p.getName());
		for (PersonAgent.MyRole r : p.roles) {
			if (r.isActive) {
				currentRole = new JLabel("Role: " + r.role);
			}
		}*/
		selectedPerson = p;
		event.setEnabled(true);				
	}
	
	
	
}
