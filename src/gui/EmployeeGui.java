package gui;

import java.awt.Color;
import java.awt.Graphics2D;

import market.EmployeeAgent;

public class EmployeeGui implements Gui{
	
	private EmployeeAgent agent;
	private int xPos = 280, yPos = 430;//default waiter position
    private int xDestination = 280, yDestination = 430;//default start position
    private int distance= 20;
    private int xTable = 280, yTable = 330;
    private int xTruck = 280, yTruck = 580;
    private boolean takeFood = false;
    private boolean sendFood = false;
    
    public void setAgent(EmployeeAgent agent){
    	this.agent = agent;
    }
    
	@Override
	public void updatePosition() {
		// TODO Auto-generated method stub
		if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos =xPos - 1;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos = yPos - 1;
        
        if (xPos == xDestination &&sendFood&& yPos == yDestination
        		& (xDestination == xTable ) & (yDestination == yTable)) {
        	sendFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&sendFood&& yPos == yDestination
        		& (xDestination == xTruck ) & (yDestination == yTruck)) {
        	sendFood = false;
           agent.msgAtTable();

        }
        
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == 320) & (yDestination == 400)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == 240) & (yDestination == 400)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == 320) & (yDestination == 460)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == 240) & (yDestination == 460)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        
        
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		g.setColor(Color.red);
        g.fillRect(xPos, yPos, distance, distance);
	}

	@Override
	public boolean isPresent() {
		// TODO Auto-generated method stub
		return true;
	}

	
	public void DoCollectFood(String type){
		if(type == "Steak"){
			xDestination = 320;
			yDestination = 400;
		}
		if(type == "Chicken"){
			xDestination = 240;
			yDestination = 400;
		}
		if(type == "Car"){
			xDestination = 320;
			yDestination = 460;
		}
		if(type == "Rice"){
			xDestination = 240;
			yDestination = 460;
		}
		takeFood = true;
	}
	
	public void DoSendToCashier(){
		xDestination = 280;
		yDestination = 330;
		sendFood = true;
	}
	
	public void DoSendToTruck(){
		xDestination = 280;
		yDestination = 580;
		sendFood = true;
	}
	
	
	
}
