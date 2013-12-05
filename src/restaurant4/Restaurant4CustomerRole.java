package restaurant4;

import restaurant4.gui.Restaurant4CustomerGui;
import restaurant4.gui.Restaurant4CustomerGui.GUIstate;
import restaurant4.interfaces.*;
import agent.Role;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import person.interfaces.Person;

/**
 * Restaurant customer agent.
 * 
 * Goes to the restaurant, looks at the menu, chooses and orders an item, eats the food
 */
public class Restaurant4CustomerRole extends Role implements Restaurant4Customer {
	private static final int eatingTime = 10000;
	private static final int menuTime = 5000;
	private String name;
	private int hungerLevel = 5;        // Unused as of v2
	private int tableNum = 0;
	private String choice;
	Timer timer = new Timer();
	private Restaurant4CustomerGui customerGui;
	private Restaurant4Menu menu;

	Semaphore movement = new Semaphore(0, true);
	// agent correspondents
	private Restaurant4Host host;
	private Restaurant4Waiter waiter;
	private Restaurant4Cashier cashier;
	double money;

	//The various states of the customer
	public enum AgentState
	{DoingNothing, WaitingInRestaurant, BeingSeated, Seated, Eating, DoneEating, Leaving, WaitingForWaiter
		, WaitingForFood, WaitingForCheck, Paying};
	private AgentState state = AgentState.DoingNothing;//The start state

	//The various events of the customer
	public enum AgentEvent 
	{none, gotHungry, followHost, seated, doneEating, doneLeaving, signaling, ordering, 
		gotFood, gotCheck, donePaying, done, restaurantFull};
	AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public Restaurant4CustomerRole(String name, Person pa){
		super(pa);
		this.name = name;
	}

	/**
	 * hack to establish connection to Host agent.
	 * 
	 * @param host the host agent
	 */
	public void setHost(Restaurant4HostRole host) {
		this.host = host;
	}

	public String getCustomerName() {
		return name;
	}
	// Messages

