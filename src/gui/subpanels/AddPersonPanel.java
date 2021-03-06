package gui.subpanels;

import gui.panels.*;
import gui.main.SimCityGUI; 

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import market.MarketCashierRole;
import market.MarketEmployeeRole;
import bank.BankHostRole;
import bank.BankTellerRole;
import person.*; 
import person.PersonAgent.HomeType;
import restaurant6.Restaurant6SDWaiterRole;
import restaurant6.Restaurant6WaiterRole;
import simcity.astar.AStarTraversal;

/**
 * This panel allows the user to add a person
 * to Sim City 201
 * Person can be customized ( framework to create a person has been implemented,
 * code to actually create the person needs to be added)
 * Random person can be created (To be implemented)
 * 
 */

public class AddPersonPanel extends JPanel implements ActionListener{
	private static final long serialVersionUID = 4;
	private SimCityGUI simcitygui = null; 
	private String title = " Add Person Panel ";
	private static final int WIDTH = 275;
	private static final int HEIGHT = 310;
	private Dimension size = new Dimension(WIDTH, HEIGHT);
	private int residentNum = 22;
	private Random generator = new Random();
	
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
		roles.add(new JCheckBox("SD Waiter"));
		roles.add(new JCheckBox("Regular Waiter"));
		roles.add(new JCheckBox("Market Cashier"));
		roles.add(new JCheckBox("Market Employee"));
		
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
			addRandomPerson();
		}
	}
	
	public void setSimCityGUI(SimCityGUI scg){
		simcitygui = scg; 
	}
	
	public void addRandomPerson() {
		AStarTraversal aStarTraversal = new AStarTraversal(simcitygui.grid);
		PersonAgent pa = new PersonAgent(nameText.getText(), simcitygui.citymap, aStarTraversal, 2000);
		pa.homeNumber = ++residentNum;
		pa.homeType = HomeType.Apartment;
		simcitygui.addPerson(pa);
		
		// Gives working role to person
		int role = generator.nextInt(4);
		if (role == 0) {
			pa.addRole(new BankHostRole(pa, pa.getName()), "Bank Host");
		}
		else if (role == 1) {
			pa.addRole(new BankTellerRole(pa, pa.getName()), "Bank Teller");
		}
		else if (role == 2) {
			pa.addRole(new MarketCashierRole(pa, pa.getName()), "Market Cashier");
		}
		else if (role == 3) {
			pa.addRole(new MarketEmployeeRole(pa, pa.getName()), "Market Employee");
		}
		else {
			pa.addRole(new Restaurant6SDWaiterRole(pa.getName(), pa), "Rest 6 SDWaiter");
		}
		
		pa.startThread();
	}
	
	public void addCustomPerson(){
		// ArrayList<Role> selectedRoles
		PersonAgent pa = new PersonAgent(nameText.getText(), simcitygui.citymap, 900);
		pa.homeNumber = ++residentNum;
		pa.homeType = HomeType.Apartment;
		simcitygui.addPerson(pa);
		pa.startThread();
		
		for(JCheckBox role : roles){
			if(role.isSelected()){
				if(role.getText().equals("Bank Host")){
					//pa.addRole(simcitygui.bankhostrole);
					//SimEvent hostGoToBank = new SimEvent("Go to work", simcitygui.citymap.bank, )
					//pa.addRole(new BankHostRole(pa, pa.getName()), "Bank Host");
				}
				if(role.getText().equals("Bank Teller")){
					// selectedRoles.add(Role 2)
					pa.addRole(new BankTellerRole(pa, pa.getName()), "Bank Teller");
				}
				if(role.getText().equals("Market Cashier")){
					// selectedRoles.add(Role 4)
					pa.addRole(new MarketCashierRole(pa, pa.getName()), "Market Cashier");
				}
				if(role.getText().equals("Market Employee")){
					//pa.addRole(simcitygui.marketemployeerole);
					// selectedRoles.add(Role 6)
					pa.addRole(new MarketEmployeeRole(pa, pa.getName()), "Market Employee");
				}
				if(role.getText().equals("SD Waiter")){
					// selectedRoles.add(Role 7)
					pa.addRole(new Restaurant6SDWaiterRole(pa.getName(), pa), "Rest 6 SDWaiter");
				}
				if(role.getText().equals("Regular Waiter")){
					// selectedRoles.add(Role 7)
					pa.addRole(new Restaurant6WaiterRole(pa.getName(), pa), "Rest 6 Waiter");
				}
			}
		}
	}
}
