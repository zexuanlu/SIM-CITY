package restaurant1;

import restaurant1.gui.CustomerGui;
import restaurant1.interfaces.Cashier;
import restaurant1.interfaces.Customer;
import restaurant1.interfaces.Waiter;
import agent.Role;
import person.interfaces.Person;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Restaurant customer agent.
 */
public class Restaurant1CustomerRole extends Role implements Customer {
	private String name;
	private int hungerLevel = 5;        // determines length of meal
	Timer timer = new Timer();
	public CustomerGui customerGui;
	private Waiter waiter;
	private int location = 0;
	private int orderingtime = 4000;
	private int eatingtime = 5000;
	private int tablenum;
	private String choice;
	private double money = 30, salad = 5.99, pizza = 8.99, chicken = 11, steak = 15;
	// agent correspondents
	private Restaurant1HostRole host = null;
	private Cashier cashier = null;

	public Map<String, Double> menue = new HashMap<String, Double>();




	//    private boolean isHungry = false; //hack for gui


	public enum AgentEvent 
	{none, gotHungry, askforstatus, followHost, seated, readytoorder, askedtoroder, ordered, fooddilivered, doneEating, abouttoleave, dogotocheck, paying, doneLeaving};
	AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for Restaurant1CustomerRole class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public Restaurant1CustomerRole(String name, Person pa){
		super(pa);
		roleName = "Rest1 Customer";
		this.name = name;
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	public void setHost(Restaurant1HostRole host) {
		this.host = host;
	}

	public void setwaiter(Waiter waiter){
		this.waiter = waiter;
	}

	public void setCashier(Cashier cashier){
		this.cashier = cashier;
	}

	public void setMoney(double money){
		this.money = money;
	}
	
	public void setLocation(int loc){
		location = loc;
		customerGui.setLocation(loc);
	}

	public String getCustomerName() {
		return name;
	}
	// Messages

	public void gotHungry() {//from animation
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgAskforStatus(){
		event = AgentEvent.askforstatus;
		stateChanged();
	}

	public void msgSitAtTable(int a, Map<String, Double> s) {
		print("Received msgSitAtTable");
		event = AgentEvent.followHost;
		menue = s;
		tablenum = a;
		stateChanged();
	}

	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}

	public void msgReorder(String choice){
		menue.remove(choice);
		event = AgentEvent.seated;
		stateChanged();
	}

	public void msgwhatyouwant(){
		event = AgentEvent.askedtoroder;
		stateChanged();
	}

	public void msgordercooked(){
		event = AgentEvent.fooddilivered;
		stateChanged();
	}

	public void msgHereisYourBill(int bill){
		stateChanged();
	}

	public void msgHereisYourChange(double change){
		money = change;
		event = AgentEvent.doneEating;
		stateChanged();
	}

	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		event = AgentEvent.doneLeaving;
		stateChanged(); 
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//	Restaurant1CustomerRole is a finite state machine
		try{
		if ( event == AgentEvent.gotHungry ){
			goToRestaurant();
			return true;
		}
		if (event == AgentEvent.askforstatus){
			Decidestatue();
			return true;
		}
		if (event == AgentEvent.followHost ){
			SitDown();
			return true;
		}
		if (event == AgentEvent.seated){
			readytoorder();
			return true;
		}
		if (event == AgentEvent.askedtoroder){
			Hereismychoice();
			return true;
		}
		if (event == AgentEvent.fooddilivered){
			EatFood();
			return true;
		}

		if (event == AgentEvent.doneEating){
			leaveTable();
			return true;
		}
		if ( event == AgentEvent.doneLeaving){
			//no action
			return true;
		}

		}
		catch(ConcurrentModificationException e){
			return false;
		}
		return false;
	}

	// Actions

	private void goToRestaurant() {
		Do("Going to restaurant");
		host.msgIWantFood(this,location);//send our instance, so he can respond to us
		event = AgentEvent.none;
	}

	private void Decidestatue(){
		Random run = new Random();
		int a = run.nextInt(2);
		event = AgentEvent.none;
		if(a == 0){
			Do("I will stay");
			host.msgDecidestatus(false, this);
		} 
		else if(a ==1){
			Do("I am leaving");
			host.msgDecidestatus(true, this);
			customerGui.DoExitRestaurant();
		}

	}

	private void SitDown() {
		Do("Being seated. Going to table");
		customerGui.DoGoToSeat(tablenum);//hack; only one table
		event = AgentEvent.none;
	}

	public void readytoorder(){
		Random run = new Random();
		event = AgentEvent.readytoorder;
		if(menue.size() != 0){
		String[] t = menue.keySet().toArray(new String[0]);
		choice = t[run.nextInt(t.length)];
		boolean noMoney = true, couldpay = false;
		if(money > salad && pizza > money){
			choice = "Salad";
		}
		else if(money > pizza && chicken > money){
			choice = "Pizza";
		}
		else if(money > chicken && steak > money){
			choice = "Chicken";
		}
		for (double s: menue.values()){
			if(money > s){
				noMoney = false;
				couldpay= true;
			}	
		}
		if(couldpay){
			couldpay = false;
			timer.schedule(new TimerTask() {
				public void run() {
					callwaiter();
					stateChanged();
				}
			},
			orderingtime);
		}
		if(noMoney){
			int a = run.nextInt(2);
			if(a == 0){
				Do("Money is not enough");
				event = AgentEvent.doneEating;
			}
			else if(a ==1){
				timer.schedule(new TimerTask() {
					public void run() {
						callwaiter();
						stateChanged();
					}
				},
				orderingtime);
			}
		}
		}
		else{
			Do("I have nothing to order!");
			event = AgentEvent.doneEating;
		}
	}

	public void callwaiter() {
		waiter.msgreadytoorder(this);
	}

	public void Hereismychoice(){
		event = AgentEvent.ordered;
		Do("I want " + choice);
		waiter.msgorderisready(this, choice, tablenum);
		customerGui.ordered(choice);
	}



	private void EatFood() {

		//This next complicated line creates and starts a  thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		customerGui.eating();
		event = AgentEvent.none;
		timer.schedule(new TimerTask() {
			public void run() {
				print("Done eating");
				event = AgentEvent.dogotocheck;
				DoGotoCheck();
				//leaveTable();
				//isHungry = false;
				stateChanged();
			}
		},
		eatingtime);//getHungerLevel() * 1000);//how long to wait before running task
	}

	private void DoGotoCheck(){
		Do("Here is my payment");
		cashier.msgPayment(this, money);
	}

	private void leaveTable() {
		customerGui.done();
		Do("Leaving.");
		event = AgentEvent.abouttoleave;
		waiter.msgLeavingTable(this);
		customerGui.DoExitRestaurant();
		person.msgFinishedEvent(this);
	}


	public String getName() {
		return name;
	}

	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
		//could be a state change. Maybe you don't
		//need to eat until hunger lever is > 5?
	}

	public String toString() {
		return "customer " + getName();
	}

	public void setGui(CustomerGui g) {
		customerGui = g;
	}

	public CustomerGui getGui() {
		return customerGui;
	}
	
	public String getRoleName(){
		return roleName;
	}
}
