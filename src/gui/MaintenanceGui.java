package gui;

import resident.MaintenancePersonRole;

import java.awt.*;

public class MaintenanceGui implements Gui {

    private MaintenancePersonRole housekeeper = null;
    SimCityGui gui;

    private int xPos = 400, yPos = 50; // Default cook position
    private int xDestination = 400, yDestination = 50; // Default start position
    
    private int homeX = 50;
    private int homeY = 50;
    private int fridgeX = 70;
    private int fridgeY = 170;
    private int doorX = 430;
    private int doorY = 50;
    private int stoveX = 100;
    private int stoveY = 170;
    private int tableX = 200;
    private int tableY = 170;
    private int sinkX = 130;
    private int sinkY = 170;
    private int bedX = 250;
    private int bedY = 350;
    
    public MaintenanceGui(MaintenancePersonRole c, SimCityGui gui) {
    	housekeeper = c;
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
        	housekeeper.msgAtFridge();
        }
        
        if (xPos == doorX && yPos == doorY) {
        	housekeeper.msgAtDoor();
        }
        
        if (xPos == stoveX && yPos == stoveY) {
        	housekeeper.msgAtStove();
        }
        
        if (xPos == tableX && yPos == tableY) {
        	housekeeper.msgAtTable();
        }
        
        if (xPos == sinkX && yPos == sinkY) {
        	housekeeper.msgAtSink();
        }
        
        if (xPos == bedX && yPos == bedY) {
        	housekeeper.msgAtBed();
        }
	}
	
	public void draw(Graphics2D g) {
		g.setColor(Color.PINK);
        g.fillRect(xPos, yPos, 20, 20);
	}
	
	public boolean isPresent() {
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
	
	public void DoGoToBed() {
		xDestination = bedX;
		yDestination = bedY;
	}
  
}
