package restaurant;

import agent.Agent;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import restaurant.gui.CookGui;
import restaurant.interfaces.Cook;
import restaurant.interfaces.Market;
import restaurant.interfaces.Waiter;
import restaurant.interfaces.Cashier;

import market.interfaces.MarketTruck;
import market.Food;


//Add case in scheduler to tell markettruck to go back

public  class Restaurant1CookRole extends Agent implements Cook {
	private CookGui cookGui = null;
	private Cashier cashier;
	private MarketTruck truck;
	String name;
	boolean opening = false;
	boolean sendTruckBack = false;
	int count = 0;
	public Map<String, MyFood> food = new HashMap<String, MyFood>();
	public List<Food> foodlist = Collections.synchronizedList(new ArrayList<Food>());
	private Semaphore AR = new Semaphore(0,true);
	public List<Order> order= Collections.synchronizedList(new ArrayList<Order>());		
	public List<Market> market = Collections.synchronizedList(new ArrayList<Market>());

	public Restaurant1CookRole(String name) {
		super();
		this.name = name; 
		food.put("Steak", new MyFood("Steak", 4000, 6, 2, 4));
		food.put("Chicken", new MyFood("Chicken", 3500, 6, 2, 4));
		food.put("Pizza", new MyFood("Pizza", 4000, 6, 2, 5));
		food.put("Salad", new MyFood("Salad", 3000, 6, 2, 8));
	}

	public class MyFood{
		String type;
		long cookingtime;
		int amount;
		int low;
		Double orderamount;
		MyFood(String type, long cookingtime, int amount, int low, double a){
			this.type = type;
			this.cookingtime = cookingtime;
			this.amount = amount;
			this.low = low;
			this.orderamount = a;
		}
	}

	public static class Order {
		Waiter w;
		String choice;
		int table;	

		public state s = state.pending;

		Order(Waiter w, String choice, int table){
			this.w = w;
			this.choice = choice;
			this.table = table;
		}


	}
	
	public void setGui(CookGui cookGui){
		this.cookGui = cookGui;
	}
	
	public void setCashier(Cashier c){
		cashier = c;
	}

	public enum state 
	{ pending, cooking, cooked, readytotake};

	Timer timer = new Timer();

	public void msgAddMarket(Market m){
		market.add(m);
	}

	public void msghereisorder(Waiter w, String choice, int table){
		order.add( new Order(w, choice, table));
		stateChanged();
	}

	public void msgordercooked(Order order){
		order.s = state.cooked;
		stateChanged();
	}

	public void msgHereisYourFood(MarketTruck t, List<Food> fList){
		this.truck = t;
		for(Entry<String, MyFood> entry : food.entrySet()){
			for(Food f : fList){
				if(f.choice.equals(entry.getKey())){
					entry.getValue().amount += f.amount;
				}
			}
		}
		sendTruckBack = true;
		stateChanged();
	}

	public void msgAR(){
		AR.release();
	}

	@Override
	protected boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if(opening){
			Orderfoodislow();
			return true;
		}
		if(sendTruckBack = true) {
			truck.msgGoBack();
		}
		synchronized(order){
			for(Order orders: order){
				if(orders.s == state.pending){
					Docooking(orders);
					return true;
				}
			}
		}
		synchronized(order){
			for(Order orders: order){
				if(orders.s == state.cooked){
					Timerdone(orders);
					return true;
				}
			}	
		}

		return false;
	}

	public void Docooking(final Order o){
		MyFood f = food.get(o.choice);
		if(f.amount == 0){
			Do(o.choice +" is sold out!");
			o.w.msgOutOfFood(o.table);
			order.remove(o);
			foodlist.add(new Food(f.type, f.amount));
			Orderfoodislow();
			return;
		}
		cookGui.DoGotoRefri();
		try {
			AR.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cookGui.showCarryFood(o.choice);
		f.amount --;
		if(f.amount <= f.low){
			foodlist.add(new Food(f.type, f.amount));
			Orderfoodislow();
		}
		cookGui.DoGotoCookingArea();
		try {
			AR.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cookGui.hideCarryFoood();
		cookGui.showfood(o.choice);
		o.s = state.cooking;
		long time = food.get(o.choice).cookingtime;
		timer.schedule(new TimerTask() {
			public void run() {
				msgordercooked(o);
				stateChanged();
			}
		},
		time);

	}

	public void Orderfoodislow(){
		opening = false;
		int s = count;
		Do("We need more food!");
		// marketCashier.MsgIwantFood(this, cahier, foodlist, 1);
	}

	public void Timerdone(Order order){
		order.s = state.readytotake;
		cookGui.hidefood();
		cookGui.showCarryFood(order.choice);
		cookGui.DoGotoPlatingArea();
		try {
			AR.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cookGui.hideCarryFoood();
		Do("Order cooked!");
		order.w.msgorderiscooked(order.table);
	}



}

