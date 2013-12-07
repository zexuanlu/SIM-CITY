package restaurant5;

import agent.Role;
import restaurant5.gui.CookGui5;
import restaurant5.interfaces.Waiter5; 
import person.PersonAgent; 

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class CookAgent5 extends Role {
	PersonAgent myPerson; 
	public enum CookState {none, checkStand};
	CookState cookstate; 
	public RevolvingStand5 revolvingstand; 
	private Queue<Integer> grillnumber = new LinkedList<Integer>(); 
	private Queue<Integer> platenumber = new LinkedList<Integer>(); 

	boolean opened = true; 	
	boolean checkstand = false; 
	
	List<Order> orders = Collections.synchronizedList(new ArrayList<Order>()); 
	List<Order> guilist = Collections.synchronizedList(new ArrayList<Order>()); 

	Timer timer = new Timer();
	String name; 
	private Semaphore atStand = new Semaphore(0);
	private Semaphore atGrill = new Semaphore(0);
	private Semaphore atPlate = new Semaphore(0);
	private CookGui5 cookGui; 
	LinkedList<MarketAgent5> markets = new LinkedList<MarketAgent5>(); 
	
	public class marketOrder {
		marketOrder(Map<String,Integer>f, OrderState _s){
			order = f;
			s = _s;
		}
		Map<String,Integer> order; 
		OrderState s; 
	}
	private enum OrderState {needtoOrder, ordered, reorder, done};
	
	List<marketOrder> marketOrders = Collections.synchronizedList(new ArrayList<marketOrder>());
	
	public class Order {
		Waiter5 w; 
		State s; 
		String choice; 
		int table;
		public int grillnumber; 
		public int platenumber; 
		public Order(Waiter5 _w,String _choice,int _table, State _s){
			w = _w;
			s = _s;
			choice = _choice;
			table = _table;
		}
		public Order(Waiter5 _w,String _choice,int _table){
			w = _w;
			choice = _choice;
			table = _table;
		}
		public void setState(State _s){
			s= _s; 
		}
	}
	public enum State { pending, cooking, done, finished, guistuff};
	class Food {
		Food(String _type, int _cookingtimes, int _low, int _capacity, int _amount){
			type = _type;
			cookingtimes = _cookingtimes;
			low = _low;
			capacity = _capacity; 
			amount = _amount; 
		}
		public String type;
		public int cookingtimes;
		public int low;
		public int capacity; 
		public int amount;
		

	}
	private Map<String,Food> foods = new HashMap<String,Food>();

	
    public CookAgent5(String name, PersonAgent p) {
		super(p);
		myPerson = p; 
		this.name = name; 
		//type, cookingtimes, low, capacity, amount
		Food f = new Food("Steak", 8, 2, 50, 50); 
		foods.put("Steak",f);
		f = new Food("Chicken", 6, 2,50,50); 
		foods.put("Chicken",f);
		f = new Food("Salad",3,2,50,50);
		foods.put("Salad", f);
		f = new Food ("Pizza", 4, 2, 50,50); 
		foods.put("Pizza", f);
		grillnumber.add(1);
		grillnumber.add(2);
		grillnumber.add(3);
		platenumber.add(1);
		platenumber.add(2);
		platenumber.add(3);
		
		
		timer.schedule(new TimerTask() {
			public void run() {
				checkstand = true;
				stateChanged();
			}
		},
			2000);//getHungerLevel() * 1000);//how long to wait before running task
		}
    

		
	public void msgHereIsOrder(Waiter5 w, String choice, int table) {
		State _s = State.pending;
		orders.add(new Order(w,choice,table,_s));
		stateChanged();
	}
	
	public void msgpickedupfood(Waiter5 w, String choice, int table){
		synchronized(guilist){
			for (Order o: guilist){
				if (o.w == w && o.choice.equals(choice) && o.table == table){
					guilist.remove(o);
					cookGui.pickedupOrder(o.grillnumber);
				}
			}
		}
	}
	
	
	public void msgHereisMarketOrder(Map<String,Integer>sentOrder, Map<String,Integer>originalOrder){
		for (Map.Entry<String, Integer> entry: sentOrder.entrySet()){
			String choice = entry.getKey();
			int amount = entry.getValue();
			foods.get(choice).amount += amount; 
			//print("Cook Inventory of "+ choice + " is now " + foods.get(choice).amount);
		}
		synchronized(marketOrders){
			for (marketOrder m: marketOrders){
				if (m.order==originalOrder){
					m.s = OrderState.done; 
				}
			}
		}
	}
	
	public void msgatPlate(){
		if (atPlate.availablePermits() == 0) {  
			atPlate.release();
		}
	}
	
	public void msgatStand(){
		atStand.release();
	}
	
	public void msgatGrill(){
		if (atGrill.availablePermits() == 0) {  
			atGrill.release();
		}
	}

	
	public void msgHereisOutofList(MarketAgent5 myMarket, Map<String,Integer> outofList){  //unable to fulfill any of the order
		//do we have to remove market after they have nothing? 
		//since no items were sent, then i will just get them to redo the entire thing and change the market
		marketOrder m = new marketOrder(outofList, OrderState.needtoOrder);
		marketOrders.add(m);
		markets.remove(myMarket);
		stateChanged();
	}
	
	public void msgfoodDone(Order o){
		o.s = State.done;
		stateChanged();
	}
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		
	
		if (opened){ 
			orderFoodthatisLow();
			return true; 
		}
		
		if (checkstand){
			checkStand();
			return true; 
		}
		
		synchronized(marketOrders){
			if (markets.size()!= 0){
				for (marketOrder m: marketOrders){
					if(m.s == OrderState.needtoOrder){
						orderFood(m);
						return true;
					}
				}
			}
		}
		synchronized(orders){
			for (Order o:orders){
				if (o.s == State.done){
					plateIt(o);
					return true;
				}
			}
		}
		
		synchronized(orders){
			for (Order m:orders){
				if (m.s == State.pending){
					cookIt(m);
					return true;
				}
			}
		}

		
		cookGui.gotoPlate(); 
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}
	// Actions

	

	private void plateIt(Order o){
	//	doPlating(o); //hack animation
		atGrill.drainPermits();
		cookGui.gotoGrill(o.grillnumber);
		try {
			atGrill.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		print("Cook order done");
		orders.remove(o);
		guilist.add(o);
		atPlate.drainPermits();
		
		
		int plate = platenumber.poll();
		o.platenumber = plate; 
		platenumber.add(plate);
		
		
		cookGui.gotoPlate(o.choice, o.grillnumber, o.platenumber); 
		try {
			atPlate.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		(o.w).msgorderDone(o.choice, o.table);
	}
	
	private void orderFoodthatisLow(){
		opened = false; 
		Map <String, Integer> shoppingList = new HashMap<String, Integer>();
		for (Food _f: foods.values()){
			if (_f.amount <= _f.low){
				shoppingList.put(_f.type,_f.capacity -_f.amount);
			}
		}
		if (shoppingList.size() != 0){
			marketOrder m = new marketOrder(shoppingList,OrderState.ordered);
			marketOrders.add(m);
			orderFood(m);
		}
	}
	
	private void orderFood(marketOrder m){
		if (markets.size()!=0){
			m.s = OrderState.ordered;
			print ("Cook ordered food that is low");
			pickaMarket().msgOrderFood(m.order);
		}
	}
	
	private boolean checkInventory(){
		String orderList = "";
		print ("Cook check inventory");
		boolean inventoryStocked = true;
		Map<String, Integer> toOrder = new HashMap<String, Integer>();
		boolean alreadyordered; 
		for(Map.Entry<String, Food> entry: foods.entrySet()){
			alreadyordered = false; 
			String name = entry.getKey();
			Food f = entry.getValue();
			if (f.amount <= f.low){
				//check in orders list for any that are currently ordered but not delivered
				synchronized(marketOrders){
					for (marketOrder m: marketOrders){
						if (m.s == OrderState.ordered || m.s == OrderState.needtoOrder){
							//check if there's anything that's already been ordered
							if (m.order.containsKey(name)){//if the order's in
								alreadyordered = true; 
							}
						}
					}
				}
				if (!alreadyordered){
					toOrder.put(name, f.capacity-f.amount);
					orderList = (orderList + " " + (f.capacity - f.amount) + " " + name);  
				}
			}
		}
		if (toOrder.size()!= 0){
			print("Cook when checking Inventory, ordered" + orderList);
			marketOrder m = new marketOrder(toOrder,OrderState.needtoOrder);
			marketOrders.add(m);
			inventoryStocked = false; 
		}
		return inventoryStocked; 
	}
	
	
    private void checkStand(){
    	print("check stand");
    	checkstand = false; 
    
    	cookGui.DoGoToStand();
    	try {
			atStand.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	CookOrder5 co = revolvingstand.removeOrder(); 
    	if (co!= null){
    		orders.add(new Order(co.w, co.choice, co.tablenum,State.pending));
    	}
		timer.schedule(new TimerTask() {
			public void run() {
				checkstand = true;
				stateChanged();
			}
		},
			2000);//getHungerLevel() * 1000);//how long to wait before running task
		
    	stateChanged();
    }
	
	
	private void cookIt(final Order o){
		if (marketOrders.size()!=0){
		checkInventory();
		}
		
		Food f = foods.get(o.choice);
		if (f.amount == 0){
			orders.remove(o);
			print("Cook Out of " + o.choice);
			(o.w).msgOutof(o.choice, o.table);
			return;
		}
				
		atGrill.drainPermits();
		int grill = grillnumber.poll();
		o.grillnumber = grill; 
		grillnumber.add(grill);
		cookGui.gotoGrill(grill, o.choice);
		try {
			atGrill.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		f.amount--; 
		o.s = State.cooking;
		timer.schedule(new TimerTask() {
			public void run() {
				msgfoodDone(o); 
				//isHungry = false;
				stateChanged();
			}
		},
		foods.get(o.choice).cookingtimes*1000);//getHungerLevel() * 1000);//how long to wait before running task
		}
	
	private MarketAgent5 pickaMarket(){
		MarketAgent5 temp; 
		temp = markets.pop();
		markets.addLast(temp);
		return temp; 
	}
	
	public void drain(){
		for(Food f: foods.values()){
			f.amount = 0; 
		}
	}
	
	public String getName(){
		return name;
	}
	
	public void addMarket(MarketAgent5 m){
		markets.addFirst(m); 
	}
	
	public void setGui(CookGui5 l){
		cookGui = l; 
	}
	
	public void setRevolvingStand(RevolvingStand5 rvs){
		revolvingstand = rvs; 
	}

	public String getRoleName(){
		return "Restaurant 5 Cook";
	}
	
}