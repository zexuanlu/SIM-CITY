package simcity.gui;

import simcity.BusRole; 
import simcity.astar.*; 

import java.util.*; 
import java.awt.Color;
import java.awt.*; 
import java.util.List; 
import java.awt.Graphics2D;

public class BusGui implements Gui {
	List<Dimension> moves = new ArrayList<Dimension>();
	private BusRole myBus = null;
	boolean NorthSouth; 
	boolean EastWest; 
	public int xPos, yPos, xDestination, yDestination; 
	
	Position currentPosition; 
	Position originalPosition;
	AStarTraversal aStar; 
	
	public enum movementState {moving,canStop, stopped};
	public enum GuiState {gotoStop, atStop,canStop};
	GuiState guistate; 
	private movementState m; 
	
	public BusGui(BusRole bus, AStarTraversal b){
		myBus = bus; 
		aStar = b; 
		//TEMPORARY TESTING CODE, starts it off at the left corner
		xPos = 0;
		yPos = 0; 
		xDestination = 100;
		yDestination = 100; 
		
		currentPosition = new Position(xPos, yPos);
        currentPosition.moveInto(aStar.getGrid());
        originalPosition = currentPosition;
		}
    
	public void draw(Graphics2D g) {
		g.setColor(Color.ORANGE);
		if (EastWest){
			g.fillRect(xPos, yPos, 30, 50);
		}
		else if (NorthSouth){
			g.fillRect(xPos, yPos, 50, 30);
		}
    }
	
    public boolean isPresent() {
        return true;
    }
    
    public void updatePosition() {
    	//check orientation of the bus
    	if (xPos == xDestination && yPos == yDestination){
    		if (!moves.isEmpty()){
    			moves.remove(0); //remove at index
    			if(!moves.isEmpty()){
	    			xDestination = moves.get(0).width; 
	    			yDestination = moves.get(0).height; 
    			}
    			else{
    				guistate = GuiState.canStop;
    			}
    		}
    	}
    	
    	if (xPos == xDestination && yPos != yDestination){
    		NorthSouth = false; 
    		EastWest = true; //y oriented
    	}
    	else if (yPos == yDestination && xPos != xDestination){
    		EastWest = false; 
    		NorthSouth = true; 
    	}
    	
    	
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;
        
        if (xPos == xDestination && yPos == yDestination && guistate == GuiState.canStop){
        	guistate = GuiState.atStop; 
        	myBus.msgAtStop();
        }
    }
    
    public void GoToBusStop(int x, int y){
    	guistate = GuiState.gotoStop; 
    	guiMoveFromCurrentPositionTo(new Position(x/20,y/20));

    }
    
	//this is just a subroutine for waiter moves. It's not an "Action"
    //itself, it is called by Actions.
    private void guiMoveFromCurrentPositionTo(Position to){

	AStarNode aStarNode = (AStarNode)aStar.generalSearch(currentPosition, to);
	List<Position> path = aStarNode.getPath();
	Boolean firstStep   = true;
	Boolean gotPermit   = true;

	for (Position tmpPath: path) {
	    //The first node in the path is the current node. So skip it.
	    if (firstStep) {
		firstStep   = false;
		continue;
	    }

	    //Try and get lock for the next step.
	    int attempts    = 1;
	    gotPermit       = new Position(tmpPath.getX(), tmpPath.getY()).moveInto(aStar.getGrid());

	    //Did not get lock. Lets make n attempts.
	    while (!gotPermit && attempts < 3) {
		//System.out.println("[Gaut] " + guiWaiter.getName() + " got NO permit for " + tmpPath.toString() + " on attempt " + attempts);

		//Wait for 1sec and try again to get lock.
		try { Thread.sleep(1000); }
		catch (Exception e){}

		gotPermit   = new Position(tmpPath.getX(), tmpPath.getY()).moveInto(aStar.getGrid());
		attempts ++;
	    }

	    //Did not get lock after trying n attempts. So recalculating path.            
	    if (!gotPermit) {
		//System.out.println("[Gaut] " + guiWaiter.getName() + " No Luck even after " + attempts + " attempts! Lets recalculate");
		guiMoveFromCurrentPositionTo(to);
		break;
	    }

	    //Got the required lock. Lets move.
	    //System.out.println("[Gaut] " + guiWaiter.getName() + " got permit for " + tmpPath.toString());
	    currentPosition.release(aStar.getGrid());
	    currentPosition = new Position(tmpPath.getX(), tmpPath.getY ());
	    moveto(currentPosition.getX(), currentPosition.getY());
	}
    }
	
	
	private void moveto(int x, int y){
		moves.add(new Dimension(x*20,y*20));
		xDestination = moves.get(0).width; 
		yDestination = moves.get(0).height; 

	}
    
}
