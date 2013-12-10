package restaurant3.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

import person.PersonAgent;
import restaurant1.Restaurant1CustomerRole;
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
	public boolean isPresent = false;
	String food;
	String eating;
	
	public ImageIcon img = new ImageIcon(this.getClass().getResource("customer.png"));
	public Image image = img.getImage();
	
	public Restaurant3CustomerGui(Restaurant3CustomerRole c) {
		//Initialize agent
		agent = c;
		xPos = xDestination;
		yPos = yDestination;
		food = "";
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
		g.drawString(food, xPos, yPos+height+height);
		g.drawString(((PersonAgent)((Restaurant3CustomerRole)agent).getPerson()).getName(), xPos-14, yPos+30);
		//g.fillRect(xPos, yPos, width, height);
		g.drawImage(image, xPos, yPos, width, height, null);
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
	
	public void ordered(String f){
		food = (f + "?");
		eating = f;
	}
	
	public void eatingFood(){
		food = eating;
	}
	
	public void done(){
		food = "";
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
