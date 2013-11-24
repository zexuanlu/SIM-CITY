package gui;

import resident.ApartmentTenantRole;

import java.awt.*;

public class ApartmentTenantGui implements Gui {

    private ApartmentTenantRole aptTenant = null;
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
    private int bedX = 130;
    private int bedY = 170;
    
    public ApartmentTenantGui(ApartmentTenantRole c, SimCityGui gui) {
    	aptTenant = c;
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
        	aptTenant.msgAtFridge();
        }
        
        if (xPos == doorX && yPos == doorY) {
        	aptTenant.msgAtDoor();
        }
        
        if (xPos == stoveX && yPos == stoveY) {
        	aptTenant.msgAtStove();
        }
        
        if (xPos == tableX && yPos == tableY) {
        	aptTenant.msgAtTable();
        }
        
        if (xPos == sinkX && yPos == sinkY) {
        	aptTenant.msgAtSink();
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
