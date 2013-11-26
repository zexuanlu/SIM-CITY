package restaurant.gui;

import restaurant.Restaurant1CustomerRole;

import java.awt.*;

import javax.swing.ImageIcon;

import agent.Gui;

public class CustomerGui implements Gui{

	private Restaurant1CustomerRole agent = null;
	public boolean isPresent = false;
	private boolean isHungry = false;
	private int location = 0;
	private int origion = 40;
	private String order = "";
	private String orderdone = "";
	//private HostAgent host;
	Restaurant1AnimationPanel gui;
	
	private int distance = 20;
	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;

	public static final int xTable = 200;
	public static final int yTable = 250;
	
	public static final int x1Table = 300;
	public static final int y1Table = 250;
	public static final int y2Table = 150;

	public CustomerGui(Restaurant1CustomerRole c, Restaurant1AnimationPanel gui){ //HostAgent m) {
		agent = c;
		xPos = 0;
		yPos = 0;
		xDestination = 40;
		yDestination = 40;
		//maitreD = m;
		//this.gui = gui;
	}
	

	
	public void setLocation(int loc){
		location = loc;
		xPos = location * origion;
		xDestination = location * origion;
	}
	
    public void stop(){
    
    }

    public void zou(){
    
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
				isHungry = false;
				//gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.green);
		g.drawString(order, xPos, yPos + distance + distance);	
		g.fillRect(xPos, yPos, distance, distance);
	}
	
	public void ordered(String a){
		order = a+"?";
		orderdone = a;
	}
	
	public void eating(){
		order = orderdone;
	}
	
	public void done(){
		order = "";
	}
	

	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
		agent.gotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToSeat(int seatnumber) {//later you will map seatnumber to table coordinates.
		if(seatnumber ==1){
		xDestination = xTable;
		yDestination = yTable;
		}
		else if(seatnumber ==2){
			xDestination = x1Table;
			yDestination = y1Table;
		}
		else if(seatnumber ==3){
			xDestination = x1Table;
			yDestination = y2Table;
		}
		command = Command.GoToSeat;
	}

	public void DoExitRestaurant() {
		xDestination = -40;
		yDestination = -40;
		command = Command.LeaveRestaurant;
	}
}
