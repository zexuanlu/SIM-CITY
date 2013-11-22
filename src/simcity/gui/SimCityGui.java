package simcity.gui;
import javax.swing.*;
import java.awt.*;
import simcity.BusRole; 
import simcity.BusStopAgent; 
import simcity.PassengerRole; 
import simcity.CityMap; 

public class SimCityGui extends JFrame{
	CityMap citymap = new CityMap();
	
	BusRole bus = new BusRole("BusRole");
	BusGui bgui = new BusGui(bus);
	
	PassengerRole passenger = new PassengerRole("PassengerRole");
	PassengerGui pgui = new PassengerGui(passenger,0,0);
	
	PassengerRole passenger2 = new PassengerRole("Passenger2Role");
	PassengerGui pgui2 = new PassengerGui(passenger2,100,300);
	
	PassengerRole passenger3 = new PassengerRole("Passenger3Role");
	PassengerGui pgui3 = new PassengerGui(passenger3,105,300);
	
	BusStopAgent busstop1 = new BusStopAgent("Stop1");
	BusStopGui bsgui = new BusStopGui(busstop1,100,80);
	
	BusStopAgent busstop2 = new BusStopAgent("Stop2");
	BusStopGui bs2gui = new BusStopGui(busstop2, 450, 80);
	
	BusStopAgent busstop3 = new BusStopAgent("Stop3");
	BusStopGui bs3gui = new BusStopGui(busstop3, 500, 300);
	
	BusStopAgent busstop4 = new BusStopAgent("Stop4");
	BusStopGui bs4gui = new BusStopGui(busstop4,100,500);
	
	public SimCityGui(){	
		 int WINDOWX = 600;
	     int WINDOWY = 600;
	     setBounds(50, 50, WINDOWX, WINDOWY);
	     AnimationPanel animationPanel = new AnimationPanel();
	     add (animationPanel);
	     
	 	 bus.setGui(bgui);
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
	     citymap.addDestination("Food", new Dimension(150, 400));

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
