package restaurant3;

import agent.Agent;
import restaurant3.interfaces.Waiter;
import restaurant3.interfaces.Cook;
import restaurant3.gui.Restaurant3CookGui;

import java.util.*;
import java.util.concurrent.Semaphore;

public class Restaurant3CookRole extends Agent implements Cook{
	//MEMBER DATA
	String name;
	
	//GUI references
	Restaurant3CookGui cookGui;
	
	//Enum to keep track of order state
	public enum oState {pending, cooking, cooked};
	
	//Private class for order information
	private class Order{
		String choice;
		Waiter wtr;
		int tableNum;
		oState state;
		
		Order(Waiter w, int tNum, String ch){
			wtr = w;
			tableNum = tNum;
			choice = ch;
			state = oState.pending;
		}
	}
	
	//Private class for food information
	private class Food{
		String type;
		int cookTime;
		int amount;
		int max;
		int numToOrder;
		
		Food(String t, int cT, int amt, int mx){
			type = t;
			cookTime = cT*1000;
			amount = amt;
			max = mx;
		}
	}
	
	//Order list
	List<Order> orders = new ArrayList<Order>();
	
	//Food inventory
	Map<String, Food> food = new HashMap<String, Food>();
	
	//Semaphore for animation
	private Semaphore atFr = new Semaphore(0, true);
	
	//Timer for cooking
	Timer cookTimer = new Timer();
	
	//CONSTRUCTOR *****************************************
	public Restaurant3CookRole(String name) {
		super();
		this.name = name;
		
		//Initialize inventory
		food.put("Steak", new Food("Steak", 4, 10, 10));
		food.put("Pizza", new Food("Pizza", 3, 10, 10));
		food.put("Chicken", new Food("Chicken", 2, 10, 10));
		food.put("Salad", new Food("Salad", 1, 10, 10));
	}
	
	//HELPER METHODS **************************************
	public String getName(){
		return name;
	}
	
	public void setGui(Restaurant3CookGui ckg){
		cookGui = ckg;
	}

	//MESSAGES ********************************************
	@Override
	public void msgNewOrder(Waiter w, int table, String choice) {
		print(name + ": received new order from waiter " + w.getName());
		orders.add(new Order(w, table,choice));
		stateChanged();
	}

	public void msgAtFrRelease(){
		atFr.release();
	}
	
	//SCHEDULER *****************************************
	@Override
	protected boolean pickAndExecuteAnAction() {
			//Check if an order is pending
			for(Order o : orders){
				if(o.state == oState.pending){
					cookOrder(o);
					return true;
				}
			}	
			//Check if an order is cooked
			for(Order o : orders){
				if(o.state == oState.cooked){
					sendOrderToTable(o);
					return true;
				}
			}
		return false;
	}

	//ACTIONS *************************************
	public void cookOrder(final Order o){
		print(name + " cooking order for table " + o.tableNum);
		o.state = oState.cooking;
		cookGui.DoGoToFridge();	//GUI CODE
		atFr.drainPermits();
		try{
			atFr.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		//NON NORM CODE HERE
		cookGui.DoGoToGrill();	//GUI CODE
		atFr.drainPermits();
		try{
			atFr.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}

		cookTimer.schedule(new TimerTask(){
			public void run(){
				o.state = oState.cooked;
				stateChanged();
			}
		}, food.get(o.choice).cookTime);
	}
	
	public void sendOrderToTable(Order o){
		print(name + ": order for table " + o.tableNum + " cooked. Sending out.");
		cookGui.DoGoToOrderStand();	//GUI CODE
		atFr.drainPermits();
		try{
			atFr.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		orders.remove(o);
		o.wtr.msgOrderReady(o.tableNum, o.choice);
	}
}
