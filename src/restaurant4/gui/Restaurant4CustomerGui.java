package restaurant4.gui;

import restaurant4.Restaurant4CustomerRole;
import simcity.astar.Position;
import utilities.Gui;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import person.PersonAgent;

/**
 * This class represents the 
 * customers in the restaurant in the animation
 */
public class Restaurant4CustomerGui implements Gui{

	private Restaurant4CustomerRole agent = null;
	public boolean isPresent = false;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private GUIstate s = GUIstate.None;
	public enum GUIstate {EatingFood, OrderedFood, None}
    private boolean atDestination = true;
	private Map<String, Position> locations = new HashMap<String, Position>();
	private String choice;
	
	public ImageIcon img = new ImageIcon(this.getClass().getResource("customer.png"));
	public Image image = img.getImage();
	
    public void setState(GUIstate st, String ch) {
    	s = st;
    	choice = ch;
    }
    
    public void carryFood(boolean b){
    	if(b)
    		s = GUIstate.EatingFood;
    	else
    		s = GUIstate.None;
    }

	public static final int xTable = 50;
	public static final int yTable = 200;

	public Restaurant4CustomerGui(Restaurant4CustomerRole c){ //HostAgent m) {
		agent = c;
        locations.put("Cashier", new Position(150, -20));
        locations.put("Host", new Position(-20, -20));
        locations.put("Home", new Position(-20, -20));
        locations.put("Table 1", new Position(100, 300));
        locations.put("Table 2", new Position(200, 300));
        locations.put("Table 3", new Position(300, 300));
        locations.put("Customer 1", new Position(110, 136));
        locations.put("Customer 2", new Position(131, 136));
        locations.put("Customer 3", new Position(152, 136));
        locations.put("Customer 4", new Position(173, 136));
        locations.put("Customer 5", new Position(194, 136));
		xPos = -20;
		yPos = -20;
		xDestination = -20;
		yDestination = -20;
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
		g.drawImage(image, xPos, yPos, 20, 20, null);
		g.drawString(((PersonAgent)agent.getPerson()).getName(), xPos-14, yPos+30);
		
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
