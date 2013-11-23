package gui.main;

import gui.panels.*;
import javax.swing.*;
import java.awt.*;

/**
 * Main Sim City 201 GUI Frame
 * This is where the 'main' function should be
 */

public class SimCityGUI extends JFrame {
	
	private String title = " SIM CITY 201 ";
	public static final int SCG_WIDTH = CityAnimationPanel.WIDTH + BuildingAnimationPanel.WIDTH;
	public static final int SCG_HEIGHT = CityAnimationPanel.HEIGHT + CityControlPanel.HEIGHT;

	CityAnimationPanel cityAnimPanel = new CityAnimationPanel();
	BuildingAnimationPanel bldngAnimPanel = new BuildingAnimationPanel();
	CityControlPanel cityCtrlPanel = new CityControlPanel();
	
	
	public SimCityGUI() {
		// SETUP
		this.setTitle(title);
		this.setSize(SCG_WIDTH, SCG_HEIGHT);
		this.setLayout(new BorderLayout());
		this.setResizable(false);
		
		// ADD COMPONENTS
		this.add(cityAnimPanel, BorderLayout.WEST);
		this.add(bldngAnimPanel, BorderLayout.EAST);
		this.add(cityCtrlPanel, BorderLayout.SOUTH);
	}
	
	public static void main(String[] args){
		SimCityGUI scg = new SimCityGUI();
		scg.setVisible(true);
		scg.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
