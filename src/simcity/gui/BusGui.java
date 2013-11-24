package simcity.gui;

import simcity.BusRole; 
import simcity.astar.*; 

import java.util.*; 
import java.awt.*; 
import java.util.List; 
import java.awt.Graphics2D;

public class BusGui implements Gui {
	int scale = 20; 
	
	private BusRole myBus = null;
	boolean NorthSouth; 
	boolean EastWest; 
	public int xPos, yPos, xDestination, yDestination; 
	
	Position currentPosition; 
	Position originalPosition;
	AStarTraversal aStar; 
	
	public enum GuiState {gotoStop, atStop,canStop, atSlot};
	GuiState guistate; 
	
	public BusGui(BusRole bus, int x, int y){
		myBus = bus; 
		//TEMPORARY TESTING CODE, starts it off at the left corner
		
		xDestination = x+1; 
		yDestination = y+1; 
		xPos = x;
		yPos = y;
		EastWest = true; 
		}
    
	public void draw(Graphics2D g) {
		g.setColor(Color.ORANGE);
		if (EastWest){
			g.fillRect(xPos, yPos, 20, 20);
		}
		else if (NorthSouth){
			g.fillRect(xPos, yPos, 20, 20);
		}
    }
	
    public boolean isPresent() {
        return true;
    }
    
    public void updatePosition() {
    	//check orientation of the bus
    	if (xPos == xDestination && yPos == yDestination && guistate == GuiState.gotoStop){
    		guistate = GuiState.canStop; 
    		System.out.println("msg at Stop");
    		myBus.msgatSlot();
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
        
        if (xPos == xDestination && yPos == yDestination && guistate == GuiState.canStop){
        	guistate = GuiState.atStop; 
        	myBus.msgAtStop();
        }
    }
	
	public void moveto(int x, int y){
    	guistate = GuiState.gotoStop; 
		xDestination = x*scale; 
		yDestination = y*scale; 
		System.out.println(xDestination +" " + yDestination);
	}
}
