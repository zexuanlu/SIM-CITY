package restaurant2.gui;

import restaurant2.Restaurant2CustomerRole;
import restaurant2.Restaurant2HostRole;
import utilities.Gui;

import java.awt.*;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

import person.PersonAgent;

public class Restaurant2CustomerGui implements Gui{

	private Restaurant2CustomerRole agent = null;
	public boolean isPresent = false;
	private boolean isHungry = false;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;

	public static final int xTable = 50;
	public static final int yTable = 400;
    public static final int xTable2 = 150;
    public static final int yTable2 = 400;
    public static final int xTable3 = 250;
    public static final int yTable3 = 400;
    public static final int xTable4 = 350;
    public static final int yTable4 = 400;

    private String text = " ";
    
	private ImageIcon img = new ImageIcon(this.getClass().getResource("customer.png"));
	private Image cu = img.getImage();
    
	public Restaurant2CustomerGui(Restaurant2CustomerRole c, int x, int y){
		agent = c;
		xPos = -20;
		yPos = -20;
		xDestination = x;
		yDestination = y;
	}
	public void changeText(String text)
	{
		this.text = text;
	}
	public void updatePosition() {
		if (xPos < xDestination)
			xPos+=2;
		else if (xPos > xDestination)
			xPos-=2;

		if (yPos < yDestination)
			yPos+=2;
		else if (yPos > yDestination)
			yPos-=2;

		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat) agent.msgAnimationFinishedGoToSeat();
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				//System.out.println("paying for meal");
				agent.msgAtCashier();
				isHungry = false;
				//gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.drawImage(cu, xPos, yPos, 20, 20, null);
		g.drawString(((PersonAgent)agent.getPerson()).getName(), xPos-14, yPos+30);
		g.drawString(text, xPos, yPos-5);	
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
	public void resetHungry()
	{

	}
	public void DoGoToSeat(int seatnumber) {//later you will map seatnumber to table coordinates.
		if(seatnumber == 1){
			xDestination = xTable;
			yDestination = yTable;
			command = Command.GoToSeat;
		}
		else if(seatnumber == 2){
			xDestination = xTable2;
			yDestination = yTable2;
			command = Command.GoToSeat;			
		}
		else if(seatnumber == 3)
		{
			xDestination = xTable3;
			yDestination = yTable3;
			command = Command.GoToSeat;
		}
		else if(seatnumber == 4)
		{
			xDestination = xTable4;
			yDestination = yTable4;
			command = Command.GoToSeat;
		}
	}

	public void DoExitRestaurant() {
		xDestination = -40;
		yDestination = -40;
		command = Command.LeaveRestaurant;
	}
}
