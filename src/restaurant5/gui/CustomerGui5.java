package restaurant5.gui;

import restaurant5.CustomerAgent5;
import utilities.Gui;

import java.awt.*;

import person.PersonAgent;

public class CustomerGui5 implements Gui{
	private int sit; 

	private CustomerAgent5 agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;
	
	private boolean showIcon = false; 
	//private HostAgent host;
	RestaurantGui gui;
	private String myChoice; 

	private int enterX; 
	private int waitingX; 
	private int waitingY; 
	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;

	public int xTable = 200;
	public int yTable = 250;

	public CustomerGui5(CustomerAgent5 c, RestaurantGui gui){ //HostAgent m) {
		agent = c;
		xPos = -40;
		yPos = -40;
		xDestination = -40;
		yDestination = -40;
		this.gui = gui;
	}
	
	public CustomerGui5(CustomerAgent5 c, RestaurantGui gui, int x){ //HostAgent m) {
		agent = c;
		waitingX = -20; 
		waitingY = -20; 
		xPos = waitingX;
		yPos = waitingY;
		enterX = x; 
		xDestination = -20;
		yDestination = -20;
		this.gui = gui;
	}
	
	public void enterRestaurant(){
		xDestination = enterX; 
		yDestination = 5; 
	}

	
	public void setChoice(String choice){
		myChoice = choice; 
	}
	
    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    
    //Food Icon
    public void doShowIcon(){ //can enable here since he just ordered
    	showIcon = true;
   
    }
    
    public void doHideIcon(){
    	showIcon = false; 
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
		
		
	    if (xPos == xDestination && yPos == yDestination && xPos == enterX){
	    	agent.msgatRestaurant();
		}
		else if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat) agent.msgAnimationFinishedGoToSeat();
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				isHungry = false;
				gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
		}
	
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, 20, 20);
		g.drawString(((PersonAgent)agent.getPerson()).getName(), xPos-14, yPos+30);
		
		//This is the icon that stays at the customer's table telling you that they have ordered
		if (showIcon){
			g.setColor(Color.BLACK);
			if (myChoice.equals("Steak")){
	        	g.drawString("ST?",xPos+20,yPos+20);
			}
			else if (myChoice.equals("Salad")){
	        	g.drawString("SA?",xPos+20,yPos+20);
			}
			else if(myChoice.equals("Chicken")){
	        	g.drawString("CH?",xPos+20,yPos+20);
			}
			else if(myChoice.equals("Pizza")){
	        	g.drawString("PI?",xPos+20,yPos+20);
			}
			
			
		}
	}

	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
		agent.msgGotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToSeat(int seatnumber) {//added the seatnumber for multiple tables
		sit = seatnumber - 1;

    	if (sit == 0){
    		xTable = 200;
    	}
    	if (sit == 1){
    		xTable = 300;
    	}
    	if (sit == 2){
    		xTable = 400;
    	}
		
		
		xDestination = xTable;
		yDestination = yTable;
		command = Command.GoToSeat;
	}
	
	public void Leave(){
		xDestination = -20; 
		yDestination = -20; 
		command = Command.LeaveRestaurant; 
	}
	
	
	public void DoExitRestaurant() {
		xDestination = waitingX;
		yDestination = waitingY;
		command = Command.LeaveRestaurant;
	}
}
