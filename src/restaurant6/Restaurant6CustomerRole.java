package restaurant6;

import restaurant6.gui.Restaurant6CustomerGui; 
import restaurant6.interfaces.Restaurant6Customer;
import restaurant6.interfaces.Restaurant6Waiter;
import agent.Agent;
import agent.Role;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import person.interfaces.Person;

/**
 * Restaurant customer agent.
 */
public class Restaurant6CustomerRole extends Role implements Restaurant6Customer {
	
	private String name;
	private int hungerLevel = 5;        // Determines length of meal
	private Timer timer = new Timer();
	private Timer readMenuTimer = new Timer();
	private Timer washingDishesTimer = new Timer();
	private Restaurant6CustomerGui customerGui;
	private int customerSeat;
	private Random generator = new Random();	// To determine customer's food choice
	private Random moneyGenerator = new Random(); // To determine random amount of money
	private String cannotOrder;
	private String information;
	private Restaurant6Check myCheck;
	private Restaurant6FoodMenu myMenu;
	private double myMoney;
	private boolean stayCannotAfford;
	private boolean stayRestFull;
	private double debt;
	private boolean haveDebt;
	
	// Agent correspondents
	private Restaurant6Waiter waiter;
	private Restaurant6HostRole host;
	private Restaurant6CashierRole cashier;
	
	// Semaphore for when customer gets to cashier
	private Semaphore atCashier = new Semaphore(0, true);
	
	// Semaphore for when customer gets to cashier
	private Semaphore atPickupSpot = new Semaphore(0, true);
	
	// If the customer is in this state and an event happens, transition to next state
	public enum AgentState
	{WaitingInRestaurant, DoingNothing, WaitingToBeSeated, GoingToPickupSpot, DecideToStayOrNot, BeingSeated, Seated, ReadyToOrder, Ordered, ReOrdered, Eating, Paying, WashingDishes, Leaving};
	
	// Initialize CustomerAgent state
	private AgentState state = AgentState.DoingNothing; //The start state

	// Transitions the customer from one state to another
	public enum AgentEvent 
	{none, gotHungry, willBeSeated, readyToBeSeated, restFull, willStay, followWaiter, seated, finishedReadingMenu, cannotAfford, ordering, reOrdering, receiveFood, receiveCheck, doneEating, donePaying, mustWashDishes, doneWashingDishes, doneLeaving};
	
	// Initialize CustomerAgent event
	AgentEvent event = AgentEvent.none;
	
	// Customer's food option
	private String myChoice;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public Restaurant6CustomerRole(String custName, double custMoney, String choice, boolean stay, boolean stayFull, String info, Person p) {
		super(p);
		
		debt = 0;
		haveDebt = false;
		
		name = custName;
		myMoney = custMoney;
		myChoice = choice;
		stayCannotAfford = stay;
		stayRestFull = stayFull;
		information = info;
	}
	
	public Restaurant6CustomerRole(String custName, String info, Person p) {
		super(p);
		
		debt = 0;
		haveDebt = false;
		
		name = custName;
		information = info;
		myMoney = generateMoney(100,1);
		myChoice = generateChoice();
		stayCannotAfford = generateStay();
		stayRestFull = generateStay();
	}
	
	public Restaurant6CustomerRole(String custName, Person p) {
		super(p);
		
		debt = 0;
		haveDebt = false;
		
		name = custName;
		information = null;
		myMoney = p.getWallet().getOnHand();
		myChoice = generateChoice();
		stayCannotAfford = generateStay();
		stayRestFull = generateStay();
	}

	// Hack to establish connection to Waiter agent
	public void setWaiter(Restaurant6Waiter waiter)  {
		this.waiter = waiter;
	}

	// Hack to establish connection to Host agent
	public void setHost(Restaurant6HostRole host)  {
		this.host = host;
	}
	
	// Hack to establish connection to Cashier agent
	public void setCashier(Restaurant6CashierRole c) {
		this.cashier = c;
	}
	
	// Returns customer name
	public String getCustomerName()  {
		return name;
	}
	
	// Returns the customer's choice
	public String getChoice() {
		return myChoice;
	}
	
	// Returns the customer's debt
	public Double getDebt() {
		return debt;
	}
	
