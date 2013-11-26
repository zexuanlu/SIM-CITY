package market.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import agent.Gui;
import market.MarketCustomerRole;

public class MarketCustomerGui implements Gui {
	
	private MarketCustomerRole agent = null;
	private int xPos = -20, yPos = 240;
	private int xDestination = 140, yDestination = 240;
	private int distance = 20;
	private int xTable =140, yTable = 240;
	private boolean atTable = false;
	
	public MarketCustomerGui(MarketCustomerRole agent){
		this.agent = agent;
	}
	
	@Override
	public void updatePosition() {
		// TODO Auto-generated method stub
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;
		
		 
        if (xPos == xDestination &&atTable&& yPos == yDestination
        		& (xDestination == xTable) & (yDestination == yTable )) {
        	atTable = false;
           agent.msgAtTable();
        }

	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, distance, distance);
	}

	@Override
	public boolean isPresent() {
		// TODO Auto-generated method stub
		return true;
	}

	
	public void DoGoToWaitingArea(int number){
		yDestination = 380;
		if(number == 1){
			xDestination = 60;
			
		}
		if(number == 2){
			xDestination = 100;
			
		}
		if(number == 3){
			xDestination = 140;
			
		}
		if(number == 4){
			xDestination = 180;
			
		}
		if(number == 5){
			xDestination = 220;
			
		}
		if(number == 6){
			xDestination = 260;
			
		}
		if(number == 7){
			xDestination = 300;
			
		}
		if(number == 8){
			xDestination = 340;
			
		}
		if(number == 9){
			xDestination = 380;
			
		}
	}
	
	public void DoGoToTable(){
		xDestination = 140;
		yDestination = 240;
		atTable = true;
	}

	public void DoLeave(){
		xDestination = -20;
		yDestination = 240;
	}
	
}
