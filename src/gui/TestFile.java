package gui;

import gui.panels.*;

import javax.swing.*;


import java.awt.*;

import person.Location;

import simcity.BusRole;
import simcity.BusStopAgent;

import simcity.CityMap;
import simcity.astar.*;
import simcity.gui.BusGui;
import simcity.gui.BusStopGui;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
* Main Sim City 201 GUI Frame
* This is where the 'main' function should be
*/

public class TestFile extends JFrame {
	
	public CityMap citymap; 
	public List<Location> locations = new ArrayList<Location>();

	public int WINDOWX = 640; //60 across
	public int WINDOWY = 480; //60 across
	public int scale = 20; 
	public int gridX = WINDOWX/scale; 
	public int gridY = WINDOWY/scale;

	private String title = " SIM CITY 201 ";
	public static final int SCG_WIDTH = CityAnimationPanel.WIDTH + BuildingAnimationPanel.WIDTH;
	public static final int SCG_HEIGHT = CityAnimationPanel.HEIGHT + CityControlPanel.HEIGHT;

	public CityAnimationPanel cityAnimPanel = new CityAnimationPanel();

	///////////////////////////////////////////////////////////INITIALIZATION CODE FOR BUSSES	

	public Semaphore[][] grid = new Semaphore[gridX][gridY];
	public BusRole bus = new BusRole("BusRole");
	public BusRole bus2 = new BusRole("BusRole2");
	public BusGui bgui2; 
	public BusGui bgui;

	public BusStopAgent busstop1 = new BusStopAgent("Stop1");
	public BusStopAgent busstop2 = new BusStopAgent("Stop2");
	public BusStopAgent busstop3 = new BusStopAgent("Stop3");
	public BusStopAgent busstop4 = new BusStopAgent("Stop4");
	public BusStopAgent busstop5 = new BusStopAgent("Stop5");
	public BusStopAgent busstop6 = new BusStopAgent("Stop6");
	public BusStopAgent busstop7 = new BusStopAgent("Stop7");
	public BusStopAgent busstop8 = new BusStopAgent("Stop8");

	public BusStopGui bs1gui = new BusStopGui(busstop1,80,200);	
	public BusStopGui bs2gui = new BusStopGui(busstop2, 420, 200);
	public BusStopGui bs3gui = new BusStopGui(busstop3, 500, 280);
	public BusStopGui bs4gui = new BusStopGui(busstop4,180,280);
	public BusStopGui bs5gui = new BusStopGui(busstop5, 260,60);
	public BusStopGui bs6gui = new BusStopGui(busstop6, 340,140);
	public BusStopGui bs7gui = new BusStopGui(busstop7, 340, 420);
	public BusStopGui bs8gui = new BusStopGui(busstop8,260,340);	

	public TestFile() {
		citymap = new CityMap(locations);

		// SETUP
		this.setTitle(title);
		this.setSize(SCG_WIDTH, SCG_HEIGHT);
		this.setLayout(new BorderLayout());
		this.setResizable(false);

		// ADD COMPONENTS
		this.add(cityAnimPanel, BorderLayout.WEST);


		for (int i=0; i<gridX ; i++)
			for (int j = 0; j<gridY; j++)
				grid[i][j]=new Semaphore(1,true);
		//build the animation areas
		try {
			for (int y=0;y<30;y++){  //Create dead position
				grid[30][20].release();
			}
			for (int z = 0; z<20; z++){ //after creation needs this area to be able to navigate 
				for (int y = 11; y<14; y++){
					grid[0][y].release(); 
				}
			}
			for (int y =0 ;y<50; y++){ //creation location
				grid[0][12].release();
			}
			for (int y =0 ;y<50; y++){ //bank location
				grid[7][12].release();
			}
			
			for (int y = 0; y < 11; y ++){
				for (int x = 0; x < 14; x++){
					grid[x][y].acquire(); 
				}
				for (int x=17; x<gridX; x++){
					grid[x][y].acquire();
				}
			}

			for (int y = 14; y<gridY; y++){
				for (int x = 0; x < 14; x++){
					grid[x][y].acquire(); 
				}
				for (int x=17; x<gridX; x++){
					grid[x][y].acquire();
				}
			}
		}catch (Exception e) {
			System.out.println("Unexpected exception caught in during setup:"+ e);
		}
	

		bgui = new BusGui(bus,0,220);	
		bgui2 = new BusGui(bus2,280,00);
		bus.setGui(bgui);
		AStarTraversal aStarTraversal = new AStarTraversal(grid);
		bus.setAStar(aStarTraversal);

		bus2.setGui(bgui2);
		aStarTraversal = new AStarTraversal(grid);
		bus2.setAStar(aStarTraversal);

		busstop1.setGui(bs1gui);
		busstop2.setGui(bs2gui);
		busstop3.setGui(bs3gui);
		busstop4.setGui(bs4gui);
		busstop5.setGui(bs5gui);
		busstop6.setGui(bs6gui);
		busstop7.setGui(bs7gui);
		busstop8.setGui(bs8gui);

		busstop1.setDirection("down");
		busstop2.setDirection("down");
		busstop3.setDirection("up");
		busstop4.setDirection("up");
		busstop5.setDirection("right");
		busstop6.setDirection("left");
		busstop7.setDirection("left");
		busstop8.setDirection("right");

		citymap.addBusStop(busstop1.name, busstop1);
		citymap.addBusStop(busstop2.name, busstop2);
		citymap.addBusStop(busstop3.name, busstop3);
		citymap.addBusStop(busstop4.name, busstop4);
		citymap.addBusStop(busstop5.name, busstop5);
		citymap.addBusStop(busstop6.name, busstop6);
		citymap.addBusStop(busstop7.name, busstop7);
		citymap.addBusStop(busstop8.name, busstop8);
		
		//all these are along Bus 1's route so you have to add it to citymap
		citymap.addBus(busstop1, bus);
		citymap.addBus(busstop2, bus);
		citymap.addBus(busstop3, bus);
		citymap.addBus(busstop4, bus);
		citymap.addBus(busstop5, bus2);
		citymap.addBus(busstop6, bus2);
		citymap.addBus(busstop7, bus2);
		citymap.addBus(busstop8, bus2);

		cityAnimPanel.addGui(bgui);
		cityAnimPanel.addGui(bgui2);
		cityAnimPanel.addGui(bs1gui);
		cityAnimPanel.addGui(bs2gui);
		cityAnimPanel.addGui(bs3gui);
		cityAnimPanel.addGui(bs4gui);
		cityAnimPanel.addGui(bs5gui);
		cityAnimPanel.addGui(bs6gui);
		cityAnimPanel.addGui(bs7gui);
		cityAnimPanel.addGui(bs8gui);
		
		busstop1.startThread();
		busstop2.startThread();
		busstop3.startThread();
		busstop4.startThread();
		busstop5.startThread();
		busstop6.startThread();
		busstop7.startThread();
		busstop8.startThread();

		bus.setBusMap(citymap);
		bus.addtoRoute(busstop1.name);
		bus.addtoRoute(busstop2.name);
		bus.addtoRoute(busstop3.name);
		bus.addtoRoute(busstop4.name);
		bus.startThread();
		bus.msgStartBus();

		bus2.setBusMap(citymap);
		bus2.addtoRoute(busstop5.name);
		bus2.addtoRoute(busstop8.name);
		bus2.addtoRoute(busstop7.name);
		bus2.addtoRoute(busstop6.name);
		bus2.startThread();
		bus2.msgStartBus();
	}

	public static void main(String[] args){
		TestFile scg = new TestFile();
		scg.setVisible(true);
		scg.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
