package restaurant5.gui;


import restaurant5.CustomerAgent5;
import restaurant5.HostAgent5;
import utilities.Gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HostGui5 implements Gui {

    private HostAgent5 agent = null;

    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position

    public static int xTable = 200;
    public static int yTable = 250;
	private List<Table5> tables;

   
    //much of this code is no longer in use because the Waiter now handles most of this movement
    //nonetheless I kept this here in case we had to do stuff with it in V 2.1 or something
    
    public HostGui5(HostAgent5 agent) {
        this.agent = agent;
        tables = agent.getTables(); 
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
        //   agent.msgAtTable();
        }
        if (xPos == -20 && yPos == -20){
        	//agent.msgatOrigin();
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, 20, 20);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(CustomerAgent5 customer) {
    	int test = 0;
    	for (Table5 x: tables){
    		if (x.isOccupied()){
    			test++;
    		}
    	}
    	
    	if (test == 0){
    		xTable = 200;
    	}
    	if (test == 1){
    		xTable = 300;
    	}
    	if (test == 2){
    		xTable = 400;
    	}
    	
        xDestination = xTable + 20;
        yDestination = yTable - 20;
    }

    public void DoLeaveCustomer() {
        xDestination = -20;
        yDestination = -20;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
