package simcity.gui;
import simcity.BusRole;
import simcity.astar.*; 

import java.util.*; 
import java.awt.*; 
import java.util.List; 
import java.awt.Graphics2D;

public class CarGui implements Gui {
	
	int scale = 20; 
	
	List<Dimension> moves = new ArrayList<Dimension>();
	private BusRole myBus = null;
	boolean NorthSouth; 
	boolean EastWest; 
	public int xPos, yPos, xDestination, yDestination; 
	
	Position currentPosition; 
	Position originalPosition;
	AStarTraversal aStar; 
	
	public enum GuiState {gotoStop, atStop,canStop};
	GuiState guistate; 
	
	CarGui(AStarTraversal a){  //WILL HAVE TO ADD A PERSON LATER PERSON LATER
		aStar = a; 
		EastWest = true; 
	}
	
	public void draw(Graphics2D g) {
		g.setColor(Color.RED);
		if (EastWest){
			g.fillRect(xPos, yPos, 20, 20);
		}
		else if (NorthSouth){
			g.fillRect(xPos, yPos, 20, 20);
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
        	System.out.println("At Place where I'm supposed to be at");
        	//myPerson.msgAtDestination(); 
        }
        
    }
	
    public void moveto(int x, int y){
		moves.add(new Dimension(x*scale,y*scale));
		xDestination = moves.get(0).width; 
		yDestination = moves.get(0).height; 
	}
    
    public void setOriginalPosition (int x, int y){
    	xPos = x; 
    	yPos = y; 
    	xDestination = x+1; 
    	yDestination = y+1; 
    	
    	
		currentPosition = new Position(xPos/scale, yPos/scale);
        currentPosition.moveInto(aStar.getGrid());
        originalPosition = currentPosition;
    }
    
    public void gotoPosition (int x, int y){
    	guistate = GuiState.gotoStop; 
    	guiMoveFromCurrentPositionTo(new Position(x/scale,y/scale));
    }
    
    
    
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
		    	
		    	/////////////ADDED
		    	path.clear();
		    	aStarNode=null; //added later
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
    
    
}
