package gui;

import resident.HomeOwnerRole;

import java.awt.*;

public class HomeOwnerGui implements Gui {

    private HomeOwnerRole homeOwner = null;
    SimCityGui gui;

    private int xPos = 50, yPos = 50; // Default cook position
    private int xDestination = 50, yDestination = 50; // Default start position
    
    private int homeX = 50;
    private int homeY = 50;
    private int fridgeX = 70;
    private int fridgeY = 170;
    private int doorX = 400;
    private int doorY = 50;
    private int stoveX = 100;
    private int stoveY = 170;
    private int tableX = 200;
    private int tableY = 170;
    private int sinkX = 130;
    private int sinkY = 170;
    
    public HomeOwnerGui(HomeOwnerRole c, SimCityGui gui) {
    	homeOwner = c;
    	this.gui = gui;
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
        
        if (xPos == fridgeX && yPos == fridgeY) {
        	homeOwner.msgAtFridge();
        }
        
        if (xPos == doorX && yPos == doorY) {
        	homeOwner.msgAtDoor();
        }
        
        if (xPos == stoveX && yPos == stoveY) {
        	homeOwner.msgAtStove();
        }
        
        if (xPos == tableX && yPos == tableY) {
        	homeOwner.msgAtTable();
        }
        
        if (xPos == sinkX && yPos == sinkY) {
        	homeOwner.msgAtSink();
        }
	}
	
	public void draw(Graphics2D g) {
		g.setColor(Color.PINK);
        g.fillRect(xPos, yPos, 20, 20);
	}
	
	public boolean isPresent() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public void DoGoToFridge() {
		xDestination = fridgeX;
		yDestination = fridgeY;
	}
	
	public void DoGoToFrontDoor() {
		xDestination = doorX;
		yDestination = doorY;
	}
	
	public void DoGoToStove() {
		xDestination = stoveX;
		yDestination = stoveY;
	}
	
	public void DoGoToHome() {
		xDestination = homeX;
		yDestination = homeY;
	}
	
	public void DoGoToTable() {
		xDestination = tableX;
		yDestination = tableY;
	}
	
	public void DoGoToSink() {
		xDestination = sinkX;
		yDestination = sinkY;
	}
  
}
