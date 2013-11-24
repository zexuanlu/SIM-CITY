package gui;
import javax.swing.*;

import person.interfaces.Person;
import resident.HomeOwnerRole;
import resident.HomeOwnerRole.MyFood;
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
	
    Person person = null;
    HomeOwnerRole homeOwner = new HomeOwnerRole("Jennie", 1, person); 
    HomeOwnerGui homeOwnerGui = new HomeOwnerGui(homeOwner, this);  
	homeOwner.myFridge.add(new MyFood("Chicken", 5));
    homeOwner.setGui(homeOwnerGui);
    homeOwner.startThread();
    homeOwner.updateVitals(4, 5);
    
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
