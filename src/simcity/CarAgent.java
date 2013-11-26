package simcity;
import java.util.List;
import java.util.concurrent.Semaphore;

import simcity.astar.AStarNode;
import simcity.astar.AStarTraversal;
import simcity.astar.Position;
import agent.Agent; 
import simcity.gui.CarGui; 



//to use: create CarAgent with aStarTraversal as parameter as well as Person Agent
//Create gui with original starting position
//gotoposition(int x, int y); 
//gui will disappear once he reaches destination and will send message back to person
//msgatLocation(int x, int y); 
//to make him reappear setatPosition(int x, int y); 
//gotoposition (int x, int y); 

//Break this sequence and you will screw up his disappearing/reappearing
public class CarAgent extends Agent {
	Position currentPosition; 
	Position originalPosition;
	AStarTraversal aStar; 
	int scale = 20; 
	int heightofStreet = 20; 
	public CarGui myGui; 
	private Semaphore atSlot = new Semaphore(0,true);
	
	public enum CarState {goTo, moving, arrived}
	private CarState carstate; 
	
	int destinationX; 
	int destinationY; 
	
	public CarAgent(AStarTraversal a){
		aStar = a; 

	}
	
	public void setGui(CarGui c){
		myGui = c; 
		System.out.println("Cargui location is " +myGui.xPos/scale + " " + myGui.yPos/scale);
		currentPosition = new Position(myGui.xPos/scale, myGui.yPos/scale);
        currentPosition.moveInto(aStar.getGrid());
        originalPosition = currentPosition;
	}
	
	public void msgatSlot(){
		atSlot.release();
		System.out.println("at Slot released "+ atSlot.availablePermits());
	}
	
	public void msgatDestination(){
		//SOME SORT OF THING HERE FOR PERSON TO KNOW HE IS THERE
	}

	
	public void setatPosition(int x, int y){
	    currentPosition.release(aStar.getGrid());
		currentPosition = new Position(x/scale, y/scale);
        currentPosition.moveInto(aStar.getGrid());
        originalPosition = currentPosition;
        
        int numx = x/scale; 
		numx = numx*scale; 
		
		int numy = y/scale; 
		numy = numy*scale; 
        
        myGui.atPosition(numx, numy);
	}
	
	public void gotoPosition(int x, int y){
		destinationX = x; 
		destinationY = y; 
		int num = x/scale; 
		num = num*scale; 
		myGui.overallX = num; 

		num = y/scale; 
		num = num*scale; 
		myGui.overallY = num; 
		System.out.println("overall is "+myGui.overallX +" , "+ myGui.overallY);

		carstate = CarState.goTo;  
		stateChanged();	
	}
	
	public void dogoto(){
		carstate = CarState.moving; 
    	guiMoveFromCurrentPositionTo(new Position((destinationX/scale),(destinationY/scale)));
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
	    myGui.moveto(currentPosition.getX(), currentPosition.getY());
		try {
			atSlot.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
    }
    
    public void deadPos(int x, int y){
	    currentPosition.release(aStar.getGrid());
		currentPosition = new Position(x/scale, y/scale);
        currentPosition.moveInto(aStar.getGrid());
        originalPosition = currentPosition;
    }

	
    protected boolean pickAndExecuteAnAction(){
    	if (carstate == CarState.goTo){
    		dogoto();
    		return true; 
    	}
    	return false; 
    }
	
}