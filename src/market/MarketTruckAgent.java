package market;
import java.util.List;
import java.util.concurrent.Semaphore;

import market.gui.*;
import market.interfaces.MarketCashier;
import market.interfaces.MarketTruck;
import simcity.CarAgent.CarState;
import simcity.astar.AStarNode;
import simcity.astar.AStarTraversal;
import simcity.astar.Position;
import agent.Agent; 
import simcity.gui.CarGui; 
import restaurant1.Restaurant1CookRole;
import restaurant1.interfaces.Cook;


public class MarketTruckAgent extends Agent implements MarketTruck{
	private MarketCustomerRole agent = null;
	private int xDestination = 140, yDestination = 240;
	Cook cook;
	List<Food> foodlist;
	MarketCashier cashier;
	MarketTruckGui myGui;
	private Semaphore atDes = new Semaphore(0,true);
	
	public enum state {collecting, sending, readytoback, back};
	public state s;


	
	public void setCashier(MarketCashier cashier){
		this.cashier = cashier;
	}
	
	public void setGui(MarketTruckGui c){
		myGui = c; 
		
	}

	
	public void gotoPosition(Cook c, List<Food> food, int dx, int dy){
		
	
		this.cook = c;
		foodlist = food;
		s= state.collecting;
		stateChanged();	
	}
	
	public void msgGoBack(){
		s = state.readytoback;
	   
		stateChanged();	
	}
	
	public void msgrelease(){
		atDes.release();
	}

    
    public boolean pickAndExecuteAnAction(){
        if (s == state.collecting){
                GoToCook();
                return true;
        }
        if(s == state.readytoback){
        	Goback();
        	return true;
        }
        return false;
}

    
    public void GoToCook(){
    	myGui.GotoCook();
    	s = state.sending;
    	try {
			atDes.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	cook.msgHereisYourFood(this, foodlist);
    }
    	
    public void Goback(){
    	myGui.GoBack();
    	s = state.back;
      	try {
    			atDes.acquire();
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
      	cashier.msgTruckBack(this);
    }
    
}
