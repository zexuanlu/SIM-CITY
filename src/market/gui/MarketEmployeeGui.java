package market.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import person.PersonAgent;
import utilities.Gui;
import market.MarketEmployeeRole;

public class MarketEmployeeGui implements Gui{
	
	private MarketEmployeeRole agent;
	private int xPos = 400, yPos = 180;//default waiter position
    private int xDestination = 400, yDestination = 180;//default start position
    private int distance= 20;
    private int xTable = 180, yTable = 180;
    private int xTruck = 530, yTruck = 180;
    private int xSteak =250, ySteak = 140;
    private int xChicken = 270, yChicken = 140;
    private int xCar = 290, yCar = 140;
    private int xPizza = 310, yPizza = 140;
    private int xSalad = 330, ySalad = 140;
    private int xHamburger = 350, yHamburger = 140;
    private int xRibs = 370, yRibs = 140;
    private int xPoundCake = 390, yPoundCake = 140;
    private int xShrimp = 410, yShrimp = 140;
    private int xScallops = 430, yScallops = 140;
    private int xLobster = 450, yLobster = 140;
    private int xCrab = 470, yCrab = 140;
    private boolean takeFood = false;
    private boolean sendFood = false;
	public boolean isPresent;
    
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
        		& (xDestination == xChicken) & (yDestination == yChicken)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == xCar) & (yDestination == yCar)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == xHamburger) & (yDestination == yHamburger)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == xSalad) & (yDestination == ySalad)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == xRibs) & (yDestination == yRibs)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == xPoundCake) & (yDestination == yPoundCake)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == xShrimp) & (yDestination == yShrimp)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == xScallops) & (yDestination == yScallops)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == xLobster) & (yDestination == yLobster)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == xCrab) & (yDestination == yCrab)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		g.setColor(Color.red);
        g.fillRect(xPos, yPos, distance, distance);
		g.drawString(((PersonAgent)agent.getPerson()).getName(), xPos-14, yPos+30);
	}

	@Override
	public boolean isPresent() {
		// TODO Auto-generated method stub
		return isPresent;
	}

	
	public void DoCollectFood(String type){
		if(type == "Steak"){
			xDestination = 250;
			yDestination = 140;
		}
		if(type == "Chicken"){
			xDestination = 270;
			yDestination = 140;
		}
		if(type == "Car"){
			xDestination = 290;
			yDestination = 140;
		}
		if(type == "Pizza"){
			xDestination = 310;
			yDestination = 140;
		}
		if(type == "Salad"){
			xDestination = 330;
			yDestination = 140;
		}
		if(type == "Hamburger"){
			xDestination = 350;
			yDestination = 140;
		}
		if(type == "Ribs"){
			xDestination = 370;
			yDestination = 140;
		}
		if(type == "Pound Cake"){
			xDestination = 390;
			yDestination = 140;
		}
		if(type == "Shrimp"){
			xDestination = 410;
			yDestination = 140;
		}
		if(type == "Scallops"){
			xDestination = 430;
			yDestination = 140;
		}
		if(type == "Lobster"){
			xDestination = 450;
			yDestination = 140;
		}
		if(type == "Crab"){
			xDestination = 470;
			yDestination = 140;
		}
		takeFood = true;
	}
	
	public void DoSendToCashier(){
		xDestination = 180;
		yDestination = 180;
		sendFood = true;
	}
	
	public void DoSendToTruck(){
		xDestination = 530;
		yDestination = 180;
		sendFood = true;
	}

	public void setPresent(boolean b) {
		isPresent = b;
	}
	
	
	
}
