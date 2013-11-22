package simcity.gui;

import simcity.BusRole; 

import java.awt.Color;
import java.awt.Graphics2D;

public class BusGui implements Gui {
	private BusRole myBus = null;
	boolean NorthSouth; 
	boolean EastWest; 
	public int xPos, yPos, xDestination, yDestination; 
	
	public enum GuiState {gotoStop, atStop};
	GuiState guistate; 
	
	public BusGui(BusRole bus){
		myBus = bus; 
		
		//TEMPORARY TESTING CODE, starts it off at the left corner
		xPos = 0;
		yPos = 0; 
		xDestination = 0;
		yDestination = 0; 
	}
    
	public void draw(Graphics2D g) {
		g.setColor(Color.ORANGE);

		if (EastWest){
			g.fillRect(xPos, yPos, 30, 50);
		}
		else if (NorthSouth){
			g.fillRect(xPos, yPos, 50, 30);
		}
    }
	
    public boolean isPresent() {
        return true;
    }
    
    public void updatePosition() {
    	//check orientation of the bus
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
        
        if (xPos == xDestination && yPos == yDestination && guistate == GuiState.gotoStop){
        	guistate = GuiState.atStop; 
        	myBus.msgAtStop();
        }
    }
    
    public void GoToBusStop(int x, int y){
    	xDestination = x;
    	yDestination = y; 
    	guistate = GuiState.gotoStop; 
    }
    
}
