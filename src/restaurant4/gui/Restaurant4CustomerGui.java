package restaurant4.gui;

import restaurant4.Restaurant4CustomerRole;
import simcity.astar.Position;
import utilities.Gui;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the 
 * customers in the restaurant in the animation
 */
public class Restaurant4CustomerGui implements Gui{

	private Restaurant4CustomerRole agent = null;
	private boolean isPresent = true;

	Restaurant4AnimationPanel gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private GUIstate s = GUIstate.None;
	public enum GUIstate {EatingFood, OrderedFood, None}
    private boolean atDestination = true;
	private Map<String, Position> locations = new HashMap<String, Position>();
	private String choice;
	
    public void setState(GUIstate st, String ch) {
    	s = st;
    	choice = ch;
    }

	public static final int xTable = 50;
	public static final int yTable = 200;

	public Restaurant4CustomerGui(Restaurant4CustomerRole c, Restaurant4AnimationPanel gui){ //HostAgent m) {
		agent = c;
        locations.put("Cashier", new Position(100, -20));
        locations.put("Host", new Position(-20, -20));
        locations.put("Table 1", new Position(70, 200));
        locations.put("Table 2", new Position(170, 200));
        locations.put("Table 3", new Position(270, 200));
        locations.put("Customer 1", new Position(60, 36));
        locations.put("Customer 2", new Position(81, 36));
        locations.put("Customer 3", new Position(102, 36));
        locations.put("Customer 4", new Position(123, 36));
        locations.put("Customer 5", new Position(144, 36));
		xPos = -20;
		yPos = -20;
		xDestination = -20;
		yDestination = -20;
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

		if (xPos == xDestination && yPos == yDestination && !atDestination) {
			agent.msgAtDestination();
			atDestination = true;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, 20, 20);
		
        String foodChoice = null;
        
        if (s == GUIstate.OrderedFood) {
        	g.setColor(Color.GREEN);
    		
    		foodChoice = choice.substring(0, 2) + "?";
    		
    		g.drawString(foodChoice, xPos+20, yPos+20);
        }
        
        else if (s == GUIstate.EatingFood) {
        	g.setColor(Color.BLUE);
    		
    		foodChoice = choice.substring(0, 2);
    		
    		g.drawString(foodChoice, xPos+20, yPos+20);
        }
	}

	public boolean isPresent() {
		return isPresent;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void DoGoToPos(int x, int y){
		xDestination = x;
		yDestination = y;
	}
	
	public void DoGoToLocation(String location){
		Position p = locations.get(location);
		xDestination = p.getX();
		yDestination = p.getY();
		atDestination = false;
	}
}
