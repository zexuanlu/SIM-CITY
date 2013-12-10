package restaurant5.gui;


import restaurant1.Restaurant1SDWaiterRole;
import restaurant1.Restaurant1WaiterRole;
import restaurant5.CustomerAgent5;
import restaurant5.SDWaiterAgent5;
import restaurant5.WaiterAgent5;
import restaurant5.WaiterBase5;
import restaurant5.interfaces.Customer5; 
import utilities.Gui;

import java.awt.*;
import java.util.ArrayList;

import person.PersonAgent;

public class WaiterGui5 implements Gui {
	private int waitingX; 
	private int waitingY; 
    private WaiterBase5 agent = null;
    private RestaurantGui gui; 
    public boolean isPresent = false;
    
    ArrayList<myGui> myGuis = new ArrayList<myGui>();
	public enum State {IconOn, IconOff,Brought};

    private class myGui {
    	public FoodGui5 f;
    	public State s; 
    	public CustomerAgent5 c; 
    }
    
    private int custxPos, custyPos; 

    private int xPos, yPos;//default waiter position
    private int xDestination, yDestination;//default start position

    boolean showIcon = false; 
    boolean leaveIcon = false; 
    
    private int standX = 430; 
    private int standY = 20; 
    
    
    public int xTable = 200;
    public int yTable = 250;
    public ArrayList<Table5> tables;

    FoodGui5 food; 
  
    
    public WaiterGui5(WaiterBase5 agent, int x) {
    	waitingX = x; 
    	xPos = waitingX; 
    	xDestination = waitingX; 
    	waitingY = 30; 
    	custxPos = waitingX; 
    	custyPos = waitingY; 
    	yPos = waitingY; 
    	yDestination = waitingY; 
        this.agent = agent;
    }

    public void setGui(RestaurantGui g){
    	gui = g; 
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

        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable + 20) & (yDestination == yTable - 20)) {
           agent.msgAtTable();
        }
        if (xPos == waitingX && yPos == waitingY){
        	agent.msgatOrigin();
        }
        if (xPos == 430 && yPos == 80){
        	agent.msgatCook();
        }
        if (xPos == -20 && yPos == 200){
        	agent.msgatCashier();
        }
        if (xPos == custxPos && yPos == custyPos){
        	agent.msgatCustomer();
        }
        if (xPos == standX && yPos == standY){
        	agent.msgatStand(); 
        }
        
    }
    
    public void IconOn(CustomerAgent5 c, String choice){
    	showIcon = true;
    	food = c.getFoodGui();
        food.setFood(choice);
        food.IconOn();
        
    	myGui temp = new myGui(); 
        temp.f = food; 
    	temp.c = c; 
        temp.s = State.IconOn;
        myGuis.add(temp);

    }
    
    public void IconOff(CustomerAgent5 customer){
    	showIcon = false; 
    	for (myGui m: myGuis){
    		if (m.c == customer){
    			m.f.IconOff();
    			m.s = State.IconOff;
    		}
    	}
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, 20, 20);
        
        if(agent instanceof WaiterAgent5){
        	g.drawString(((PersonAgent)((WaiterAgent5)agent).getPerson()).getName(), xPos-14, yPos+30);
        }
        else if(agent instanceof SDWaiterAgent5){
        	g.drawString(((PersonAgent)((SDWaiterAgent5)agent).getPerson()).getName(), xPos-14, yPos+30);
        }
        }

    public boolean isPresent() {
        return isPresent;
    }
    
    public void DoGotoCustomer(CustomerAgent5 c){
    	xDestination = c.getGui().getXPos()+20; 
    	yDestination = c.getGui().getYPos()+20;
    	custxPos = xDestination; 
    	custyPos = yDestination; 
        
    }

    public void DoBringToTable(CustomerAgent5 customer, int test) {
    	if (test == 1){ 
    		xTable = 200;
    	}
    	if (test == 2){
    		xTable = 300;
    	}
    	if (test == 3){
    		xTable = 400;
    	}
    	
        xDestination = xTable + 20;
        yDestination = yTable - 20;
        
        
    	if (showIcon){
    		for (myGui m: myGuis){
	    			if (m.s == State.IconOn){
		    		food.xPos = xPos;
		        	food.yPos = yPos + 30; 
		            food.xDestination = xDestination; 
		            food.yDestination = yDestination + 30;
		            m.s = State.Brought; 
    			}
    		}
    	}
    }
    
    public void DoGoToStand(){
	  xDestination = standX; 
	  yDestination = standY; 
    }

    public void DoLeaveCustomer() {
        xDestination = waitingX;
        yDestination = waitingY;
    }
    
    public void DoGoToCook(){
    	xDestination = 430;
    	yDestination = 80; 
    }
    
    public void DoGoToCashier(){
    	xDestination = -20; 
    	yDestination = 200; 
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
