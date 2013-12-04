package restaurant6.gui;


import restaurant6.Restaurant6CookRole;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import agent.Gui;

public class Restaurant6CookGui implements Gui {

    private Restaurant6CookRole agent = null;
//    RestaurantGui gui;

    private int xPos = 320, yPos = 120; // Default cook position
    private int xDestination = 320, yDestination = 120; // Default start position
    
    private int homeX = 320;
    private int homeY = 120;
    
    private static class MyGrill {
    	private int grillXPos;
    	private int grillYPos;
    	private int numCooking;
    	public enum GrillState {Cooking, Empty}
    	private GrillState state;
    	
    	MyGrill(int x, int y) {
    		grillXPos = x;
    		grillYPos = y;
    		numCooking = 0;
    		state = GrillState.Empty;
    	}
    }
    
    // Sets the state of a particular grill to cooked
    public void setCookingGrillState(String food) {
    	grills.get(food).state = MyGrill.GrillState.Cooking;
    	++grills.get(food).numCooking;
    }
    
    // Sets the state of a particular grill to empty
    public void setEmptyGrillState(String food) {
    	--grills.get(food).numCooking;
    	if (grills.get(food).numCooking == 0) {
    		grills.get(food).state = MyGrill.GrillState.Empty;
    	}
    }
     
    Map<String, MyGrill> grills = new HashMap<String, MyGrill>();
    
    public enum GuiState {GettingIngredients, CookingFood, GotCookedFood, CookedFood, DoingNothing, GettingCookedFood, WaitingForFoodToCook};
    private GuiState cookGuiState;
    
    private String choice;

    public void drawCook(Graphics2D g) { 
    	if (cookGuiState == GuiState.GettingIngredients) {
    		g.setColor(Color.BLACK);
    		
    		String customerChoice = choice;
    		customerChoice = customerChoice.substring(0, 1);
    		
    		g.fillRect(xPos, yPos, 20, 20);
    		g.drawString(customerChoice, xPos, yPos);
    	}
    	if (cookGuiState == GuiState.GotCookedFood) {		
    		g.setColor(Color.BLACK);
    		
    		String customerChoice = choice;
    		customerChoice = customerChoice.substring(0, 2);
    		
    		g.fillRect(xPos, yPos, 20, 20);
    		g.drawString(customerChoice, xPos, yPos);
    	}   		
    }
    
    public void setState (GuiState st) {
    	cookGuiState = st;
    }
    
    public GuiState getState() {
    	return cookGuiState;
    }
    
    public Restaurant6CookGui(Restaurant6CookRole c) {//, RestaurantGui gui) {
    	agent = c;
//    	this.gui = gui;
    	cookGuiState = GuiState.DoingNothing;
    	
    	synchronized(grills) {
    		grills.put("Chicken", new MyGrill(400, 150));
    		grills.put("Steak", new MyGrill(440, 150));
    		grills.put("Pizza", new MyGrill(480, 150));
    		grills.put("Salad", new MyGrill(520, 150));
    	}
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
        
        if ((xPos == 560 && yPos == 150) && (cookGuiState == GuiState.DoingNothing || cookGuiState == GuiState.WaitingForFoodToCook)) {
        	agent.msgAtFridge();
        }
        
        if ((xPos == xDestination && yPos == yDestination) && (cookGuiState == GuiState.GettingIngredients || cookGuiState == GuiState.GettingCookedFood)) {
        	agent.msgAtGrill();
        }
        
        if ((xPos == xDestination && yPos == yDestination) && cookGuiState == GuiState.GotCookedFood) {
        	agent.msgAtPlatingArea();
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.PINK);
        g.fillRect(xPos, yPos, 20, 20);
        
        if (grills.get("Steak").numCooking >= 1 && (grills.get("Steak").state == MyGrill.GrillState.Cooking)) {
        	g.setColor(Color.lightGray);
        	g.drawString("St..", grills.get("Steak").grillXPos, grills.get("Steak").grillYPos+40);
        }
        if (grills.get("Chicken").numCooking >= 1 && (grills.get("Chicken").state == MyGrill.GrillState.Cooking)) {
        	g.setColor(Color.lightGray);
        	g.drawString("Ch..", grills.get("Chicken").grillXPos, grills.get("Chicken").grillYPos+40);
        }
        if (grills.get("Pizza").numCooking >= 1 && (grills.get("Pizza").state == MyGrill.GrillState.Cooking)) {
        	g.setColor(Color.lightGray);
        	g.drawString("Pi..", grills.get("Pizza").grillXPos, grills.get("Pizza").grillYPos+40);
        }
        if (grills.get("Salad").numCooking >= 1 && (grills.get("Salad").state == MyGrill.GrillState.Cooking)) {
        	g.setColor(Color.lightGray);
        	g.drawString("Sa..", grills.get("Salad").grillXPos, grills.get("Salad").grillYPos+40);
        }
    }
    
    public boolean isPresent() {
        return true;
    }
   
    public int getXPos() {                                                                
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    
    public void DoGoToGrill(String c) {
    	choice = c;
    	xDestination = grills.get(choice).grillXPos;
    	yDestination = grills.get(choice).grillYPos;
    }
    
    public void DoGoToFridge(String c) {
    	choice = c;
    	xDestination = 560;
    	yDestination = 150;
    }
    
    public void DoGoToPlatingArea() {
    	xDestination = 330;
    	yDestination = 150;
    }
    
    public void DoGoToHome() {
    	xDestination = homeX;
    	yDestination = homeY;
    }
}
