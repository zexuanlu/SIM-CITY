package restaurant3.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import restaurant3.Restaurant3CustomerRole;

public class Restaurant3CustomerGui implements Gui {
	
	//Dimensions of gui
	private int width = 20;
	private int height = 20;

	Restaurant3CustomerRole agent = null;
	int origin = -20;
	int xDestination = origin;
	int yDestination = origin;
	int xPos, yPos;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;
	private boolean isPresent = false;
	
	
	public Restaurant3CustomerGui(Restaurant3CustomerRole c) {
		//Initialize agent
		agent = c;
		xPos = xDestination;
		yPos = yDestination;
	}

	@Override
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
			if (command==Command.GoToSeat) {
				agent.msgAtTableRelease();
			}
			else if (command==Command.LeaveRestaurant) {
				agent.msgAtTableRelease();
			}
			command=Command.noCommand;
		}
		
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.green);
		g.fillRect(xPos, yPos, width, height);
	}

	@Override
	public boolean isPresent() {
		return isPresent;
	}
	
	public void setPresent(boolean b){
		isPresent = b;
	}
	
	public void gotHungry(){
		isPresent = true;
	}
	
	//DO METHODS ***********************************
	public void DoFollowWaiterToTable(int table){
		int row;
     	if(table%3 !=0){
     		row = table/3 + 1;
		}
		else {
			row = table/3;
		}
        xDestination = (((table-1)%3)+1)*100;
        yDestination = row*100;
        
        command = Command.GoToSeat;
	}
	
	public void DoLeaveRestaurant(){
		xDestination = origin;
		yDestination = origin;
	}

}
