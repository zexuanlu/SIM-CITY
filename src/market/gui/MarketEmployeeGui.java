package market.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

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
    private int xmi = 250, ymi = 240;
    private int xri = 270, yri = 240;
    private int xgi = 290, ygi = 240;
    private int xmoi = 310, ymoi = 240;
    private int xBelgium = 330, yBelgium = 240;
    private int xSassy = 350, ySassy = 240;
    private int xChocolate = 370, yChocolate = 240;
    private boolean takeFood = false;
    private boolean sendFood = false;
	public boolean isPresent;
	
	ImageIcon img = new ImageIcon(this.getClass().getResource("worker.png"));
	Image image = img.getImage();
    
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
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == xmi) & (yDestination == ymi)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == xri) & (yDestination == yri)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == xgi) & (yDestination == ygi)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == xmoi) & (yDestination == ymoi)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == xBelgium) & (yDestination == yBelgium)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == xSassy) & (yDestination == ySassy)) {
        	takeFood = false;
           agent.msgAtTable();

        }
        if (xPos == xDestination &&takeFood&& yPos == yDestination
        		& (xDestination == xChocolate) & (yDestination == yChocolate)) {
        	takeFood = false;
           agent.msgAtTable();

        }
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		g.drawImage(image, xPos, yPos, distance, distance, null);
        g.setColor(Color.BLACK);
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
		
		if(type == "Mint Chip Ice Cream"){
			xDestination = xmi;
			yDestination = ymi;
		}	
		if(type == "Rocky Road Ice Cream"){
			xDestination = xri;
			yDestination = yri;
		}
		if(type == "Green Tea Ice Cream"){
			xDestination = xgi;
			yDestination = ygi;
		}
		if(type == "Mocha Almond Fudge Ice Cream"){
			xDestination = xmoi;
			yDestination = ymoi;
		}
		if(type == "Belgium"){
			xDestination = xBelgium;
			yDestination = yBelgium;
		}
		if(type == "Sassy"){
			xDestination = xSassy;
			yDestination = ySassy;
		}
		if(type == "Chocolate"){
			xDestination = xChocolate;
			yDestination = yChocolate;
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
