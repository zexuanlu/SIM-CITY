package restaurant6.gui;


import restaurant6.Restaurant6CookRole;
import utilities.Gui;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import person.PersonAgent;

public class Restaurant6CookGui implements Gui {
	
    private Restaurant6CookRole agent = null;
    public boolean isPresent;
//    RestaurantGui gui;

    private int xPos = 400, yPos = 40; // Default cook position
    private int xDestination = 400, yDestination = 40; // Default start position
    
    private int homeX = 420;
    private int homeY = 60;
    
    //public ImageIcon img = new ImageIcon(this.getClass().getResource("cook.png"));
    //Image image = img.getImage();
    
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
    	isPresent = false;
    	cookGuiState = GuiState.DoingNothing;
    	
    	synchronized(grills) {
    		grills.put("Mint Chip Ice Cream", new MyGrill(450, 110));
    		grills.put("Rocky Road Ice Cream", new MyGrill(450, 150));
    		grills.put("Green Tea Ice Cream", new MyGrill(450, 190));
    		grills.put("Mocha Almond Fudge Ice Cream", new MyGrill(450, 230));
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
        
        if ((xPos == 450 && yPos == 270) && (cookGuiState == GuiState.DoingNothing || cookGuiState == GuiState.WaitingForFoodToCook)) {
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
       // g.fillRect(xPos, yPos, 20, 20);
        //g.drawImage(image, xPos, yPos, 20, 20, null);
        g.drawString(((PersonAgent)agent.getPerson()).getName(), xPos-14, yPos+30);
        
        if (grills.get("Mint Chip Ice Cream").numCooking >= 1 && (grills.get("Mint Chip Ice Cream").state == MyGrill.GrillState.Cooking)) {
        	g.setColor(Color.lightGray);
        	g.drawString("Mi..", grills.get("Mint Chip Ice Cream").grillXPos, grills.get("Mint Chip Ice Cream").grillYPos+40);
        }
        if (grills.get("Rocky Road Ice Cream").numCooking >= 1 && (grills.get("Rocky Road Ice Cream").state == MyGrill.GrillState.Cooking)) {
        	g.setColor(Color.lightGray);
        	g.drawString("Ro..", grills.get("Rocky Road Ice Cream").grillXPos, grills.get("Rocky Road Ice Cream").grillYPos+40);
        }
        if (grills.get("Green Tea Ice Cream").numCooking >= 1 && (grills.get("Green Tea Ice Cream").state == MyGrill.GrillState.Cooking)) {
        	g.setColor(Color.lightGray);
        	g.drawString("Gr..", grills.get("Green Tea Ice Cream").grillXPos, grills.get("Green Tea Ice Cream").grillYPos+40);
        }
        if (grills.get("Mocha Almond Fudge Ice Cream").numCooking >= 1 && (grills.get("Mocha Almond Fudge Ice Cream").state == MyGrill.GrillState.Cooking)) {
        	g.setColor(Color.lightGray);
        	g.drawString("Mo..", grills.get("Mocha Almond Fudge Ice Cream").grillXPos, grills.get("Mocha Almond Fudge Ice Cream").grillYPos+40);
        }
    }
    
    public boolean isPresent() {
        return isPresent;
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
    	xDestination = 450;
    	yDestination = 270;
    }
    
    public void DoGoToPlatingArea() {
    	xDestination = 450;
    	yDestination = 40;
    }
    
    public void DoGoToHome() {
    	xDestination = homeX;
    	yDestination = homeY;
    }
    
    public void setPresent(boolean b){
    	isPresent = b; 
    }
}
