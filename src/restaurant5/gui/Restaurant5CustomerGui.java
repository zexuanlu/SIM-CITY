package restaurant5.gui;

import restaurant5.Restaurant5CustomerAgent;
import utilities.Gui;

import java.awt.*;

public class Restaurant5CustomerGui implements Gui{
	private int sit; 

	private Restaurant5CustomerAgent agent = null;
	public boolean isPresent = false;
	private boolean isHungry = false;
	
	private boolean showIcon = false; 
	//private HostAgent host;
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

	public Restaurant5CustomerGui(Restaurant5CustomerAgent c){ //HostAgent m) {
		agent = c;
		xPos = -20;
		yPos = -20;
		waitingX = 20; 
		waitingY = 20; 
		xDestination = -20;
		yDestination = -20;
	}
	
	public Restaurant5CustomerGui(Restaurant5CustomerAgent c, int x){ //HostAgent m) {
		agent = c;
		waitingX = -20; 
		waitingY = -20; 
		xPos = waitingX;
		yPos = waitingY;
		enterX = x; 
		xDestination = -20;
		yDestination = -20;
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
			}
			command=Command.noCommand;
		}
	
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, 20, 20);
		
		//This is the icon that stays at the customer's table telling you that they have ordered
		if (showIcon){
			g.setColor(Color.BLACK);
			if (myChoice.equals("Belgium")){
	        	g.drawString("BG?",xPos+20,yPos+20);
			}
			else if (myChoice.equals("Sassy")){
	        	g.drawString("SA?",xPos+20,yPos+20);
			}
			else if(myChoice.equals("Chicken")){
	        	g.drawString("CK?",xPos+20,yPos+20);
			}
			else if(myChoice.equals("Chocolate")){
	        	g.drawString("CH?",xPos+20,yPos+20);
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