	// Generates the food choice for the customer
	public String generateChoice() {
		int r = generator.nextInt(3);
		if (r == 0)
			myChoice = "Chicken";
		if (r == 1)
			myChoice = "Steak";
		if (r == 2)
			myChoice = "Salad";
		if (r == 3)
			myChoice = "Pizza";
		return myChoice;
	}
	
	// Generate random amount of money if the customer has debt
	public double generateMoney(int max, int min) {
		double r = moneyGenerator.nextInt((max - min) + 1) + min;
		return r; 
	}
	
	/*
	 * Generates true or false if customer will stay after understanding restaurant
	 * is full or if order something cannot afford
	 */
	public boolean generateStay() {
		int s = generator.nextInt(1);
		if (s == 0)
			return false;
		else if (s == 1)
			return true;
		return false;
	}
	
	// Finds the cheapest item on the menu if the customer can't afford choice
	private String findCheapestItem(double money) {
		String temp;
		if (money >= 15.99) {
			temp = "Steak";
		}
		else if (money >= 10.99) {
			temp = "Chicken";
		}
		else if (money >= 8.99) {
			temp = "Pizza";
		}
		else {
			temp = "Salad";
		}
		return temp;
	}
	
	// Messages
	// From animation
	public void gotHungry() {
		print("I'm hungry");
		
		// If the customer has debt, generate a random number enough to pay off that debt
		// Also generate a random choice so the customer can switch it up
		if (debt > 0) {
			int temp = (int)debt + 15;
			myMoney += generateMoney(100, temp);
			myChoice = generateChoice();
		}
		
		print("My money: $" + myMoney);
		event = AgentEvent.gotHungry;
		stateChanged();
	}
	
	// Told by the host to wait in the waiting area
	public void msgPleaseHaveASeat() {
		event = AgentEvent.willBeSeated;
		stateChanged();
	}
	
	// Told by the host that customer is ready to be seated
	public void msgReadyToBeSeated() {
		event = AgentEvent.readyToBeSeated;
		stateChanged();
	}
	
	// Told by the host that the restaurant is full
	public void msgCustomerFull() {
		event = AgentEvent.restFull;
		stateChanged();
	}
	
	// Called by waiter to follow to seat
	public void followMe(int tableNum, Restaurant6FoodMenu menu, Restaurant6Waiter w) {
		event = AgentEvent.followWaiter;
		waiter = w;
		myMenu = menu;
		print("Received msgSitAtTable");
		customerSeat = tableNum;
		stateChanged();
	}

