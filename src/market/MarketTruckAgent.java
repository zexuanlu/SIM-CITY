package market;
import java.util.*;
import java.util.concurrent.Semaphore;

import person.Restaurant;
import market.gui.*;
import market.interfaces.MarketCashier;
import market.interfaces.MarketTruck;
import agent.Agent; 
import utilities.restaurant.RestaurantCook;


public class MarketTruckAgent extends Agent implements MarketTruck{
	private MarketCustomerRole agent = null;
	private int xDestination = 140, yDestination = 240;
	private int restnum;
	RestaurantCook cook;
	List<Food> foodlist;
	MarketCashier cashier;
	MarketTruckGui myGui;
	private Semaphore atDes = new Semaphore(0,true);
	private List<myRestaurant> restaurant = new ArrayList<myRestaurant>();
	
	public enum state {collecting, sending, readytoback, back};
	public state s;

	private class myRestaurant{
		Restaurant r;
		int number;
		
		myRestaurant(Restaurant r, int number){
			this.r = r;
			this.number = number;
		}
	}
	
	public void setCashier(MarketCashier cashier){
		this.cashier = cashier;
	}
	
	public void setGui(MarketTruckGui c){
		myGui = c; 
		
	}

	
	public void setRestaurant(Restaurant rest, int number){
		restaurant.add(new myRestaurant(rest,number));
	}
	
	public void gotoPosition(RestaurantCook c, List<Food> food, int dx, int dy, int restaurantnum){
		
	
		this.cook = c;
		foodlist = food;
		restnum = restaurantnum;
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
    	for(myRestaurant rest: restaurant){
    		if(rest.number == restnum){
    			if(rest.r.isClosed()){
    				myGui.GoBack();
    		      	try {
    	    			atDes.acquire();
    	    		} catch (InterruptedException e) {
    	    			// TODO Auto-generated catch block
    	    			e.printStackTrace();
    	    		}
    				cashier.msgDevliveryFail(this, cook, foodlist, rest.r, rest.number);
    				return;
    			}
    			else{
    		    	cook.msgHereisYourFood(this, foodlist);
    		    	return;
    			}
    		}
    	}

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
