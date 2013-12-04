package restaurant4.gui;

import restaurant4.Restaurant4CustomerRole;
import agent.Gui;

import java.awt.*;

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
	private enum Command {noCommand, GoToSeat, LeaveRestaurant, GoToCashier};
	private Command command=Command.noCommand;
	private GUIstate s = GUIstate.None;
	public enum GUIstate {EatingFood, OrderedFood, None}
	private String choice;
	
    public void setState(GUIstate st, String ch) {
    	s = st;
    	choice = ch;
    }

	public static final int xTable = 50;
	public static final int yTable = 200;

	public Restaurant4CustomerGui(Restaurant4CustomerRole c, Restaurant4AnimationPanel gui){ //HostAgent m) {
		agent = c;
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

		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat) agent.msgAnimationFinishedGoToSeat();
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				System.out.println("about to call gui.setCustomerEnabled(agent);");
			}
			else if (command ==Command.GoToCashier) {
				agent.msgAtCashier();
			}
			command=Command.noCommand;
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

	public void DoGoToSeat(int seatnumber) {//later you will map seatnumber to table coordinates.
		xDestination = xTable + (100 * (seatnumber - 1));
		yDestination = yTable;
		command = Command.GoToSeat;
	}

	public void DoGoToCashier(){
		xDestination = 100;
		yDestination = -20;
		command = Command.GoToCashier;
	}
	
	public void DoGoToPos(int x, int y){
		xDestination = x;
		yDestination = y;
	}
	public void DoExitRestaurant() {
		xDestination = -40;
		yDestination = -40;
		command = Command.LeaveRestaurant;
	}
}
