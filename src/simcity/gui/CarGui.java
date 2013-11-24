package simcity.gui;
import simcity.BusRole;
import simcity.astar.*; 
import simcity.gui.BusGui.GuiState;
import simcity.CarAgent; 

import java.util.*; 
import java.awt.*; 
import java.util.List; 
import java.awt.Graphics2D;

public class CarGui implements Gui {
	
	int scale = 20; 
	
	List<Dimension> moves = new ArrayList<Dimension>();
	boolean NorthSouth; 
	boolean EastWest; 
	public int xPos, yPos, xDestination, yDestination; 
	public int overallX, overallY; 
	
	private CarAgent myCar; 
	
	public enum GuiState {gotoStop, atStop,canStop};
	GuiState guistate; 
	
	CarGui(CarAgent c, int x, int y){
		myCar = c; 
    	xPos = x; 
    	yPos = y; 
    	xDestination = x+1; 
    	yDestination = y+1; 
    	EastWest = true; 
	}
	
	public void draw(Graphics2D g) {
		g.setColor(Color.RED);
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
    		myCar.msgatSlot();
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
        	System.out.println("At Place where I'm supposed to be at");
        	//myPerson.msgAtDestination(); 
        }
        
    }
    
    
	public void moveto(int x, int y){
    	guistate = GuiState.gotoStop; 
		xDestination = x*scale; 
		yDestination = y*scale; 
		System.out.println(xDestination +" " + yDestination);
	}
    
    
}
