package resident.gui;
import javax.swing.*;  

import person.interfaces.Person;
import resident.ApartmentTenantRole;
import resident.ApartmentTenantRole.MyFood;
import resident.HomeOwnerRole;
import resident.interfaces.HomeOwner;

import java.awt.*;
import java.awt.event.*;

public class SimCityGui extends JFrame{

public SimCityGui() {
	int WINDOWX = 640;
	int WINDOWY = 480;
    setBounds(50, 50, WINDOWX, WINDOWY);
//	HouseAnimationPanel houseAnimationPanel = new HouseAnimationPanel();
//	add(houseAnimationPanel);
	
	ApartmentAnimationPanel aptAnimationPanel = new ApartmentAnimationPanel();
	add(aptAnimationPanel);
	
    Person person = null;
    
//    HomeOwnerRole homeOwner = new HomeOwnerRole("Jennie", 1, person); 
//    HomeOwnerGui homeOwnerGui = new HomeOwnerGui(homeOwner, this);  
//	homeOwner.myFridge.add(new MyFood("Chicken", 5));
//    homeOwner.setGui(homeOwnerGui);
//   // homeOwner.setMaintenance(housekeeper);
//    homeOwner.startThread();
//    homeOwner.updateVitals(4, 5);
//    homeOwner.msgMaintainHome();
//    homeOwner.setMoney(50);
    
    ApartmentTenantRole aptTenant = new ApartmentTenantRole("Jennie", 1, person); 
    ApartmentTenantGui aptGui = new ApartmentTenantGui(aptTenant, this);  
    aptTenant.myFridge.add(new MyFood("Chicken", 5));
    aptTenant.setGui(aptGui);
    aptTenant.startThread();
    aptTenant.updateVitals(4, 5);
   
//    houseAnimationPanel.addGui(homeOwnerGui);
    aptAnimationPanel.addGui(aptGui);
	}
    
	public static void main(String[] args) {
        SimCityGui gui = new SimCityGui();
        gui.setTitle("SIMCITY");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


}
