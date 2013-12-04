package restaurant4.gui;


import resident.gui.HomeOwnerGui.HomeCookingState;
import restaurant4.Restaurant4CustomerRole;
import restaurant4.Restaurant4WaiterRole;
import agent.Gui;

import java.awt.*;

/**
 * This class represents the waiters
 * in the restaurant
 */
public class Restaurant4WaiterGui implements Gui {

    private Restaurant4WaiterRole agent = null;
    Restaurant4AnimationPanel gui;
    private boolean tired = false;
    private GUIstate s = GUIstate.None;
    public enum GUIstate {None, CarryingFood}
    private String choice;

    private int xPos, yPos;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position

    public int xHome;
    public int yHome;
    public int xCust, yCust;
    public int xGrill, yGrill;
    public static final int xTable = 50;
    public static final int yTable = 200;
    public static final int xCook = 340;
    public static final int yCook = 150;
    public static final int xCashier = 100;
    public static final int yCashier = -20;
    public static final int xHost = -20;
    public static final int yHost = -20;
    
    private int tableNum;

    public Restaurant4WaiterGui(Restaurant4WaiterRole agent, Restaurant4AnimationPanel gui, int x, int y) {
        this.agent = agent;
        this.gui = gui;
        xHome = x;
        yHome = y;
        xDestination = x;
        yDestination = y;
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
        		& (xDestination == xHost) & (yDestination == yHost)){
        	agent.msgAtHost();
        	xDestination++;
        }
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xCust) & (yDestination == yCust)){
        	agent.msgAtCust();
        	yDestination++;
        }
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable + (100 * (tableNum - 1)) + 20) & (yDestination == yTable - 20)) {
           agent.msgAtTable();
           s = GUIstate.None;
           xDestination++;
        }
        if(xPos == xDestination && yPos == yDestination
        		& (xDestination == xCook) & (yDestination == yCook)) {
        	agent.msgAtCook();
        	xDestination--;
        }
        if(xPos == xDestination && yPos == yDestination
        		& (xDestination == xCashier) & (yDestination == yCashier)) {
        	agent.msgAtCashier();
        	xDestination--;
        }
        if(xPos == xDestination && yPos == yDestination
        		& (xDestination == xGrill) & (yDestination == yGrill)) {
        	agent.msgAtCook();
        	xDestination--;
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, 20, 20);
        
        String foodChoice = null;
        
        if (s == GUIstate.CarryingFood) {
        	g.setColor(Color.BLACK);
    		
    		foodChoice = choice.substring(0, 2);
    		g.drawString(foodChoice, xPos, yPos-10);
        }
    }

    public boolean isPresent() {
        return true;
    }

    public void DoGoToCust(int x, int y){
    	xCust = x+20;
    	yCust = y+20;
    	xDestination = xCust;
    	yDestination = yCust;
    }
    public void DoBringToTable(Restaurant4CustomerRole customer, int tableNum){
    	this.tableNum = tableNum;
        xDestination = xTable + (100*(tableNum-1)) + 20;
        yDestination = yTable - 20;
    }
    
    public void DoGoToTable(int tableNum){
    	this.tableNum = tableNum;
    	xDestination = xTable + (100*(tableNum-1)) + 20;
    	yDestination = yTable - 20;
    }

    public void DoLeaveCustomer() {
        xDestination = xHome;
        yDestination = yHome;
    }
    
    public void DoGoToCook() {
    	xDestination = xCook;
    	yDestination = yCook;
    }
    
    public void DoGoToCashier() {
    	xDestination = xCashier;
    	yDestination = yCashier;
    }
    
    public void DoBringFoodToTable(int tableNum, String item){
    	this.tableNum = tableNum;
    	s = GUIstate.CarryingFood;
    	choice = item;
    	xDestination = xTable + (100*(tableNum-1)) + 20;
    	yDestination = yTable - 20;
    }

    public void DoGoHome(){
    	xDestination = xHome;
    	yDestination = yHome;
    }
    
    public void DoGoToGrill(int grillNum){
    	xGrill = 340;
    	yGrill = 22 + 20*grillNum;
    	xDestination = xGrill;
    	yDestination = yGrill;
    }
    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    
    public boolean isTired() {
    	return tired;
    }
    public void noBreak() {
    	tired = false;
    }
    public void goOnBreak() {
    	xDestination = -21;
    	yDestination = -21;
    }
    public void setTired() {
    	if(tired){
    		tired = false;
    		agent.msgEndOfBreak();
    	}
    	else{
    		tired = true;
    		agent.msgWantToBreak();
    	}
    }
}
