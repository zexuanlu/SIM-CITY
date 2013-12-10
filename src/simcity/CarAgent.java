package simcity;
import java.util.List;
import java.util.concurrent.Semaphore;





//import person.Position;
import simcity.astar.AStarNode;
import person.PersonAgent; 
import simcity.astar.AStarTraversal;
import simcity.astar.Position;
import agent.Agent; 
import simcity.gui.CarGui; 
import utilities.TrafficLightAgent;

//CAR AGENT HAS NO TEST SINCE NO WAY TO TEST IT WITHOUT GUI


//to use: create CarAgent with aStarTraversal as parameter as well as Person Agent
//Create gui with original starting position
//gotoposition(int x, int y); 
//gui will disappear once he reaches destination and will send message back to person
//msgatLocation(int x, int y); 
//to make him reappear setatPosition(int x, int y); 
//gotoposition (int x, int y); 

//Break this sequence and you will screw up his disappearing/reappearing
public class CarAgent extends Agent {
	int WIDTHTOTAL = 740; 
	int HEIGHTTOTAL = 480; 
	private TrafficLightAgent trafficlightagent; 

	 public int percentCrash = 20; 
	 boolean crashed = false; 
	 Position currentPosition;
     Position originalPosition;
     AStarTraversal aStar;
     int scale = 20;
     String name; 
     int heightofStreet = 20;
     public CarGui myGui;
 	private Semaphore greenLight = new Semaphore(0,true);
     private Semaphore atSlot = new Semaphore(0,true);
     
     public enum CarState {goTo, moving, arrived}
     private CarState carstate;
     
     int destinationX;
     int destinationY;
     public PersonAgent myPerson; 
     public CarAgent(AStarTraversal a, PersonAgent mp){
             aStar = a;
             myPerson = mp;
             name = myPerson.toString(); 

     }
     
     public void setGui(CarGui c){
             myGui = c;
             System.out.println("Cargui location is " +myGui.xPos/scale + " " + myGui.yPos/scale);
             currentPosition = new Position(myGui.xPos/scale, myGui.yPos/scale);
             currentPosition.moveInto(aStar.getOrigGrid());
             originalPosition = currentPosition;
     }
     
     public void msgatSlot(){
             atSlot.release();
     }
     
     
 	public void msgLightGreen(){
		print("GREEN LIGHT RELEASED");
		greenLight.release();
	}
	
     
     public void msgatDestination(){ 	
    	person.Position p; 
    	 
    	 
 		int tempX = 0; 
		int tempY = 0; 

		//split into quadrants 
		if (myGui.xPos <= WIDTHTOTAL/2){ //leftside
			if (myGui.yPos<=HEIGHTTOTAL/2){//topleft
				tempX = Math.abs(myGui.xPos - 340);
				tempY = Math.abs(myGui.yPos - 180);
				if (tempX < tempY){ //on vertical road
					p =  new person.Position(330, myGui.yPos);
				}
				else { //on horizontal road
					p =  new person.Position(myGui.xPos, 170);
				}
			}
			else { //bottomleft
				tempX = Math.abs(myGui.xPos - 340);
				tempY = Math.abs(myGui.yPos - 280);
				if (tempX < tempY){ //on vertical road
					p = new person.Position(330, myGui.yPos);
				}
				else { //on horizontal road
					p = new person.Position(myGui.xPos, 280);
				}
				
			}
		}
		else {//rightside
			if (myGui.yPos<=HEIGHTTOTAL/2){//topright
				tempX = Math.abs(myGui.xPos - 440);
				tempY = Math.abs(myGui.yPos - 180);
				if (tempX < tempY){ //on vertical road
					p = new person.Position(440, myGui.yPos);
				}
				else { //on horizontal road
					p =  new person.Position(myGui.xPos, 170);
				}
			}
			else { //bottomright
				tempX = Math.abs(myGui.xPos - 440);
				tempY = Math.abs(myGui.yPos - 280);
				if (tempX < tempY){ //on vertical road
					p = new person.Position(440, myGui.yPos);
				}
				else { //on horizontal road
					p = new person.Position(myGui.xPos, 280);
				}
			}
		}
				
 	
 		System.err.println(myPerson.getName()+" getting off at: ("+p.getX()+" , "+p.getY()+")");
 		myPerson.msgAtDest(p, this);//(new person.Position(myGui.xPos, myGui.yPos),this);
 	}

     
     public void setatPosition(int originx, int originy){
      currentPosition.release(aStar.getOrigGrid());
             currentPosition = new Position(originx/scale, originy/scale);
     currentPosition.moveInto(aStar.getOrigGrid());
     originalPosition = currentPosition;
     
     		int numx = originx/scale;
             numx = numx*scale;
             
             int numy = originy/scale;
             numy = numy*scale;
             
             myGui.atPosition(numx, numy);
     }
     
