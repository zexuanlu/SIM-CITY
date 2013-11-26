package market.gui;
import simcity.astar.*; 
import simcity.CarAgent; 
import market.MarketTruckAgent; 

import java.util.*; 
import java.awt.*; 
import java.util.List; 
import java.awt.Graphics2D;

import agent.Gui;

	
public class MarketTruckGui implements Gui {
        
        int scale = 20;
        int deadpositionX = 600;
        int deadpositionY = 400;
        public boolean isPresent;
        
        List<Dimension> moves = new ArrayList<Dimension>();
        boolean NorthSouth;
        boolean EastWest;
        boolean deadpos;
        public int xPos, yPos, xDestination, yDestination;
        public int overallX, overallY;
        
        private MarketTruckAgent myTruck;
        
        public enum GuiState {gotoStop, atStop,canStop};
        GuiState guistate;
        
        public MarketTruckGui(MarketTruckAgent c){
            myTruck = c;
            xPos = deadpositionX;
            yPos = deadpositionY;
            xDestination = deadpositionX;
            yDestination = deadpositionY;
            EastWest = true;
            deadpos = true;
        }
        
        
        public void draw(Graphics2D g) {
                
                if (!deadpos){
                        g.setColor(Color.GREEN);
                        if (EastWest){
                                g.fillRect(xPos, yPos, 20, 20);
                        }
                        else if (NorthSouth){
                                g.fillRect(xPos, yPos, 20, 20);
                        }
                }
    }
        
        public boolean isPresent() {
        return true;
    }
    
        
    public void updatePosition() {
            //check orientation of the bus
               if (xPos == xDestination && yPos == yDestination && guistate == GuiState.gotoStop){
                    guistate = GuiState.canStop;
                    myTruck.msgatSlot();
                    return;
            }
            
            if (xPos == xDestination && yPos != yDestination){
                    NorthSouth = false;
                    EastWest = true; //y oriented
            }
            else if (yPos == yDestination && xPos != xDestination){
                    EastWest = false;
                    NorthSouth = true;
            }
            
            
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;
        
   // if (xPos == xDestination && yPos == yDestination && guistate == GuiState.canStop){
        if (xPos == overallX && yPos == overallY && guistate == GuiState.canStop){
                guistate = GuiState.atStop;
                System.out.println("At Place where I'm supposed to be at/ dead position");
                myTruck.deadPos(deadpositionX, deadpositionY);
                xDestination = deadpositionX;
                yDestination = deadpositionY;
                atPosition(deadpositionX, deadpositionY);
                myTruck.msgatDestination();
        }
    }
    
    public void atPosition(int x, int y){
            if (deadpos){
                    deadpos = false;
            }
            else {
                    deadpos = true;
            }
            xPos = x;
            yPos = y;
    }
    
        public void moveto(int x, int y){
            guistate = GuiState.gotoStop;
                xDestination = x*scale;
                yDestination = y*scale;
        }
    
    
}