	/**
	 * Recieved from the gui and tells the customer that he is hungry
	 */
	public void gotHungry() {//from animation
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	/**
	 * Received from the waiter, telling the customer to go to a table
	 * 
	 * @param tableNum the table to go to
	 * @param waiter the waiter who sent the message
	 * @param menu the menu to order from
	 */
	public void msgSitAtTable(int tableNum, Restaurant4Waiter waiter, Restaurant4Menu menu) {
		this.tableNum = tableNum;
		this.waiter = waiter;
		this.menu = menu;
		event = AgentEvent.followHost;
		stateChanged();
	}
	
	//Received from the host when the restaurant is full
	public void msgRestaurantFull(){
		event = AgentEvent.restaurantFull;
		stateChanged();
	}

	//Received from the waiter when the waiter arrives to take the order
	public void msgReadyForOrder(){
		event = AgentEvent.ordering;
		stateChanged();
	}

	/** 
	 * Received from the waiter when the customer needs to reorder
	 * 
	 * @param m the changed menu
	 */
	public void msgReOrder(Restaurant4Menu m){
		menu = m;
		state = AgentState.WaitingForWaiter;
		event = AgentEvent.ordering;
		stateChanged();
	}

	/**
	 * Received from the waiter when he brings the food
	 * 
	 * @param choice the type of food brought
	 */
	public void msgHereIsFood(String choice) {
		//State is now eating
		event = AgentEvent.gotFood;
		stateChanged();
	}
	
	/**
	 * Received from the waiter giving him his check
	 * 
	 * @param price the "Check" - the price to be paid
	 * @param cashier the cashier
	 */
	public void msgHereIsCheck(double price, Restaurant4Cashier cashier){
		this.cashier = cashier;
		if(money < price){
			Do("Dine And DASH!");
			event = AgentEvent.donePaying;
		}
		else{
			Do("Got the Check");
			event = AgentEvent.gotCheck;
		}
		stateChanged();
	}
	
	/**
	 * Received from the cashier, giving the customer his change
	 * 
	 * @param money the change
	 */
	public void msgHereIsChange(double money){
		this.money = money;
		event = AgentEvent.donePaying;
		stateChanged();
	}
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine

		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.WaitingInRestaurant;
			goToRestaurant();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.restaurantFull){
			if(name.equals("Rush")){
				state = AgentState.Leaving;
				notWaiting();
				return true;
			}
			else{
				event = AgentEvent.gotHungry;
				return true;
			}	
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followHost ){
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		if (state == AgentState.BeingSeated && event == AgentEvent.seated){
			state = AgentState.Seated;
			CheckMenu();
			return true;
		}
		if (state == AgentState.Seated && event == AgentEvent.signaling){
			state = AgentState.WaitingForWaiter;
			SignalWaiter();
			return true;
		}
		if (state == AgentState.Seated && event == AgentEvent.done){
			state = AgentState.Leaving;
			leaveTable();
			return true;
		}
		if (state == AgentState.WaitingForWaiter && event == AgentEvent.ordering){
			state = AgentState.WaitingForFood;
			OrderFood();
			return true;
		}
		if (state == AgentState.WaitingForFood && event == AgentEvent.gotFood){
			state = AgentState.Eating;
			EatFood();
			return true;
		}		
		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			state = AgentState.WaitingForCheck;
			requestCheck();
			return true;
		}
		if (state == AgentState.WaitingForCheck && event == AgentEvent.gotCheck){
			state = AgentState.Paying;
			payForFood();
			return true;
		}
		if (state == AgentState.WaitingForCheck && event == AgentEvent.donePaying){
			state = AgentState.Leaving;
			leaveTable();
			return true;
		}
		if (state == AgentState.Paying && event == AgentEvent.donePaying){
			state = AgentState.Leaving;
			leaveTable();
			return true;
		}
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			//no action
			return true;
		}
		return false;
	}

	// Actions

	//Messages the host
	private void goToRestaurant() {
		host.msgIWantFood(this);//send our instance, so he can respond to us
	}

	//Tells the gui to go to the table
	private void SitDown() {
		customerGui.DoGoToLocation("Table " + tableNum);
	}

	//Tells the host that he is leaving
	private void notWaiting() {
		host.msgWaitTooLong(this);
		customerGui.DoGoToLocation("Home");
	}
	//Sets a timer while he looks over the menu. When it finishes, he has chosen his dinner
	private void CheckMenu(){
		if(money < 5.99 && !name.equals("Flake")){
			event = AgentEvent.done;
			return;
		}
		timer.schedule(new TimerTask() {
			public void run() {
				event = AgentEvent.signaling;
				stateChanged();
			}
		}, menuTime);
	}
	
	//Messages the waiter telling him that he is ready to order
	private void SignalWaiter(){
		waiter.msgReadyToOrder(this);
	}
	
	//Tells the waiter his choice of food
	private void OrderFood(){
		if(menu.options.size() == 0){
			leaveTable();
			return;
		}
		else if(!menu.contains("Salad") && money < 5.99){
			leaveTable();
			return;
		}
		int choose = (int)(Math.random() * menu.options.size());
		while(menu.options.get(choose).price > money && !name.equals("Flake"))
			choose = (int)(Math.random() * menu.options.size());
		choice = menu.options.get(choose).type;
		customerGui.setState(GUIstate.OrderedFood, choice);
		waiter.msgHereIsOrder(this, choice);
	}
	
	//Eats the food
	private void EatFood() {
		customerGui.setState(GUIstate.EatingFood, choice);
		timer.schedule(new TimerTask() {
			public void run() {
				event = AgentEvent.doneEating;
				stateChanged();
			}
		},
		eatingTime);
	}

	//Requests the check from the waiter
	public void requestCheck(){
		waiter.msgINeedCheck(this);
	}
	
	//Goes to the cashier and pays for his food
	public void payForFood(){
		customerGui.DoGoToLocation("Cashier");
		try{
			movement.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		cashier.msgPayingForFood(this, money);
		money = 0;
	}
	//Messages the gui to tell him to leave the table
	private void leaveTable() {
		customerGui.setState(GUIstate.None, "");
		waiter.msgLeavingTable(this);
		customerGui.DoGoToLocation("Home");
	}

	// Accessors, etc.

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

	public void setGui(Restaurant4CustomerGui g) {
		customerGui = g;
	}

	public Restaurant4CustomerGui getGui() {
		return customerGui;
	}

	public void msgAtDestination() {
		movement.release();
		stateChanged();
	}

	@Override
	public String getRoleName() {
		return "Restaurant 4 Customer";
	}
}

