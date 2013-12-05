package restaurant4;

import restaurant4.gui.Restaurant4CustomerGui;
import restaurant4.gui.Restaurant4CustomerGui.GUIstate;
import restaurant4.interfaces.*;
import agent.Agent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

/**
 * Restaurant customer agent.
 * 
 * Goes to the restaurant, looks at the menu, chooses and orders an item, eats the food
 */
public class Restaurant4CustomerRole extends Agent implements Restaurant4Customer {
	private static final int eatingTime = 10000;
	private static final int menuTime = 5000;
	private String name;
	private int hungerLevel = 5;        // Unused as of v2
	private int tableNum = 0;
	private String choice;
	Timer timer = new Timer();
	private Restaurant4CustomerGui customerGui;
	private Restaurant4Menu menu;

	Semaphore atCashier = new Semaphore(0, true);
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
	public Restaurant4CustomerRole(String name){
		super();
		this.name = name;
		if(name.equals("Poor"))
			money = 7.00;
		else if(name.equals("Broke"))
			money = 0.00;
		else if(name.equals("Flake"))
			money = 0.00;
		else
			money = 20.00;
		Do("Got " + money);
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
		print("I'm hungry");
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
		print("Received msgSitAtTable");
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
		print("Received msgReadyForOrder");
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
	 * Received from the gui when the customer arrives at his seat
	 */
	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	
	/**
	 * Received from the gui when the customer leaves the restaurant
	 */
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		if(name.equals("Flake"))
			money += 40.00;
		event = AgentEvent.doneLeaving;
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
	
	//Received from the gui telling the customer he is at the cashier
	public void msgAtCashier(){
		atCashier.release();
		stateChanged();
	}
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
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
		Do("Going to restaurant");
		host.msgIWantFood(this);//send our instance, so he can respond to us
	}

	//Tells the gui to go to the table
	private void SitDown() {
		Do("Being seated. Going to table");
		customerGui.DoGoToSeat(tableNum);
	}

	//Tells the host that he is leaving
	private void notWaiting() {
		host.msgWaitTooLong(this);
		customerGui.DoExitRestaurant();
	}
	//Sets a timer while he looks over the menu. When it finishes, he has chosen his dinner
	private void CheckMenu(){
		if(money < 5.99 && !name.equals("Flake")){
			event = AgentEvent.done;
			Do("Food Too Expensive");
			return;
		}
		timer.schedule(new TimerTask() {
			public void run() {
				Do("Chose my food");
				event = AgentEvent.signaling;
				stateChanged();
			}
		}, menuTime);
	}
	
	//Messages the waiter telling him that he is ready to order
	private void SignalWaiter(){
		Do("Signaling Waiter");
		waiter.msgReadyToOrder(this);
	}
	
	//Tells the waiter his choice of food
	private void OrderFood(){
		if(menu.options.size() == 0){
			leaveTable();
			return;
		}
		else if(!menu.contains("Salad") && name.equals("Poor")){
			leaveTable();
			return;
		}
		int choose = (int)(Math.random() * menu.options.size());
		while(menu.options.get(choose).price > money && !name.equals("Flake"))
			choose = (int)(Math.random() * menu.options.size());
		choice = menu.options.get(choose).type;
		customerGui.setState(GUIstate.OrderedFood, choice);
		Do("Ordering " + choice);
		waiter.msgHereIsOrder(this, choice);
	}
	
	//Eats the food
	private void EatFood() {
		Do("Eating Food");
		customerGui.setState(GUIstate.EatingFood, choice);
		timer.schedule(new TimerTask() {
			public void run() {
				Do("Done eating");
				event = AgentEvent.doneEating;
				stateChanged();
			}
		},
		eatingTime);
	}

	//Requests the check from the waiter
	public void requestCheck(){
		Do("Requesting Check");
		waiter.msgINeedCheck(this);
	}
	
	//Goes to the cashier and pays for his food
	public void payForFood(){
		Do("Paying For Food");
		customerGui.DoGoToCashier();
		try{
			atCashier.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		cashier.msgPayingForFood(this, money);
		money = 0;
	}
	//Messages the gui to tell him to leave the table
	private void leaveTable() {
		Do("Leaving.");
		customerGui.setState(GUIstate.None, "");
		waiter.msgLeavingTable(this);
		customerGui.DoExitRestaurant();
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
}