	// Called by the animation when the customer has finished going to seat
	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		print("Sat down at table");
		stateChanged();
	}
	
	// Called by the waiter when the customer is ready to order
	public void whatWouldYouLike() {
		event = AgentEvent.ordering;
		stateChanged();
	}
	
	// Order new item
	public void pleaseOrderSomethingNew(String c) {
		event = AgentEvent.reOrdering;
		cannotOrder = c;
		stateChanged();
	}
	
	// Waiter gives customer the check
	public void hereIsYourCheck(Restaurant6Check c) {
		event = AgentEvent.receiveCheck;
		myCheck = c;
		stateChanged();
	}
	
	// Called by the waiter when customer is being served food
	public void hereIsYourFood(String food) {
		event = AgentEvent.receiveFood;
		stateChanged();
	}
	
	// Called by the cashier to give the customer change
	public void hereIsYourChange(double change) {
		event = AgentEvent.donePaying;
		myMoney = change;
		stateChanged();
	}
	
	// When cashier notifies customer that customer has debt
	public void youHaveDebt(double db) {
		debt = db;
		haveDebt = true;
		stateChanged();
	}
	
	// Called by the animation when the customer GUI has finished leaving  restaurant
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		event = AgentEvent.doneLeaving;
		stateChanged();
	}
	
	// Called by the GUI when customer has reached cashier
	public void msgAtCashier() {
		print("msgAtCashier called");
		atCashier.release();
		stateChanged();
	}
	
	// Called by the GUI when the customer has reached the pickup spot
	public void msgAtPickupSpot() {
		atPickupSpot.release();
		stateChanged();
	}

	/** 
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine	
		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry) {
			state = AgentState.WaitingInRestaurant;
			goToRestaurant();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.restFull) {
			state = AgentState.DecideToStayOrNot;
			stayOrLeave();
			return true;
		}
		if (state == AgentState.DecideToStayOrNot && event == AgentEvent.willBeSeated) {
			state = AgentState.WaitingToBeSeated;
			goToWaiting();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.willBeSeated) {
			state = AgentState.WaitingToBeSeated;
			goToWaiting();
			return true;
		}
		if (state == AgentState.WaitingToBeSeated && event == AgentEvent.readyToBeSeated) {
			state = AgentState.GoingToPickupSpot;
			goToPickupSpot();
		}
		if (state == AgentState.GoingToPickupSpot && event == AgentEvent.followWaiter) {
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		if (state == AgentState.BeingSeated && event == AgentEvent.seated) {
			state = AgentState.Seated;
			readMenu();
			return true;
		}
		if (state == AgentState.Seated && event == AgentEvent.finishedReadingMenu) {
			state = AgentState.ReadyToOrder;
			print("Ready to order");
			callWaiter();
			return true;
		}
		if (state == AgentState.ReadyToOrder && event == AgentEvent.ordering) {
			state = AgentState.Ordered;
			tellWaiterOrder();
			return true;
		}
		if (state == AgentState.Ordered && event == AgentEvent.reOrdering) {
			state = AgentState.ReOrdered;
			tellWaiterNewOrder(cannotOrder);
			return true;
		}
		if ((state == AgentState.Ordered || state == AgentState.ReOrdered) && event == AgentEvent.receiveFood){
			state = AgentState.Eating;
			EatFood();
			return true;					
		}
		if (state == AgentState.Eating && event == AgentEvent.receiveCheck){
			state = AgentState.Paying;
			leaveTable();
			return true;
		}
		if (state == AgentState.Paying && event == AgentEvent.donePaying) {
			state = AgentState.Leaving;
			leaveRestaurant();
			return true;
		}
		if ((state == AgentState.DecideToStayOrNot || state == AgentState.Ordered || state == AgentState.ReOrdered) && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			//no action
			return true;
		}
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving) {
			state = AgentState.DoingNothing;
			return true;
		}
		return false;
	}


	// Actions
	// Customer goes to restaurant when isHungry checkbox is checked 
	private void goToRestaurant() {
		Do("Going to restaurant");
		customerGui.DoGoToRestaurant();
		host.msgIWantFood(this);//send our instance, so he can respond to us
	}
	
	// Customer goes to the waiting area until waiter is ready to pick up customer
	private void goToWaiting() {
		print("Going to the waiting area to be seated.");
		customerGui.DoGoToWaitingArea();
	}
	
	// Customer goes to the pickup spot to meet with waiter
	private void goToPickupSpot() {
		Do("Going to pickup spot");
		customerGui.DoGoToPickupSpot();
		
		atPickupSpot.drainPermits();
		
		try {
			atPickupSpot.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		host.msgCustomerAtFront();
	}

	// Customer decides if wants to stay or leave when the restaurant is full
	private void stayOrLeave() {
		if (stayRestFull) {
			host.wantToStay(this);
			print("It's okay, I want to wait.");
		}
		else {
			host.dontWantToStay(this);
			customerGui.DoExitRestaurant();
			print("I don't want to wait, so I'm going to go. Bye!");
		}
	}
	
	// Customer sits down at table by waiter
	private void SitDown() {
		Do("Being seated. Going to table");
		customerGui.DoGoToSeat(customerSeat);
	}
	
	// Action to read the menu
	private void readMenu() {
		print("Deciding what to eat..");
		// Timer to read menu
		readMenuTimer.schedule(new TimerTask() 
		{
			public void run() 
			{
				event = AgentEvent.finishedReadingMenu;
				stateChanged();
			}
		}, 10000);
	}
	
	// Action to message waiter to come over to table
	private void callWaiter() {
		waiter.readyToOrder(this);
	}

	// Action to message waiter choice of food and set the GUI with the choice icon
	private void tellWaiterOrder() {		
		double cheapestFood = Collections.min(myMenu.getMap().values()); // Get the minimum value of the menu
		String cheapestChoice = myMenu.getReverseMap().get(cheapestFood);
		
		if (cheapestFood < myMoney) { // If the cheapest food is less than the amount of money we have, then continue with eating
			if (myMoney >= myMenu.getMap().get(myChoice)) {
				print("I would like to order " + myChoice);
				waiter.hereIsMyOrder(myChoice, this);
				customerGui.setOrdered(true);
			}
			else if (myMoney < myMenu.getMap().get(myChoice)) {
				myChoice = findCheapestItem(myMoney);
				waiter.hereIsMyOrder(myChoice, this);
				customerGui.setOrdered(true);
			}
		}
		else if (cheapestFood == myMoney) { // If the cheapest food is equal to the amount of money we have, then continue with eating
			myChoice = new String(cheapestChoice);
			print("I would like to order " + myChoice);
			waiter.hereIsMyOrder(myChoice, this);
			customerGui.setOrdered(true);
		}
		else if (cheapestFood > myMoney) { // If cheapest food is more than the amount of money we have, we must decide to leave or stay the restaurant
			if (!stayCannotAfford) { // && numItems == 1) {
				print("I can't afford anything on the menu. Sorry.. bye!");
				waiter.cannotAffordAnything(this);
				customerGui.DoExitRestaurant();
			}
			else if (stayCannotAfford) {
				print("I don't have enough money, but I'm going to continue ordering.");
				waiter.hereIsMyOrder(myChoice, this);
				customerGui.setOrdered(true);
			}
		}
	}
	
	// Action to message waiter choice of food and set the GUI with the choice icon
	private void tellWaiterNewOrder(String notThisChoice) {
		myChoice = generateChoice();
		
		while (myChoice == notThisChoice) { // If the new choice is the same as the one we can't order, keep regenerating choice.
			myChoice = generateChoice();
		}
		
		double choicePrice = myMenu.getMap().get(myChoice); // Get the minimum value of the menu
		
		if (stayCannotAfford) {
			if (choicePrice <= myMoney) { // If the cheapest food is less than the amount of money we have, then continue with eating
				print("I would like to order " + myChoice);
				waiter.hereIsMyOrder(myChoice, this);
				customerGui.setOrdered(true);
			}
			else { // If cheapest food is more than the amount of money we have, we must leave the restaurant
				print("I can't afford anything on the menu. Sorry.. bye!");
				waiter.cannotAffordAnything(this);
				customerGui.setOrdered(false);
				customerGui.DoExitRestaurant();
			}
		}
		else if (!stayCannotAfford) { // If user decides to leave
			print("I don't want to order anything else. Sorry.. bye!");
			waiter.cannotAffordAnything(this);
			customerGui.setOrdered(false);
			customerGui.DoExitRestaurant();
		}
	}
	
	// Action to eat food
	private void EatFood() {
		customerGui.setOrdered(false);
		customerGui.setEating(true);
		Do("Eating Food");
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		timer.schedule(new TimerTask() 
		{
			public void run() 
			{
				print("Done eating");
				event = AgentEvent.doneEating;
				//isHungry = false;
				stateChanged();
			}
		},
		8000);//getHungerLevel() * 1000);//how long to wait before running task
	}

	// Action to leave the table and let the waiter know that the table is empty
	private void leaveTable() {
		customerGui.setEating(false);
		Do("Paying cashier then leaving.");
		waiter.doneEatingAndLeaving(this);
		customerGui.DoGoToCashier();
		
		// Semaphore to make sure customer is at cashier
		try {
			atCashier.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cashier.iWouldLikeToPayPlease(myMoney, this, myCheck);
		customerGui.DoExitRestaurant();
	}
	
	// Leave the restaurant after customer is done paying
	private void leaveRestaurant() {
		DecimalFormat df = new DecimalFormat("##.##");
		print("My debt: $" + df.format(debt));
		print("My bill: $" + df.format(myCheck.getBillAmount()));
		print("Thank you for a lovely meal. I now have $" + df.format(myMoney) + ".");
	}
	
	// Accessors, etc.
	public AgentState getState()
	{
		return state;
	}
	
	public void setState(AgentState st)
	{
		state = st; 
	}
	
	public String getName() {
		return name;
	}
	
	public String getInfo() {
		return information;
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

	public void setGui(Restaurant6CustomerGui g) {
		customerGui = g;
	}

	public Restaurant6CustomerGui getGui() {
		return customerGui;
	}

	// Gets role name
	public String getRoleName() {
		return "Restaurant 6 Customer";
	}
}

