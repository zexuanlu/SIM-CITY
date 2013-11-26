package resident.gui;

import resident.HomeOwnerRole;

import java.awt.*;

public class HomeOwnerGui implements Gui {

    private HomeOwnerRole homeOwner = null;

    private int xPos = 600, yPos = 50; // Default home owner position
    private int xDestination = 600, yDestination = 50; // Default start position
    
    private int homeX = 50;
    private int homeY = 50;
    private int fridgeX = 70;
    private int fridgeY = 170;
    private int doorX = 400;
    private int doorY = 50;
    private int stoveX = 100;
    private int stoveY = 170;
    private int tableX = 300;
    private int tableY = 100;
    private int sinkX = 130;
    private int sinkY = 170;
    private int bedX = 270;
    private int bedY = 320;
    
    public enum HomeCookingState {GettingIngredients, Cooking, GettingCookedFood, Nothing};
    public HomeCookingState state;
    
    private String choice;
    
    public HomeOwnerGui(HomeOwnerRole c) {
    	homeOwner = c;
    }
    
    public void setState(HomeCookingState st, String ch) {
    	state = st;
    	choice = ch;
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
        
        if (xPos == bedX && yPos == bedY) {
        	homeOwner.msgAtBed();
        }
	}
	
	public void draw(Graphics2D g) {
		g.setColor(Color.BLUE);
        g.fillRect(xPos, yPos, 20, 20);
        
        String foodChoice = null;
        
        if (state == HomeCookingState.GettingIngredients) {
        	g.setColor(Color.BLUE);
    		
    		foodChoice = choice.substring(0, 2) + "?";
    		
    		g.fillRect(xPos, yPos, 20, 20);
    		g.drawString(foodChoice, xPos, yPos);
        }
        
        else if (state == HomeCookingState.Cooking) {
        	g.setColor(Color.LIGHT_GRAY);
    		
    		foodChoice = choice.substring(0, 2) + "..";
    		
    		g.fillRect(stoveX, stoveY+20, 20, 20);
    		g.drawString(foodChoice, stoveX, stoveY+20);
        }
        
        else if (state == HomeCookingState.GettingCookedFood) {
        	g.setColor(Color.BLUE);
    		
    		foodChoice = choice.substring(0, 2);
    		
    		g.fillRect(xPos, yPos, 20, 20);
    		g.drawString(foodChoice, xPos, yPos);
        }
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
