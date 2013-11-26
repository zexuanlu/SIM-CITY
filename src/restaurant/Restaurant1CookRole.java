package restaurant;

import agent.Role;
import person.PersonAgent;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import restaurant.gui.CookGui;
import restaurant.interfaces.Cook;
import restaurant.interfaces.Waiter;
import restaurant.interfaces.Cashier;
import market.interfaces.MarketCashier;
import restaurant.shareddata.*;

import market.interfaces.MarketTruck;
import market.Food;


//Add case in scheduler to tell markettruck to go back

public  class Restaurant1CookRole extends Role implements Cook {
	private CookGui cookGui = null;
	private Cashier cashier;
	private MarketTruck truck;
	private MarketCashier marketCashier;
	private Restaurant1RevolvingStand revStand;
	String name;
	boolean opening = false;
	public boolean sendTruckBack = false;
	int count = 0;
	public Map<String, MyFood> food = new HashMap<String, MyFood>();
	public List<Food> foodlist = Collections.synchronizedList(new ArrayList<Food>());
	private Semaphore AR = new Semaphore(0,true);
	public List<Order> order= Collections.synchronizedList(new ArrayList<Order>());	

	public Restaurant1CookRole(String name, PersonAgent pa) {
		super(pa);
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
	
	public void setGui(CookGui cookGui){
		this.cookGui = cookGui;
	}
	
	public void setCashier(Cashier c){
		cashier = c;
	}
	
	public void setRevStand(Restaurant1RevolvingStand rev){
		this.revStand = rev;
	}

	public void setMarketCashier(MarketCashier c){
		this.marketCashier = c;
	}
	
	Timer timer = new Timer();

	public void msghereisorder(Waiter w, String choice, int table){
		order.add( new Order(w, choice, table));
		stateChanged();
	}

	public void msgordercooked(Order order){
		order.s = Order.state.cooked;
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
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if(opening){
			Orderfoodislow();
			return true;
		}
		if(sendTruckBack == true) {
			System.out.println(sendTruckBack);
			truck.msgGoBack();
			return true;
		}
		if(order.size() <= 3 && !revStand.isEmpty()) {
			TakeOrderFromStand();
			return true;
		}
		synchronized(order){
			for(Order orders: order){
				if(orders.s == Order.state.pending){
					Docooking(orders);
					return true;
				}
			}
		}
		synchronized(order){
			for(Order orders: order){
				if(orders.s == Order.state.cooked){
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
		o.s = Order.state.cooking;
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
		marketCashier.MsgIwantFood(this, cashier, foodlist, 1);
	}

	public void TakeOrderFromStand() {
		Order o = revStand.removeOrder();
		order.add(o);
	}
	
	public void Timerdone(Order order){
		order.s = Order.state.readytotake;
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

