package simcity.gui;

import simcity.BusRole.myPassenger;
import simcity.*;
import simcity.astar.*; 
import utilities.Gui;

import java.util.*; 
import java.awt.*; 
import java.util.List; 
import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import person.PersonAgent;

public class BusGui implements Gui {
	int scale = 20; 
	
	private BusRole myBus = null;
	boolean NorthSouth; 
	boolean EastWest; 
	public int xPos, yPos, xDestination, yDestination; 
	public ImageIcon img = new ImageIcon(this.getClass().getResource("bus.png"));
	public Image bImg = img.getImage();
	
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
		g.drawString(myBus.toString(), xPos-14, yPos+30);
		if (EastWest){
			//g.fillRect(xPos, yPos, 20, 20);
			g.drawImage(bImg, xPos, yPos, 20, 20, null);
		}
		else if (NorthSouth){
			//g.fillRect(xPos, yPos, 20, 20);
			g.drawImage(bImg, xPos, yPos, 20, 20, null);
		}
		synchronized(myBus.passengers){
			for(int i = 0; i < myBus.passengers.size(); i++){
				g.drawString(((PersonAgent)((PassengerRole)myBus.passengers.get(i).p).person).getName(), xPos-15, yPos-(10*i));
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
            xPos+=2;
        else if (xPos > xDestination)
            xPos-=2;

        if (yPos < yDestination)
            yPos+=2;
        else if (yPos > yDestination)
            yPos-=2;
        
        if (xPos == xDestination && yPos == yDestination && guistate == GuiState.canStop){
        	guistate = GuiState.atStop; 
        	myBus.msgAtStop();
        }
    }
	
	public void moveto(int x, int y){
    	guistate = GuiState.gotoStop; 
		xDestination = x*scale; 
		yDestination = y*scale; 
	}
	
	public void setPresent(boolean b){
	//	isPresent = b; 
	}
}
