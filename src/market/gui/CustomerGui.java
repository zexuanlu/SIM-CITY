package market.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import market.CustomerAgent;

public class CustomerGui implements Gui {
	
	private CustomerAgent agent = null;
	private int xPos = 220, yPos = -20;
	private int xDestination = 220, yDestination = 300;
	private int distance = 20;
	private int xTable =220, yTable = 300;
	private boolean atTable = true;
	
	public void setAgent(CustomerAgent agent){
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
		xDestination = 50;
		if(number == 1){
			yDestination = 100;
			
		}
		if(number == 2){
			yDestination = 140;
			
		}
		if(number == 3){
			yDestination = 180;
			
		}
		if(number == 4){
			yDestination = 220;
			
		}
		if(number == 5){
			yDestination = 260;
			
		}
		if(number == 6){
			yDestination = 300;
			
		}
	}
	
	public void DoGoToTable(){
		xDestination = 220;
		yDestination = 300;
		atTable = true;
	}

	public void DoLeave(){
		xDestination = 220;
		yDestination = -20;
	}
	
}