     public void gotoPosition(int originx, int originy, int x, int y){
    	 print ("gotoposition");
    	 
    	 if ((originx/scale == x/scale) && (originy/scale == y/scale)){
    		 msgatDestination();
    		 return; 
    	 }
    	 
         currentPosition.release(aStar.getOrigGrid());
         currentPosition = new Position(originx/scale, originy/scale);
    //     currentPosition.moveInto(aStar.getOrigGrid());
         originalPosition = currentPosition;
 
         int numx = originx/scale;
         numx = numx*scale;
         
         int numy = originy/scale;
         numy = numy*scale;
 
         myGui.atPosition(numx, numy);
    	 
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
     Boolean firstStep = true;
     Boolean gotPermit = true;

     for (Position tmpPath: path) {
      //The first node in the path is the current node. So skip it.
      if (firstStep) {
             firstStep = false;
             continue;
      }

      //Try and get lock for the next step.
      int attempts = 1;
      gotPermit = new Position(tmpPath.getX(), tmpPath.getY()).moveInto(aStar.getOrigGrid());

      //Did not get lock. Lets make n attempts.
      while (!gotPermit && attempts < 5) {
             //System.out.println("[Gaut] " + guiWaiter.getName() + " got NO permit for " + tmpPath.toString() + " on attempt " + attempts);

             //Wait for 1sec and try again to get lock.          
          double chancecrash = Math.random() % 100;
          print ("chancecrash " + chancecrash);
          if (chancecrash <= percentCrash/100){  
        	  crashed = true; 
              myGui.Collide();
              myPerson.crashed();

                 try { Thread.sleep(1000); }
                 
                 catch (Exception e){}

//                 gotPermit = new Position(tmpPath.getX(), tmpPath.getY()).moveInto(aStar.getOrigGrid());
                 myGui.gotoDeadPos();
                 return; 	    
          }
          else {
              try { Thread.sleep(1000); } 
              catch (Exception e){}
              attempts ++;
    		  gotPermit   = new Position(tmpPath.getX(), tmpPath.getY()).moveInto(aStar.getOrigGrid());
         //     aStar.crashed();
              
          }
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
      currentPosition.release(aStar.getOrigGrid());
      currentPosition = new Position(tmpPath.getX(), tmpPath.getY ());
      myGui.moveto(currentPosition.getX(), currentPosition.getY());
      
      
      
      if (trafficlightagent != null){
	    if (currentPosition.getX()==16 && (currentPosition.getY()==12 || currentPosition.getY()==13)){
	    	trafficlightagent.msgCheckLight(this,  myGui.xPos, myGui.yPos);
	    	try {
				greenLight.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
  	}
  
	    else if (currentPosition.getX()==22 && (currentPosition.getY()==9 || currentPosition.getY()==10)){
	    	trafficlightagent.msgCheckLight(this,  myGui.xPos, myGui.yPos);
	    	try {
				greenLight.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    else if (currentPosition.getY()==8 && (currentPosition.getX()==17 || currentPosition.getX()==18)){
	    	trafficlightagent.msgCheckLight(this,  myGui.xPos, myGui.yPos);
	    	try {
				greenLight.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    else if (currentPosition.getY()==14 && (currentPosition.getX()==20 || currentPosition.getX()==21)){
	    	trafficlightagent.msgCheckLight(this,  myGui.xPos, myGui.yPos);
	    	try {
				greenLight.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
      }
	    
	    
      
      
      
      
      
             try {
                     atSlot.acquire();
             } catch (InterruptedException e) {
                     e.printStackTrace();
             }
             }
 }
 
 public void deadPos(int x, int y){
      currentPosition.release(aStar.getOrigGrid());
      currentPosition = new Position(x/scale, y/scale);
     currentPosition.moveInto(aStar.getOrigGrid());
     originalPosition = currentPosition;
 }
 
 protected boolean pickAndExecuteAnAction(){
         if (carstate == CarState.goTo){
                 dogoto();
                 return true;
         }
         return false;
 }

 public void setTrafficLightAgent(TrafficLightAgent tla){
 	trafficlightagent = tla; 
 }
 
 
 public String toString(){
	 return name;
 }
}


