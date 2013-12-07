package restaurant4.gui;

import restaurant4.Restaurant4WaiterRole;
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

    private Restaurant4WaiterRole agent = null;
    Restaurant4AnimationPanel gui;
    private boolean tired = false;
    private GUIstate s = GUIstate.None;
    public enum GUIstate {None, CarryingFood}
    private boolean atDestination = true;
	private Map<String, Position> locations = new HashMap<String, Position>();
    private String choice;

    private int xPos, yPos;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position

    public Restaurant4WaiterGui(Restaurant4WaiterRole agent, Restaurant4AnimationPanel gui, int x, int y) {
        this.agent = agent;
        this.gui = gui;
        locations.put("Home", new Position(x, y));
        locations.put("Cashier", new Position(100, -20));
        locations.put("Host", new Position(-20, -20));
        locations.put("Cook", new Position(335, 150));
        locations.put("Table 1", new Position(70, 200));
        locations.put("Table 2", new Position(170, 200));
        locations.put("Table 3", new Position(270, 200));
        locations.put("Grill 1", new Position(335, 22));
        locations.put("Grill 2", new Position(335, 42));
        locations.put("Grill 3", new Position(335, 62));
        locations.put("Customer 1", new Position(60, 36));
        locations.put("Customer 2", new Position(81, 36));
        locations.put("Customer 3", new Position(102, 36));
        locations.put("Customer 4", new Position(123, 36));
        locations.put("Customer 5", new Position(144, 36));
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
