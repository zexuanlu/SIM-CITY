package restaurant6.gui;

import restaurant1.Restaurant1SDWaiterRole;
import restaurant1.Restaurant1WaiterRole;
import restaurant6.Restaurant6AbstractWaiterRole;
import restaurant6.Restaurant6HostRole; 
import restaurant6.Restaurant6SDWaiterRole;
import restaurant6.Restaurant6Table;
import restaurant6.Restaurant6WaiterRole;
import utilities.Gui;

import java.awt.*;

import person.PersonAgent;

public class Restaurant6WaiterGui implements Gui {

    private Restaurant6AbstractWaiterRole agent = null;
    private Restaurant6CookGui cookGui;
    private boolean isPresent;
    private Restaurant6HostRole host = null;
    private boolean onBreakSoon = false;
    private boolean canGoBackToWork = false;
    private boolean requestedBreak;
    private boolean isDelivering = false;
    
    // Sets home positions for waiter
    private int homeX;
    private int homeY;
    
    String customerFood;
    //RestaurantGui gui;

    private int xPos, yPos;//default waiter position
    private int xDestination, yDestination;//default start position
    
    private enum Command {noCommand, WaitingForCustomer, GoingToCook, GoingToPlate, GoingToCashier, GoingToFront, GoingToHome};
	private Command command=Command.noCommand;
    
    // Pickup spot
    private final int pickUpX = 30;
    private final int pickUpY = 150;
    
    public void msgRequestedBreak() {
    	requestedBreak = true;
    }
    
    public boolean getRequestedBreak() {
    	return requestedBreak;
    }
    
    public void DoCheckBreak(boolean p, Restaurant6AbstractWaiterRole w) {
    	requestedBreak = p;
    	//gui.setWaiterDisabled(w);
    }
    
    public void DoUncheckBreak(Restaurant6AbstractWaiterRole w) {
    	requestedBreak = false;
    	//gui.setWaiterEnabled(w);
    }
    
    public boolean goBackToWork() {
    	return canGoBackToWork;
    }
    
    public boolean isFront()
    {
    	if (xPos == pickUpX && yPos == pickUpY)
    		return true;
    	else
    		return false;
    }
    
    public boolean isDelivering() {
    	return isDelivering;
    }
    
    public void setDeliveringFood(boolean p) {
    	isDelivering = p;
    }
    
    public final int tableXPos = 30;
    public final int tableYPos = 360;

    public Restaurant6WaiterGui(Restaurant6AbstractWaiterRole w, int x, int y) { //, RestaurantGui gui, int x, int y) {
    	agent = w;
		//this.gui = gui;
		requestedBreak = false;
		
		isPresent = false;
		
		homeX = x;
		homeY = y;
		xPos = homeX;
		yPos = homeY;
		xDestination = homeX;
		yDestination = homeY;
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

        for (Restaurant6Table t : agent.tables)
    	{
        	if (xPos == xDestination && yPos == yDestination
        		&& (xDestination == t.getXPos() + 20) && (yDestination == t.getYPos() - 20)) 
        	{
        		agent.msgAtTable();
        	}
        }
        
        if (xPos == xDestination && yPos == yDestination) {
        	if (command == Command.GoingToCashier) {
        		agent.msgAtCashier();
        	}
        	else if (command == Command.GoingToCook) {
        		agent.msgAtCook();
        	}
        	else if (command == Command.GoingToFront) {
        		agent.msgAtFront();
        	}
        	else if (command == Command.GoingToPlate) {
        		agent.msgAtPlate();
        		//cookGui.msgPickingUpFood();
        	}
        	command = Command.noCommand;
        }
        
        /*if (xPos == 300 & yPos == 120)
        	agent.msgAtCook();
        
        if (xPos == 300 & yPos == 150) 
        	agent.msgAtPlate();
        
        if (xPos == 20 & yPos == 50)
        	agent.msgAtCashier();
        
        if (xPos == pickUpX & yPos == pickUpY) 
        	agent.msgAtFront();*/
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, 20, 20);
        
        if(agent instanceof Restaurant6WaiterRole){
        	g.drawString(((PersonAgent)((Restaurant6WaiterRole)agent).getPerson()).getName(), xPos-14, yPos+30);
        }
        else if(agent instanceof Restaurant6SDWaiterRole){
        	g.drawString(((PersonAgent)((Restaurant6SDWaiterRole)agent).getPerson()).getName(), xPos-14, yPos+30);
        }
    }

    // Draws the waiter delivering food
    public void drawDelivering(Graphics2D g) {
		g.setColor(Color.BLACK);
		
		String choice = agent.getCustChoice();
		customerFood = choice.substring(0, 2);
		
		g.fillRect(xPos, yPos, 20, 20);
		g.drawString(customerFood, xPos, yPos);
	}
    
    // Sets the waiter on break
	public void setOnBreak() {
		agent.msgOnBreak();
	}
	
	// Sets waiter off break
	public void setOffBreak() {
		onBreakSoon = false;
		agent.msgOffBreak();
	}
    
    public boolean isPresent() {
        return isPresent;
    }
    
    public boolean isOnBreak() {
        return onBreakSoon;
    }

    public void DoGoToTable(int tableNum) 
    {
    	for (Restaurant6Table t : agent.tables)
	    {
	    		if (t.getTableNum() == tableNum)
	    		{
	    			xDestination = t.getXPos() + 20;
	    			yDestination = t.getYPos() - 20;
	    			break;
	    		}
	    }
    }
    
    public void DoGoToHomePosition() {
        xDestination = homeX;
        yDestination = homeY;
        command = Command.GoingToHome;
    }
    
    public void DoGoToCook() {
    	xDestination = 480;
        yDestination = 40;
        command = Command.GoingToCook;
    }
    
    public void DoGoToPlate() {
    	xDestination = 450;
    	yDestination = 40;
    	command = Command.GoingToPlate;
    }
    
    public void DoGoToCashier() {
    	xDestination = 20;
    	yDestination = 50;
    	command = Command.GoingToCashier;
    }
    
    public void DoGoToFront() {
    	command = Command.GoingToFront;
    	xDestination = pickUpX;
    	yDestination = pickUpY;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}