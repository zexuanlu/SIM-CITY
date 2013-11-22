package simcity.gui;
import javax.swing.*;
import java.awt.*;
import simcity.BusRole; 
import simcity.BusStopAgent; 
import simcity.PassengerRole; 
import simcity.CityMap; 
import simcity.astar.*; 
import java.util.concurrent.*; 
import java.awt.event.*; 

public class SimCityGui extends JFrame{
	
	static int gridX = 30; 
	static int gridY = 30; 
	
	CityMap citymap = new CityMap();
	
	Semaphore[][] grid = new Semaphore[gridX + 1][gridY + 1];
	//intialize the semaphore grid

    AStarTraversal aStarTraversal = new AStarTraversal(grid);
    
	BusRole bus = new BusRole("BusRole");
	BusRole bus2 = new BusRole("BusRole2");
	BusGui bgui2; 
	BusGui bgui;
	
	PassengerRole passenger = new PassengerRole("PassengerRole");
	PassengerGui pgui = new PassengerGui(passenger,0,0);
	
	PassengerRole passenger2 = new PassengerRole("Passenger2Role");
	PassengerGui pgui2 = new PassengerGui(passenger2,100,300);
	
	PassengerRole passenger3 = new PassengerRole("Passenger3Role");
	PassengerGui pgui3 = new PassengerGui(passenger3,105,300);
	
	BusStopAgent busstop1 = new BusStopAgent("Stop1");
	BusStopGui bsgui = new BusStopGui(busstop1,160,110);
	
	BusStopAgent busstop2 = new BusStopAgent("Stop2");
	BusStopGui bs2gui = new BusStopGui(busstop2, 420, 110);
	
	BusStopAgent busstop3 = new BusStopAgent("Stop3");
	BusStopGui bs3gui = new BusStopGui(busstop3, 470, 300);
	
	BusStopAgent busstop4 = new BusStopAgent("Stop4");
	BusStopGui bs4gui = new BusStopGui(busstop4,100,500);
	
	public SimCityGui(){	

		for (int i=0; i<gridX+1 ; i++)
		    for (int j = 0; j<gridY+1; j++)
			grid[i][j]=new Semaphore(1,true);
		//build the animation areas
		try {
		    //make the 0-th row and column unavailable
		    System.out.println("making row 0 and col 0 unavailable.");
		    for (int i=0; i<gridY+1; i++) grid[0][0+i].acquire();
		    for (int i=1; i<gridX+1; i++) grid[0+i][0].acquire();
		    
		    for (int x=8;x<gridX+1;x++){
		    	for (int y=1;y<5;y++) grid[x][y].acquire();
		    }
		    for (int y = 5; y<9; y++){
		    	for (int x=27;x<gridX+1;x++) grid[x][y].acquire();
		    }
		    
		    for (int y = 9;y<25;y++){//rows 9-24
		    	for (int x=1; x<5;x++) grid[x][y].acquire(); 
		    	for (int x=7; x<23;x++) grid[x][y].acquire(); 
		    	for (int x=27;x<gridX+1;x++)  grid[x][y].acquire(); 
		    }	    
		    
		}catch (Exception e) {
		    System.out.println("Unexpected exception caught in during setup:"+ e);
		}
		
		bgui = new BusGui(bus,aStarTraversal);
		bgui2 = new BusGui(bus,aStarTraversal);
		
		
		 int WINDOWX = 600; //60 across
	     int WINDOWY = 600; //60 across
	     setBounds(50, 50, WINDOWX+50, WINDOWY+50);
	     AnimationPanel animationPanel = new AnimationPanel();
	     add (animationPanel);
	     
	 	 bus.setGui(bgui);
	 	 bus2.setGui(bgui2);
	 	 busstop1.setGui(bsgui);
	 	 busstop2.setGui(bs2gui);
	 	 busstop3.setGui(bs3gui);
	 	 busstop4.setGui(bs4gui);
	 	 passenger.setGui(pgui);	 	 
	 	 passenger2.setGui(pgui2);
	 	 passenger3.setGui(pgui3);
	     
	     citymap.addBusStop(busstop1.name, busstop1);
	     citymap.addBusStopDim(busstop1,busstop1.getDim()); //add in the location of busstop
	     citymap.addBusStop(busstop2.name, busstop2);
	     citymap.addBusStopDim(busstop2, busstop2.getDim()); 
	     citymap.addBusStop(busstop3.name, busstop3);
	     citymap.addBusStopDim(busstop3,busstop3.getDim());
	     citymap.addBusStop(busstop4.name, busstop4);
	     citymap.addBusStopDim(busstop4, busstop4.getDim());
	     
	     //all these are along Bus 1's route so you have to add it to citymap
	     citymap.addBus(busstop1, bus);
	     citymap.addBus(busstop2, bus);
	     citymap.addBus(busstop3, bus);
	     citymap.addBus(busstop4, bus);
	     
	     citymap.addDestination("Home", new Dimension(550, 100));
	     citymap.addDestination("Food", new Dimension(200, 400));

	     animationPanel.addGui(bgui);
	     animationPanel.addGui(bsgui);
	     animationPanel.addGui(bs2gui);
	     animationPanel.addGui(bs3gui);
	     animationPanel.addGui(bs4gui);
	     animationPanel.addGui(pgui);
	     animationPanel.addGui(pgui2);
	     animationPanel.addGui(pgui3);
	     
	     busstop1.startThread();
	     busstop2.startThread();
	     busstop3.startThread();
	     busstop4.startThread();
	     passenger.startThread();
	     passenger2.startThread();
	     passenger3.startThread();
	     
	     
	     bus.setBusMap(citymap);
	     bus.startThread();
	     bus.addtoRoute(busstop1.name);
	     bus.addtoRoute(busstop2.name);
	     bus.addtoRoute(busstop3.name);
	     bus.addtoRoute(busstop4.name);
	     bus.msgStartBus();
	     
	     passenger.setCityMap(citymap);
	 	 passenger.setPassDestination("Home");
	 	 passenger.gotoBus();
	 	 passenger2.setCityMap(citymap);
	 	 passenger2.setPassDestination("Home");
	 	 passenger2.gotoBus();
	 	 passenger3.setCityMap(citymap);
	 	 passenger3.setPassDestination("Food");
	 	 passenger3.gotoBus();

	}
    
	public static void main(String[] args) {
        SimCityGui gui = new SimCityGui();
        gui.setTitle("SIMCITY");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


}
