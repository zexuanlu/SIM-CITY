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
	private int restnum;
	private int trucknum;
	RestaurantCook cook;
	List<Food> foodlist;
	MarketCashier cashier;
	MarketTruckGui myGui;
	private Semaphore atDes = new Semaphore(0,true);
	private List<myRestaurant> restaurant = new ArrayList<myRestaurant>();
	private Map<Integer, CityMap> cityMap = new HashMap<Integer, CityMap>();
	
	public enum state {collecting, sending, readytoback, back};
	public state s;

	public MarketTruckAgent(int trucknum){
		this.trucknum = trucknum;
		cityMap.put(1, new CityMap(220, 90));
		cityMap.put(2, new CityMap(250, 90));
		cityMap.put(3, new CityMap(250, 90));
		cityMap.put(4, new CityMap(540, 90));
		cityMap.put(5, new CityMap(610, 90));
		cityMap.put(6, new CityMap(510, 60));
	}
	
	private class myRestaurant{
		Restaurant r;
		int number;
		
		myRestaurant(Restaurant r, int number){
			this.r = r;
			this.number = number;
		}
	}
	
	private class CityMap{
		int x;
		int y;
		
		CityMap(int x, int y){
			this.x = x;
			this.y = y;
		}
	}
	
	public void setCashier(MarketCashier cashier){
		this.cashier = cashier;
	}
	
	public void setGui(MarketTruckGui c){
		myGui = c; 
		
	}

	
	public void setRestaurant(Restaurant rest, int number){  ////////set restaurant!!!!!!!           do it in simcity gui!!!!!!
		restaurant.add(new myRestaurant(rest,number));
	}
	
	public void gotoPosition(RestaurantCook c, List<Food> food, int restaurantnum){
		
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
    	myGui.GotoCook(cityMap.get(restnum).x,cityMap.get(restnum).y);
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
    				myGui.GoBack(trucknum);
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
    	myGui.GoBack(trucknum);
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
