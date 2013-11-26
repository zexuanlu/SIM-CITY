package simcity.gui;
import javax.swing.*;
import java.awt.*;
import simcity.BusRole;
import simcity.BusStopAgent;
import simcity.PassengerRole;
import simcity.CarAgent;
import simcity.CityMap;
import simcity.astar.*;
import java.util.concurrent.*;

public class SimCityGui extends JFrame{
        
         int WINDOWX = 640; //60 across
     int WINDOWY = 480; //60 across
     int scale = 20;
          int gridX = WINDOWX/scale;
          int gridY = WINDOWY/scale;
        
        public CityMap citymap = new CityMap();
        
        Semaphore[][] grid = new Semaphore[gridX][gridY];
        //intialize the semaphore grid

    
        BusRole bus = new BusRole("BusRole");
        BusRole bus2 = new BusRole("BusRole2");
        BusGui bgui2;
        BusGui bgui;
        
        /**PassengerRole passenger = new PassengerRole("PassengerRole");
        PassengerGui pgui = new PassengerGui(passenger,1,1);
        
        PassengerRole passenger2 = new PassengerRole("Passenger2Role");
        PassengerGui pgui2 = new PassengerGui(passenger2,100,300);
        
        PassengerRole passenger3 = new PassengerRole("Passenger3Role");
        PassengerGui pgui3 = new PassengerGui(passenger3,130,400);*/
        
        BusStopAgent busstop1 = new BusStopAgent("Stop1");
        BusStopAgent busstop2 = new BusStopAgent("Stop2");
        BusStopAgent busstop3 = new BusStopAgent("Stop3");
        BusStopAgent busstop4 = new BusStopAgent("Stop4");
        BusStopAgent busstop5 = new BusStopAgent("Stop5");
        BusStopAgent busstop6 = new BusStopAgent("Stop6");
        BusStopAgent busstop7 = new BusStopAgent("Stop7");
        BusStopAgent busstop8 = new BusStopAgent("Stop8");
        
        BusStopGui bs1gui = new BusStopGui(busstop1,80,200);        
        BusStopGui bs2gui = new BusStopGui(busstop2, 420, 200);
        BusStopGui bs3gui = new BusStopGui(busstop3, 500, 280);
        BusStopGui bs4gui = new BusStopGui(busstop4,180,280);
        BusStopGui bs5gui = new BusStopGui(busstop5, 260,60);
        BusStopGui bs6gui = new BusStopGui(busstop6, 340,140);
        BusStopGui bs7gui = new BusStopGui(busstop7, 340, 420);
        BusStopGui bs8gui = new BusStopGui(busstop8,260,340);
        
        public SimCityGui(){        

                for (int i=0; i<gridX ; i++)
                 for (int j = 0; j<gridY; j++)
                        grid[i][j]=new Semaphore(1,true);
                //build the animation areas
                try {
                 //make the 0-th row and column unavailable
        //         for (int i=0; i<gridY+1; i++) grid[0][0+i].acquire();
                // for (int i=1; i<gridX+1; i++) grid[0+i][0].acquire();
                
                 //to create dead position
                 // for (int i=0;i<20;i++){
                 //         grid[1][1].release();
                 //         System.out.println(grid[2][4].availablePermits());
                 // }
                        
                        for (int y=0;y<30;y++){
                                grid[30][20].release();
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

         setBounds(0, 0, WINDOWX, WINDOWY);
         AnimationPanel animationPanel = new AnimationPanel();
         add (animationPanel);
        
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
                 
                 /** passenger.setGui(pgui);                 
                  passenger2.setGui(pgui2);
                  passenger3.setGui(pgui3);*/
        
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
        
        // citymap.addDestination("Home", new Dimension(550, 100));
        // citymap.addDestination("Food", new Dimension(200, 400));

         animationPanel.addGui(bgui);
         animationPanel.addGui(bgui2);
         animationPanel.addGui(bs1gui);
         animationPanel.addGui(bs2gui);
         animationPanel.addGui(bs3gui);
         animationPanel.addGui(bs4gui);
         animationPanel.addGui(bs5gui);
         animationPanel.addGui(bs6gui);
         animationPanel.addGui(bs7gui);
         animationPanel.addGui(bs8gui);
       /**  animationPanel.addGui(pgui);
         animationPanel.addGui(pgui2);
         animationPanel.addGui(pgui3);*/
        
         busstop1.startThread();
         busstop2.startThread();
         busstop3.startThread();
         busstop4.startThread();
         busstop5.startThread();
         busstop6.startThread();
         busstop7.startThread();
         busstop8.startThread();
    //     passenger.startThread();
     //    passenger2.startThread();
       //  passenger3.startThread();
        
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
        
        /** passenger.setCityMap(citymap);
                  passenger.setPassDestination("Home"); //change to the int of the thing
                  passenger.gotoBus();
                  passenger2.setCityMap(citymap);
                  passenger2.setPassDestination("Home");
                  passenger2.gotoBus();
                  passenger3.setCityMap(citymap);
                  passenger3.setPassDestination("Food");
                  passenger3.gotoBus();
                 */
                 /**
                 aStarTraversal = new AStarTraversal(grid);
                
                 CarAgent caragent = new CarAgent(aStarTraversal);
                 CarGui cgui = new CarGui(caragent,50,250);
                 caragent.setGui(cgui);
                 animationPanel.addGui(cgui);
                 caragent.startThread();
                 caragent.gotoPosition(500,250);
                
                 aStarTraversal = new AStarTraversal(grid);
                 CarAgent caragent2 = new CarAgent(aStarTraversal);
                 CarGui cgui2 = new CarGui(caragent2,500,250);
                 caragent2.setGui(cgui2);
                 animationPanel.addGui(cgui2);
                 caragent2.startThread();
                 caragent2.gotoPosition(90,250);
                
                 aStarTraversal = new AStarTraversal(grid);
                 CarAgent caragent3 = new CarAgent(aStarTraversal);
                 CarGui cgui3 = new CarGui(caragent3,400,240);
                 caragent3.setGui(cgui3);
                 animationPanel.addGui(cgui3);
                 caragent3.startThread();
                 caragent3.gotoPosition(300,300);
                */
                 aStarTraversal = new AStarTraversal(grid);
                 CarAgent caragent4 = new CarAgent(aStarTraversal);
                 CarGui cgui4 = new CarGui(caragent4,297,410);
                 caragent4.setGui(cgui4);
                 animationPanel.addGui(cgui4);
                 caragent4.startThread();
                 caragent4.setatPosition(297, 410);
                 caragent4.gotoPosition(300,50);


                
        }
    
        public static void main(String[] args) {
        SimCityGui gui = new SimCityGui();
        gui.setTitle("SIMCITY");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}