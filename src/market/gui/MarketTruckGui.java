package market.gui;
import simcity.astar.*; 
import simcity.CarAgent;  
import utilities.Gui;
import market.MarketTruckAgent; 

import java.util.*; 
import java.awt.*; 
import java.util.List; 
import java.awt.Graphics2D;

	
public class MarketTruckGui implements Gui {
        
        public boolean isPresent;
        
        private int distance = 20;
        public int xPos = 200, yPos = 140, xDestination = 200, yDestination = 140;
        public int overallX, overallY;
        public boolean atstop = false;
        
        private MarketTruckAgent myTruck;
        
        private int xrest = 200;
        private int yrest = 80;
        private int ymar = 140;
        
        public enum GuiState {gotoStop, atStop,canStop};
        GuiState guistate;
        
        public MarketTruckGui(MarketTruckAgent c){
            myTruck = c;
        }
        
        
        public void draw(Graphics2D g) {
    		g.setColor(Color.red);
            g.fillRect(xPos, yPos, distance, distance);
               
    }
        
        public boolean isPresent() {
        return true;
    }
    
        
    public void updatePosition() {
           
            
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;
        if (xPos == xDestination &&atstop&& yPos == yDestination
        		& (xDestination == xrest) & (yDestination == yrest )) {
        	atstop = false;
           myTruck.msgrelease();
        }

        if (xPos == xDestination &&atstop&& yPos == yDestination
        		& (xDestination == xrest) & (yDestination == ymar )) {
        	atstop = false;
           myTruck.msgrelease();
        }
        }
    
    public void GotoCook(){
    	xDestination = 200;
    	yDestination = 80;
    	atstop = true;
    }
  
    public void GoBack(){
    	xDestination = 200;
    	yDestination = 140;
    	atstop = true;
    }
    
}
