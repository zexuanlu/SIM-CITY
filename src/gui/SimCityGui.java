package gui;
import javax.swing.*; 

import person.interfaces.Person;
import resident.ApartmentTenantRole;
import resident.HomeOwnerRole.MyFood;
import resident.HomeOwnerRole;
import resident.MaintenancePersonRole;
import resident.interfaces.HomeOwner;

import java.awt.*;
import java.awt.event.*;

public class SimCityGui extends JFrame{

public SimCityGui() {
	int WINDOWX = 480;
	int WINDOWY = 640;
    setBounds(50, 50, WINDOWX, WINDOWY);
	AnimationPanel animationPanel = new AnimationPanel();
	add (animationPanel);
	
    Person person1 = null;
    Person person2 = null;
    
    MaintenancePersonRole housekeeper = new MaintenancePersonRole("Jennie", person1); 
    MaintenanceGui mGui = new MaintenanceGui(housekeeper, this);  
    housekeeper.setGui(mGui);
    housekeeper.startThread();
    housekeeper.setMaintenanceCost(30);
    
    HomeOwnerRole homeOwner = new HomeOwnerRole("Jennie", 1, person2); 
    HomeOwnerGui homeOwnerGui = new HomeOwnerGui(homeOwner, this);  
	homeOwner.myFridge.add(new MyFood("Chicken", 5));
    homeOwner.setGui(homeOwnerGui);
    homeOwner.setMaintenance(housekeeper);
    homeOwner.startThread();
    homeOwner.updateVitals(4, 5);
    homeOwner.msgMaintainHome();
    homeOwner.setMoney(50);
    
    /*ApartmentTenantRole aptTenant = new ApartmentTenantRole("Jennie", 1, person); 
    ApartmentTenantGui aptGui = new ApartmentTenantGui(aptTenant, this);  
    aptTenant.myFridge.add(new MyFood("Chicken", 5));
    aptTenant.setGui(aptGui);
    aptTenant.startThread();
    aptTenant.updateVitals(4, 5);*/
    
    animationPanel.addGui(mGui);
    animationPanel.addGui(homeOwnerGui);
	}
    
	public static void main(String[] args) {
        SimCityGui gui = new SimCityGui();
        gui.setTitle("SIMCITY");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


}
