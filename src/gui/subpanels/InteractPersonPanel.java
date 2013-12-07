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

public class InteractPersonPanel extends JPanel implements ActionListener{
	
	private CityControlPanel citycontrolpanel; 
	private SimCityGUI simcitygui; 
	
	// Reference to the sim world clock
	private SimWorldClock clock;
	
	// Sets world clock
	public void setClock(SimWorldClock c) {
		clock = c;
	}
	
	private PersonAgent selectedPerson;
	
	// Sets the sim city gui
	public void setSimCityGUI(SimCityGUI sc) {
		simcitygui = sc;
	}

	private String title = " Interact Person Panel ";
	private static final int WIDTH = 275;
	private static final int HEIGHT = 310;
	private final int f_width = WIDTH;
	private final int f_height = 40;
	private Dimension size = new Dimension(WIDTH, HEIGHT);
	private Timer refresh;
	
	// Main control panel reference
	CityControlPanel cntrlPanel;
	
	// Formatting panels
	JPanel searchPanel = new JPanel();
	JPanel displayInfoPanel = new JPanel();
	JPanel chooseEventPanel = new JPanel();
	
	// COMPONENT DECLARATIONS
	// Labels
	JLabel name;
	JLabel currentRole;
	
	// ComboBoxes
	JComboBox<String> event;
	JComboBox<String> person;
	JComboBox<Integer> startTime;
	
	
	// TextField for name
	JLabel personName;
	JTextField nameText;
	JButton search = new JButton("Search");
	
	
	public InteractPersonPanel(CityControlPanel cp) {
		cntrlPanel = cp;
		refresh = new Timer(2000, this);
		
		// PANEL SETUP
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createTitledBorder(title));
		this.setBackground(Color.GRAY);
		
		// Panel size initiations
		this.setPreferredSize(size);
		this.setMaximumSize(size);

		// COMPONENT INITIALIZATIONS
		// Formatting Panels setup
		Dimension fpSize = new Dimension(f_width, f_height);
		
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
		searchPanel.setBackground(Color.GRAY);
		searchPanel.setPreferredSize(fpSize);
		searchPanel.setMaximumSize(fpSize);
		
		displayInfoPanel.setLayout(new GridLayout(1, 2));
		displayInfoPanel.setBackground(Color.GRAY);
		displayInfoPanel.setPreferredSize(fpSize);
		displayInfoPanel.setMaximumSize(fpSize);
		
		chooseEventPanel.setLayout(new GridLayout(1, 2));
		chooseEventPanel.setBackground(Color.GRAY);
		chooseEventPanel.setPreferredSize(fpSize);
		chooseEventPanel.setMaximumSize(fpSize);
		
		// Labels
		personName  = new JLabel("  Name: ");
		name = new JLabel("  Person: ");
		//name.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		currentRole = new JLabel("Role");
		
		// TextFields
		Dimension fieldSize = new Dimension(150, 32);
		nameText = new JTextField(10);
		nameText.setPreferredSize(fieldSize);
		nameText.setMaximumSize(fieldSize);
		
		// ComboBoxes
		event = new JComboBox<String>();
		event.addItem("Go to Bank");
		event.addItem("Go Buy From Market");
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
		
		// ADD COMPONENTS TO FORMATTING PANELS
		// Search Panel
		searchPanel.add(personName);
		searchPanel.add(nameText);
		searchPanel.add(search);
		
		// Display Info Panel
		displayInfoPanel.add(name);
		displayInfoPanel.add(currentRole);
		
		// Choose Event Panel
		chooseEventPanel.add(new JLabel("  Choose event: "));
		chooseEventPanel.add(event);
		
		// ADD COMPONENTS TO THIS
		this.add(searchPanel);
		this.add(displayInfoPanel);
		this.add(chooseEventPanel);
		//this.add(new JLabel("Choose start time: "));
		//this.add(startTime);
		
		refresh.start();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == search) {
			for (PersonAgent p : simcitygui.people) {
				if (p.getName().equals(nameText.getText().trim())) {
					selectedPerson = p;
					name.setText("  Person: " + selectedPerson.getName());
					currentRole.setText(selectedPerson.getActiveRoleName());
					event.setEnabled(true);
				}
			}
		}
		else if(e.getSource() == refresh){
			if(selectedPerson != null){
				name.setText("  Person: " + selectedPerson.getName());
				currentRole.setText(selectedPerson.getActiveRoleName());
			}
			
		}
	}
	
	public void displayInfo(PersonAgent p) {
		selectedPerson = p;
		event.setEnabled(true);				
	}
	
	
	
}
