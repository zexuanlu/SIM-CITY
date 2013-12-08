package restaurant1.gui;

import restaurant1.Restaurant1SDWaiterRole;
import restaurant1.Restaurant1WaiterRole;
import restaurant1.interfaces.Restaurant1Customer;
import restaurant1.interfaces.Restaurant1Waiter;
import simcity.gui.*;
import utilities.Gui;

import java.awt.*;

import person.PersonAgent;

public class Restaurant1WaiterGui implements Gui {
	

    private Restaurant1Waiter agent = null;
    public boolean isPresent = false;
    private int countNumber = 0;
    private int customernumber = 0;
    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = 50, yDestination = 50;//default start position
    private int origin = 40;
    private int chomeposition = 20;
    private int distance= 20;
	private int xfood = 540;
	private int yfood = 250;
    private String order = "";
    private String food = "";
    private boolean go = false;
    private static final int cookX = 520;
    private static final int cookY = 230;
    public static final int xTable = 200;
    public static final int yTable = 250;
    public static final int x1Table = 300;
    public static final int y1Table = 150;
    
    public Restaurant1WaiterGui(Restaurant1WaiterRole agent) {
        this.agent = agent;
    }
    
    public Restaurant1WaiterGui(Restaurant1SDWaiterRole agent) {
        this.agent = agent;
    }
    
    public void setOrigion(int number){
    	countNumber = number;
    	yPos = origin;
    	xPos = countNumber * origin;
    	xDestination = countNumber * origin;
    	yDestination = origin;
    }
    
    public void stop(){
    	
    }
    
    public void zou(){
    	
    }

    public void updatePosition() {
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos =xPos - 2;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos = yPos - 2;
        
        if (xPos == (customernumber * origin + distance) && yPos == chomeposition){
        	agent.msgAtTable();
        }
        
        if (xPos == xDestination &&go && yPos == yDestination
        		& (xDestination == xTable + distance) & (yDestination == yTable - distance)) {
        	
           agent.msgAtTable();
           go = false;

        }
        
        if (xPos == xDestination  &&go && yPos == yDestination
        		& (xDestination == x1Table + distance) & (yDestination == yTable - distance)) {

           agent.msgAtTable();
           go = false;

        }
        
        if (xPos == xDestination &&go&& yPos == yDestination
        		& (xDestination == x1Table + distance) & (yDestination == y1Table - distance)) {

           agent.msgAtTable();
           go = false;

        } 
        
        if (xPos == cookX && yPos == cookY) {
        	agent.msgatCook();
        }
        

        if(xPos == countNumber * origin && yPos == origin){
        	agent.msgIsback();
        }
        
        if(xPos == 520 && yPos == 230){

        	agent.msgAtTable();
        	
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, distance, distance);
        if(agent instanceof Restaurant1WaiterRole){
        	g.drawString(((PersonAgent)((Restaurant1WaiterRole)agent).getPerson()).getName(), xPos-14, yPos+30);
        }
        else if(agent instanceof Restaurant1SDWaiterRole){
        	g.drawString(((PersonAgent)((Restaurant1SDWaiterRole)agent).getPerson()).getName(), xPos-14, yPos+30);
        }
        g.drawString(order, xPos, yPos);
        g.drawString(food, xfood, yfood);
    }
    
    public void animationBringFood(String a){
    	order = a;
    }

    public void bringFoodDone(){
    	order = "";
    }
    
    public boolean isPresent() {
        return isPresent;
    }
    public void setPresent(boolean tf){
    	isPresent = tf;
    }
    public void DoGotoCHomePosition(int number){
    	customernumber = number;
    	xDestination = number * origin + distance;
    	yDestination = chomeposition;
    }
    
    public void DoBringToTable(int seattable) {
    	if(seattable == 1){
        xDestination = xTable + distance;
        yDestination = yTable - distance;
    	}
    	else if(seattable ==2 ){
    		xDestination = x1Table + distance;
            yDestination = yTable - distance;	
    	}
    	else if(seattable == 3){
    		xDestination = x1Table + distance;
            yDestination = y1Table - distance;	
    	}
    	go = true;

    }

    public void DoGoToTakeOrder(Restaurant1Customer c, int seattable) {
    	//customer = c;
    	if(seattable == 1){
        xDestination = xTable + distance;
        yDestination = yTable - distance;
    	}
    	else if(seattable ==2 ){
    		xDestination = x1Table + distance;
            yDestination = yTable - distance;	
    	}
    	else if(seattable == 3){
    		xDestination = x1Table + distance;
            yDestination = y1Table - distance;	
    	}
    	go = true;

    }
    
    public void DoBackToTable(Restaurant1Customer c,int seattable) {
    	if(seattable == 1){
        xDestination = xTable + distance;
        yDestination = yTable - distance;
    	}
    	else if(seattable ==2 ){
    		xDestination = x1Table + distance;
            yDestination = yTable - distance;	
    	}
    	else if(seattable == 3){
    		xDestination = x1Table + distance;
            yDestination = y1Table - distance;	
    	}
    	go = true;
    }


    
    
    public void DoLeaveCustomer() {
    	xDestination = countNumber * origin;
    	yDestination = origin;
    }
    
    public void Dogotocook(){
    	xDestination = cookX;
    	yDestination = cookY;
    }
    
    public void showfood(String order){
    	food = order;
    }
    
    public void hidefood(){
    	food = "";
    }
    
    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
