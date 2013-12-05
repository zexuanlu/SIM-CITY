package restaurant2;

import restaurant2.gui.CustomerGui;
import restaurant2.interfaces.Market;
import agent.Agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MarketAgent extends Agent implements Market{

	Timer timer = new Timer();


	// agent correspondents
	private MyCook cook;
	private Restaurant2CashierRole cashier;
	public List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	public List<String> outOfItems = Collections.synchronizedList(new ArrayList<String>());
	private Map<Restaurant2CashierRole, Integer> cashierOwesMe = new HashMap<Restaurant2CashierRole, Integer>();
	private Map<String, Integer> price = new HashMap<String, Integer>();
	private Food inventory = new Food(10,10,10,10,10,10);
	public enum OrderState {Unfufilled, Fufilled};

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public MarketAgent(String name){
		super();	
		price.put("Steak", 20);
		price.put("Hamburger", 10);
		price.put("Chicken", 8);
		price.put("Ribs", 12);
		price.put("Salad", 5);
		price.put("Pound Cake", 2);
	}
	public void setCook(Restaurant2CookRole c)
	{
		cook = new MyCook(c);
	}
	public void setCashier(Restaurant2CashierRole c)
	{
		cashier = c;
	}

	// Messages
	public void msgRestockMe(Map<String, Integer> restockOrders) 
	{
		List<String> items = new ArrayList<String>();
		for(Map.Entry<String, Integer> entry: restockOrders.entrySet())
		{
			items.add(entry.getKey());
		}
		Order newOrder = new Order(items, restockOrders);
		orders.add(newOrder);

		stateChanged();
	}
	public void msgHeresPayment(Restaurant2CashierRole cashier, int payment)
	{
		if(payment <= 0)
		{
			cashierOwesMe.put(this.cashier, payment);
			print("You owe me $"+payment);
		}
		else
		{
			print("Thanks for your payment");
		}
		stateChanged();
	}
	public void msgHeresLatePayment(Restaurant2CashierRole cashier, int payment)
	{
		int cp = cashierOwesMe.get(cashier);
		cp += payment;
		
		cashierOwesMe.put(cashier, cp);
		print("You still owe me $"+cp);
	}
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {

		synchronized(orders){
			for(Order o : orders)
			{
				if(o.state == OrderState.Unfufilled)
				{
					getOrder(o);
				}
			}
		}
		return false;
	}

	// Actions

	private void checkAndNotify(Order order){
		for(String item : outOfItems)
		{
			if(order.items.contains(item))
			{
				cook.getCook().msgOutOfFood(item, this);
			}
		}
	}
	private void getOrder(final Order order) {

		checkAndNotify(order);

		int time = 1000;
		timer.schedule(new TimerTask() {

			public void run() {
				
				orders.remove(order);
				fillOrder(order);
			}
		},
		time);
	}
	private void fillOrder(Order order)
	{
		int q = 7;
		for(String item : order.items)
		{
			if(inventory.getInventory(item) < 7)//This is a magic number and must be changed (cook orders at 3 so the market should fulfill an order for 7)
			{
				order.setQuantity(item, inventory.getInventory(item));
				inventory.reduce(item, inventory.getInventory(item));
			}
			else{
				order.setQuantity(item, q);
				inventory.reduce(item, q);
			}
		}
		cook.getCook().msgMarketOrderFilled(this, order.itemsAndQuantity, order.total);
		cashier.msgCheckFromMarket(this, order.total);
		print("order filled");
		stateChanged();
	}

	private class MyCook {
		private Restaurant2CookRole cook;
		private boolean stocked;
		MyCook(Restaurant2CookRole cook){
			this.cook = cook;
			stocked = true;
		}
		public boolean stocked()
		{
			return stocked;
		}
		public Restaurant2CookRole getCook(){
			return cook;
		}
		public void setCook(Restaurant2CookRole c)
		{
			this.cook = c;
		}
	}
	private class Food {

		public Map<String, Boolean> orderStatus = new HashMap<String, Boolean>();
		public Map<String, Integer> foodList = new HashMap<String, Integer>();

		Food(int s, int hb, int r, int sal, int chix, int cake)
		{

			foodList.put("Steak", s);
			foodList.put("Hamburger", hb);
			foodList.put("Ribs", r);
			foodList.put("Salad", sal);
			foodList.put("Chicken", chix);
			foodList.put("Pound Cake", cake);


			orderStatus.put("Steak", false);
			orderStatus.put("Hamburger", false);
			orderStatus.put("Ribs", false);
			orderStatus.put("Salad", false);
			orderStatus.put("Chicken", false);
			orderStatus.put("Pound Cake", false);
		}
		void reduce(String foodType, int quantity)
		{
			int currentQuantity = foodList.get(foodType);
			foodList.put(foodType, currentQuantity - quantity);
		}
		int getInventory(String foodType)
		{
			return foodList.get(foodType);
		}

	}
	private static class Order{

		public List<String> items;
		public Map<String, Integer> itemsAndQuantity; //= new HashMap<String, Integer>();
		OrderState state;
		public Check total;

		Order(List<String> i, Map<String, Integer> quantities)
		{
			items = i;
			state = OrderState.Unfufilled;
			itemsAndQuantity = quantities;
			total = new Check(itemsAndQuantity);
		}
		void setQuantity(String item, int q)
		{
			itemsAndQuantity.put(item, itemsAndQuantity.get(item)+q);
			total.setCheck(itemsAndQuantity);
		}

	}
	@Override
	public void msgRestockMe() {
		// TODO Auto-generated method stub
		
	}
}
