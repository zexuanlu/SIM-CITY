package restaurant4.gui;

import restaurant4.Restaurant4AbstractWaiter;
import simcity.astar.Position;
import utilities.Gui;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the waiters
 * in the restaurant
 */
public class Restaurant4WaiterGui implements Gui {

    private Restaurant4AbstractWaiter agent = null;
    private boolean tired = false;
    private GUIstate s = GUIstate.None;
    public enum GUIstate {None, CarryingFood}
    private boolean atDestination = true;
	private Map<String, Position> locations = new HashMap<String, Position>();
    private String choice;

    private int xPos, yPos;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position

    public Restaurant4WaiterGui(Restaurant4AbstractWaiter agent, int x, int y) {
        this.agent = agent;
        locations.put("Home", new Position(x, y));
        locations.put("Cashier", new Position(150, -20));
        locations.put("Host", new Position(-20, -20));
        locations.put("Cook", new Position(385, 250));
        locations.put("Table 1", new Position(120, 300));
        locations.put("Table 2", new Position(220, 300));
        locations.put("Table 3", new Position(320, 300));
        locations.put("Grill 1", new Position(385, 122));
        locations.put("Grill 2", new Position(385, 142));
        locations.put("Grill 3", new Position(385, 162));
        locations.put("Customer 1", new Position(110, 136));
        locations.put("Customer 2", new Position(131, 136));
        locations.put("Customer 3", new Position(152, 136));
        locations.put("Customer 4", new Position(173, 136));
        locations.put("Customer 5", new Position(194, 136));
        xDestination = x;
        yDestination = y;
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
        if (xPos == xDestination && yPos == yDestination && !atDestination){
        	atDestination = true;
        	agent.msgAtDestination();
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, 20, 20);
        
        String foodChoice = null;
        
        if (s == GUIstate.CarryingFood) {
        	g.setColor(Color.BLACK);
    		
    		foodChoice = choice.substring(0, 2);
    		g.drawString(foodChoice, xPos, yPos-10);
        }
    }

    public boolean isPresent() {
        return true;
    }

    public void carryFood(String food){
    	s = GUIstate.CarryingFood;
    	choice = food;
    }
    
    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    
    public boolean isTired() {
    	return tired;
    }
    public void noBreak() {
    	tired = false;
    }
    public void goOnBreak() {
    	xDestination = -21;
    	yDestination = -21;
    }
    public void setTired() {
    	if(tired){
    		tired = false;
    		agent.msgEndOfBreak();
    	}
    	else{
    		tired = true;
    		agent.msgWantToBreak();
    	}
    }

	public void GoToLocation(String string) {
		Position p = locations.get(string);
		xDestination = p.getX();
		yDestination = p.getY();
		atDestination = false;
	}
}
