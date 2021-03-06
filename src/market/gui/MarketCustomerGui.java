package market.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

import person.PersonAgent;
import utilities.Gui;
import market.MarketCustomerRole;

public class MarketCustomerGui implements Gui {
	
	private MarketCustomerRole agent = null;
	private int xPos = -20, yPos = 240;
	private int xDestination = 140, yDestination = 240;
	private int distance = 20;
	private int xTable =140, yTable = 240;
	private boolean atTable = false;
	private boolean isPresent;
	
	ImageIcon img = new ImageIcon(this.getClass().getResource("customer.png"));
	Image image = img.getImage();
	
	public MarketCustomerGui(MarketCustomerRole agent){
		this.agent = agent;
		isPresent = false;
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
        
        if (xPos == -20 && yPos == 240
        		& (xDestination == -20) & (yDestination == 240 )) {
        	agent.msgDoneLeaving();
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
	
	public void setPresent(boolean b){
		isPresent = b;
	}
	
}
