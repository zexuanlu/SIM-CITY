package gui.subpanels;

import gui.panels.*;
import gui.main.SimCityGUI; 

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import person.*; 
import person.gui.PersonGui;

/**
 * This panel allows the user to add a person
 * to Sim City 201
 * Person can be customized ( framework to create a person has been implemented,
 * code to actually create the person needs to be added)
 * Random person can be created (To be implemented)
 * 
 */

public class AddPersonPanel extends JPanel implements ActionListener{
	private SimCityGUI simcitygui = null; 
	private String title = " Add Person Panel ";
	private static final int WIDTH = 275;
	private static final int HEIGHT = 310;
	private Dimension size = new Dimension(WIDTH, HEIGHT);
	
	List<String> strRoles = new ArrayList<String>();	
	// Main control panel reference
	CityControlPanel cntrlPanel;
	
	// Layout
	GridLayout grid;
	
	// TextField for name
	JLabel name = new JLabel("  Name: ");
	JTextField nameText = new JTextField(10);
	
	// Checkbox list to select which role/roles
	List<JCheckBox> roles = new ArrayList<JCheckBox>(); 
	
	// Buttons
	JButton custom = new JButton("Custom Person");
	JButton random = new JButton("Random Person");
	
	public AddPersonPanel(CityControlPanel cp) {
		cntrlPanel = cp;
		
		// PANEL SETUP
		this.setLayout(new GridLayout(8, 2));
		this.setBorder(BorderFactory.createTitledBorder(title));
		this.setBackground(Color.GRAY);
		
		// Panel size initiations
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		
		// ROLES TO ADD
		roles.add(new JCheckBox("Bank Host"));
		roles.add(new JCheckBox("Bank Teller"));
		roles.add(new JCheckBox("Market Cashier"));
		roles.add(new JCheckBox("Market Employee"));
		roles.add(new JCheckBox("Role 5"));
		roles.add(new JCheckBox("Role 6"));
		roles.add(new JCheckBox("Role 7"));
		roles.add(new JCheckBox("Role 8"));
		roles.add(new JCheckBox("Role 9"));
		roles.add(new JCheckBox("Role 10"));
		
		// ADD COMPONENTS
		// Name info
		this.add(name);
		this.add(nameText);
		
		// CheckBoxes
		for(JCheckBox role : roles){
			this.add(role);
		}
		
		// Buttons
		this.add(custom);
		custom.addActionListener(this);
		
		this.add(random);
		random.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == custom){
			addCustomPerson();
		}
		else if (e.getSource()==random){
			System.out.println("Random button clicked");
		}
	}
	
	public void setSimCityGUI(SimCityGUI scg){
		simcitygui = scg; 
	}
	
	public void addCustomPerson(){
		// ArrayList<Role> selectedRoles
		System.out.println("Custom button clicked");
		PersonAgent pa = new PersonAgent(nameText.getText(),simcitygui.citymap);
		PersonGui pgui = new PersonGui(pa);
		pa.gui = pgui; 
		simcitygui.cityAnimPanel.addGui(pgui);
		pa.startThread();
		
		
		for(JCheckBox role : roles){
			if(role.isSelected()){
				if(role.getText().equals("Bank Host")){
					pa.addRole(simcitygui.bankhostrole);
				}
				if(role.getText().equals("Bank Teller")){
					// selectedRoles.add(Role 2)
				}
				if(role.getText().equals("Market Cashier")){
					// selectedRoles.add(Role 4)
				}
				if(role.getText().equals("Market Employee")){
					// selectedRoles.add(Role 6)
				}
				if(role.getText().equals("Role 7")){
					// selectedRoles.add(Role 7)
				}
				if(role.getText().equals("Role 8")){
					// selectedRoles.add(Role 8)
				}
				if(role.getText().equals("Role 9")){
					// selectedRoles.add(Role 9)
				}
				if(role.getText().equals("Role 10")){
					// selectedRoles.add(Role 10)
				}
			}
		}
	}
}
