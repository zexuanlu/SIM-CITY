package restaurant5;

import person.PersonAgent; 
import restaurant5.interfaces.Market5; 
import agent.Role; 

import java.util.*;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class MarketAgent5 extends Role implements Market5{
	int Cash; 
	Menu5 myMenu = new Menu5();
	Timer timer = new Timer();
	PersonAgent myPerson; 
	class Food {
		int amount;
		String choice; 
		Food (String c, int a){
			choice = c;
			amount = a; 
		}
	}

	
	class Order{
		Map<String, Integer> orderList; 
		Map<String, Integer> sendList; 

		State s;
		Order (Map<String, Integer> shoppingList, State _s){
			orderList = shoppingList; 
			s = _s;
		}
	}
	
	class FlakedBill{
		public CashierAgent5 cashier; 
		public int flakedAmount; 
		public flakeState state; 
		FlakedBill(CashierAgent5 c, int f){
			flakedAmount = f;
			cashier = c; 
			state = flakeState.flaked; 
		}
	}
	private enum flakeState {flaked, paid};
	private enum State {ordered,sendingFood,toPay,paid, flaked,done};
	private Map<String,Integer> foods = new HashMap<String,Integer>();
	private List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	private CookAgent5 myCook; 
	private CashierAgent5 myCashier; 
	List<FlakedBill> flakes = Collections.synchronizedList(new ArrayList<FlakedBill>());
	private String name;

	
    public MarketAgent5(String _name, PersonAgent p) {
		super(p);		
    	myPerson = p; 
		name = _name; 
		Cash = 100; 
	
		foods.put("Steak",4);
		foods.put("Chicken",4);
		foods.put("Salad", 4);
		foods.put("Pizza", 4);
		}
    
    public void msgOrderFood(Map<String,Integer>sentList){
    	Order _o = new Order(sentList, State.ordered);
    	orders.add(_o);
    	stateChanged();
    }
		
    public void msghereispayment(int original, int paid){
    	Cash = Cash + paid; 
    	if (paid < original){
    		//put him on the flaked list
    		FlakedBill f = new FlakedBill(myCashier, original - paid);
    		flakes.add(f);
    	}
    }
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		synchronized(orders){
			for (Order o:orders){
				if (o.s == State.ordered){
					sendFood(o);
					return true; 
				}
			}
		}	
		
		synchronized(orders){
			for (Order o:orders){
				if (o.s == State.toPay){
					sendCashierBill(o);
					return true; 
				}
			}
		}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	private void sendFood(final Order o){
		o.s = State.sendingFood; 

		Map<String,Integer> sendList = new HashMap<String,Integer>();
		Map<String,Integer> outofList = new HashMap<String,Integer>();

		for (Map.Entry<String, Integer> entry: o.orderList.entrySet()){
			String choice = entry.getKey();
			int amount = entry.getValue();
			if (amount <= foods.get(choice)){ //if you can fulfill order
				sendList.put(choice, amount);
				foods.put(choice,foods.get(choice)-amount); //removes items from inventory
			}
			else { //can't fulfill all of the order
				if (foods.get(choice)!= 0){ //tries to fulfill some part of order
					sendList.put(choice,foods.get(choice)); //adds all that you can of item
				}
				outofList.put(choice, amount - foods.get(choice)); //adds what you can't order
				foods.put(choice,0); //so you have no more in your inventory

			}
		}
		o.sendList = sendList; 
		String s = ""; 
		for (Map.Entry<String, Integer> entry: outofList.entrySet()){
			s = (s + entry.getKey() + " " + entry.getValue() + " ");			 
		}
		
		if (outofList.size()!= 0){
			print ("Market here is outof List");
			print ("out of list is " + s);
			myCook.msgHereisOutofList(this, outofList);
		}
		
		final Map<String,Integer> m = sendList;
		if (sendList.size() != 0){
			timer.schedule(new TimerTask() {
				public void run() {
					sendMarketOrder(m,o.orderList,o); 
				}
			},
			3000);
		}
	}
	
	private void sendCashierBill(Order o){
		//calculate the bill
		o.s = State.done; 
		orders.remove(o);
		int Bill = 0;
		String choice; 
		int amount; 
		for (Map.Entry<String, Integer> s: o.sendList.entrySet()){
			choice = s.getKey();
			amount = s.getValue();
			Bill = Bill + (myMenu.getPrice(choice)*amount); 
		}
		
		//check if the cashier is in debt && add previous debt to his new check
		synchronized(flakes){
			for (FlakedBill f: flakes){
				if (f.state == flakeState.flaked){
					f.state = flakeState.paid; 
					print ("Market Cashier previously flaked $" + f.flakedAmount + " and the debt has been added to his current bill of " + Bill);
					Bill = Bill + f.flakedAmount; 
					f.flakedAmount = 0;
				}
			}
		}
		
	    print ("Market Here is the Bill of " + Bill);
		myCashier.msgmarketbill(this, Bill);
	}
	
	private void sendMarketOrder(Map<String,Integer>m, Map<String,Integer>original, Order o){
		o.s = State.toPay;
		String l = ""; 
		for (Map.Entry<String, Integer> entry: m.entrySet()){
			l = l+" " + entry.getValue() + " " + entry.getKey();
		}
		print ("Market here is order of:" + l);
		myCook.msgHereisMarketOrder(m, original);
		stateChanged();
	}
	
	public void setCook(CookAgent5 c){
		myCook = c;
	}
	
	public String getName(){
		return name;
	}
	
	public void setCashier(CashierAgent5 c){
		myCashier = c; 
	}
	
	public String toString(){
		return name; 
	}

	public String getRoleName(){
		return "Restaurant 5 Market";
	}
}