package restaurant2.gui;


import restaurant2.Restaurant2CustomerRole;
import restaurant2.Restaurant2HostRole;
import utilities.Gui;

import java.awt.*;

public class Restaurant2HostGui implements Gui {

    private Restaurant2HostRole agent = null;

    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position

    public static final int xTable = 200;
    public static final int yTable = 250;
    public static final int xTable2 = 300;
    public static final int yTable2 = 300;
    public static final int xTable3 = 50;
    public static final int yTable3 = 50;
    
    public Restaurant2HostGui(Restaurant2HostRole agent) {
        this.agent = agent;
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
        		& ((xDestination == xTable + 20) & (yDestination == yTable - 20) ||(xDestination == xTable2 + 20) & (yDestination == yTable2 - 20) || (xDestination == xTable3 + 20) & (yDestination == yTable3 - 20) ) ) {
           agent.msgAtTable();
        }
        if(xPos == -20 && yPos == -20)
        {
        	agent.msgBackHome();
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, 20, 20);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(Restaurant2CustomerRole customer, int table) {
    	System.out.println("seating"+ customer+" at" + table);
    	if(table == 1){
    		xDestination = xTable + 20;
    		yDestination = yTable - 20;
    	}
    	else if(table == 2){
    		xDestination = xTable2+20;
    		yDestination = yTable2-20;
    	}
    	else if(table == 3){
    		xDestination = xTable3+20;
    		yDestination = yTable3-20;    		
    	}
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
