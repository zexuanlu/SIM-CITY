package market.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import agent.Gui;
import market.MarketEmployeeRole;

public class MarketEmployeeGui implements Gui{
	
	private MarketEmployeeRole agent;
	private int xPos = 400, yPos = 180;//default waiter position
    private int xDestination = 400, yDestination = 180;//default start position
    private int distance= 20;
    private int xTable = 180, yTable = 180;
    private int xTruck = 620, yTruck = 180;
    private int xSteak =300, ySteak = 140;
    private int xChicken = 400, yChicken = 140;
    private int xCar = 500, yCar = 140;
    private int xPizza = 300, yPizza = 240;
    private int xSalad = 400, ySalad = 240;
    private boolean takeFood = false;
    private boolean sendFood = false;
    
     public MarketEmployeeGui(MarketEmployeeRole agent){
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
        		& (xDestination == xSteak) & (yDestination == ySteak)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == xPizza) & (yDestination == yPizza)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == xCar) & (yDestination == yCar)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == xSalad) & (yDestination == ySalad)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == xChicken) & (yDestination == yChicken)) {
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
			xDestination = 300;
			yDestination = 140;
		}
		if(type == "Chicken"){
			xDestination = 400;
			yDestination = 140;
		}
		if(type == "Car"){
			xDestination = 500;
			yDestination = 140;
		}
		if(type == "Pizza"){
			xDestination = 300;
			yDestination = 240;
		}
		if(type == "Salad"){
			xDestination = 400;
			yDestination = 240;
		}
		takeFood = true;
	}
	
	public void DoSendToCashier(){
		xDestination = 180;
		yDestination = 180;
		sendFood = true;
	}
	
	public void DoSendToTruck(){
		xDestination = 620;
		yDestination = 180;
		sendFood = true;
	}
	
	
	
}
