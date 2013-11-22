package gui;
import javax.swing.*;

import market.CashierAgent;
import market.CustomerAgent;
import market.EmployeeAgent;

import java.awt.*;
import java.awt.event.*;

public class SimCityGui extends JFrame{

	
	public SimCityGui(){
		 int WINDOWX = 400;
	     int WINDOWY = 600;
	     setBounds(50, 50, WINDOWX, WINDOWY);
	 	AnimationPanel animationPanel = new AnimationPanel();
	 	add (animationPanel);
	 	
	 	////////
/*	 	CashierAgent cashier = new CashierAgent();
        CustomerAgent customer = new CustomerAgent();
        EmployeeAgent employee = new EmployeeAgent();
        CustomerGui customerGui = new CustomerGui();
        EmployeeGui employeeGui = new EmployeeGui();
        cashier.startThread();
        employee.startThread();
        customer.startThread();
        animationPanel.addGui(customerGui);
        animationPanel.addGui(employeeGui);
        customerGui.setAgent(customer);
        employeeGui.setAgent(employee);
        cashier.addEmployee(employee);
        employee.setCashier(cashier);
        customer.setCashier(cashier);
        customer.setGui(customerGui);
        employee.setGui(employeeGui);
        customer.addFood("Steak", 2);
        customer.addFood("Car", 1);
        customer.msgHello();       */
	}
    
	public static void main(String[] args) {
        SimCityGui gui = new SimCityGui();
        gui.setTitle("SIMCITY");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }


}
