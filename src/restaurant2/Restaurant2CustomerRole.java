package restaurant2;

import restaurant2.gui.CustomerGui;
import restaurant2.interfaces.Customer;
import restaurant2.interfaces.Waiter;
import agent.Agent;
import agent.Role;

import java.awt.Container;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import person.interfaces.Person;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;
/**
 * Restaurant customer agent.
 */
public class Restaurant2CustomerRole extends Role implements Customer{
	private String name;
	Random chooser = new Random();
	private int hungerLevel = 5;  // determines length of meal
	private int tableAt = 0;
	private int wallet;
	private int check;
	private Semaphore waitForWaiter = new Semaphore(0, true);
	private Semaphore waitForPayment = new Semaphore(0, true);
	Timer timer = new Timer();
	Timer thinkTimer = new Timer();
	private CustomerGui customerGui;
	private Waiter waiter;
	// agent correspondents
	private Restaurant2HostRole host;
	private Restaurant2CashierRole cashier;
	private Menu menu;
	private String mealChoice;
	//private List<String> restrictions = new ArrayList<String>();
	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, WaitingInRestaurant, MustWait, BeingSeated, Seated, Thinking, readyToOrder, Ordering, NotEnoughMoney, WaitingForFood, Eating, DoneEating, Paying, Leaving};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, followHost, seated, readyToOrder, leavingWithoutOrdering, ordering, served, doneEating, donePaying, doneLeaving, waiting};
	AgentEvent event = AgentEvent.none;
	//make a slot for the menu whitch will be its own class 
	//private Menu menu;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public Restaurant2CustomerRole(String name, Person p){
		super(p);
		this.name = name;
		wallet = 20;//ideally we will make this random
	}
	/**
	 * hack to establish connection to Host agent.
	 */
	public void setHost(Restaurant2HostRole host) {
		this.host = host;
	}
	public void setWaiter(Waiter waiter){
		this.waiter = waiter;
	}
	public void setCashier(Restaurant2CashierRole cashier){
		this.cashier = cashier;
	}
	public String getCustomerName() {
		return name;
	}
	public void setMenu(Menu m){
		this.menu = m;
	}
	public boolean readyToOrder(){
		if(state == AgentState.readyToOrder){
			return true;
		}
		else 
			return false;
	}
	public boolean hasOrdered(){
		if(state == AgentState.WaitingForFood){
			return true;
		}
		else 
			return false;
	}
	public int getTableAt(){
		return tableAt;
	}
	// Messages

	public void gotHungry() {//from animation
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgSitAtTable(Waiter waiter, int tableNum, Menu menu) {
		print("Received msgSitAtTable");

		tableAt = tableNum;
		setWaiter(waiter);
		setMenu(menu);

		event = AgentEvent.followHost;
		stateChanged();
	}
	public void msgYouHaveToWait()
	{
		print("im sorry from the wait! feel free to leave if need be");
		event = AgentEvent.waiting;
	}
	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		event = AgentEvent.doneLeaving;
		stateChanged();
	}
	public void msgWhatWouldYouLike(){
		waitForWaiter.release();
		stateChanged();
	}
	public void msgReOrder(){
		state = AgentState.readyToOrder;
		event = AgentEvent.ordering;
		mealChoice = null;
		stateChanged();
	}
	public void msgServed(String order)
	{
		System.out.println("Thank you for the "+order);
		event = AgentEvent.served;
		stateChanged();
	}
	public void msgHeresYourCheck(int check)
	{
		this.check = check;
		stateChanged();
	}
	public void msgAtCashier()
	{
		if(state != AgentState.NotEnoughMoney || state != AgentState.DoingNothing)
		{
			waitForPayment.release();
			print("At the cashier paying for meal");
			event = AgentEvent.donePaying;
			stateChanged();
		}
	}
	public void msgThanksForDining()//from the cashier
	{
		event = AgentEvent.doneLeaving;
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
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followHost ){
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		if (state == AgentState.BeingSeated && event == AgentEvent.seated){
			state = AgentState.Thinking;
			whatShouldIOrder();
			return true;
		}
		if(state == AgentState.Thinking && event == AgentEvent.readyToOrder){
			state = AgentState.readyToOrder;
			imReadyToOrder();
			return true;
		}
		if (state == AgentState.readyToOrder && event == AgentEvent.ordering){
			state = AgentState.WaitingForFood;
			Order();
			return true;
		}	
		if(state == AgentState.NotEnoughMoney && event == AgentEvent.leavingWithoutOrdering)
		{
			state = AgentState.DoingNothing;
			leaveTableNoPayment();
			return true;
		}
		if(state == AgentState.WaitingForFood && event == AgentEvent.served){
			state = AgentState.Eating;
			EatFood();
			return true;
		}
		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			state = AgentState.Paying;//used to be Leaving state
			leaveTable();
			return true;
		}
		//add states for payment sequence
		if(state == AgentState.Paying && event == AgentEvent.donePaying){
			state = AgentState.Leaving;
			payForMeal();
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

	private void gamble()
	{
		int leave = chooser.nextInt(6);
		if(leave == 2)
		{
			leaveTable();
		}
	}
	private void goToRestaurant() {
		Do("Going to restaurant");
		host.msgIWantFood(this);//send our instance, so he can respond to us
	}
	private void imReadyToOrder(){
		customerGui.changeText("!");
		waiter.msgReadyToOrder((Customer) this, tableAt);
		event = AgentEvent.ordering;
		try {
			waitForWaiter.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//stateChanged();
	}
	private void SitDown() {
		Do("Being seated. Going to table");
		customerGui.DoGoToSeat(tableAt);//hack; only one table
	}
	private void Order(){
		if(decide()){
			waiter.msgOrder((Customer) this, mealChoice);
			customerGui.changeText(mealChoice+"?");
		}
		else{
			print("Too damn expensive!");
			stateChanged();
		}
	}
	private void EatFood() {
		Do("Eating Food");
		customerGui.changeText("Thanks!");
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				event = AgentEvent.doneEating;
				customerGui.changeText(" ");
				stateChanged();
			}
		},
		10000);
	}
	private boolean decide()
	{
		String choice = menu.chooseMeal();
		if(choice.equals("none"))
		{
			state = AgentState.NotEnoughMoney;
			event = AgentEvent.leavingWithoutOrdering;
			return false;
		}
		if(menu.checkPrice(choice, wallet) || name.equalsIgnoreCase("flake"))
		{
			mealChoice = choice;
			return true;
		}
		else
		{
			menu.restrict(choice);
			print("deciding again "+choice);
			decide();
		}
		state = AgentState.NotEnoughMoney;
		event = AgentEvent.leavingWithoutOrdering;
		return false;
	}
	private void whatShouldIOrder()
	{
		print("thinking about what to get");
		customerGui.changeText("...");
		Random thinkingTime = new Random();
		int time = thinkingTime.nextInt(4) * 10000;
		if(time == 0)//since nextInt is inclusive of the 0 value
		{
			time = 10000;
		}
		thinkTimer.schedule(new TimerTask() {
			public void run(){
				if(decide()){
					print("i've chosen");
					event = AgentEvent.readyToOrder;
					stateChanged();
				}
				else{
					print("Too damn expensive!");
					stateChanged();
				}
			}
		}, 1000);

	}
	private void leaveTable() {
		Do("Leaving.");
		customerGui.changeText("$");
		waiter.msgLeavingTable((Customer) this);
		customerGui.DoExitRestaurant();
		try {
			waitForPayment.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void leaveTableNoPayment()
	{
		Do("I dont have the money to eat here");
		customerGui.changeText("$ :(");
		waiter.msgLeavingTableNoOrder((Customer) this);
		customerGui.DoExitRestaurant();

	}
	private void payForMeal()
	{
		if(!this.name.equals("flake lord")){
			cashier.msgPayment((Customer) this, check);
		}
		else{
			cashier.msgPayment(this, 0);
		}
		//wallet -= check.getCheck();
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

	public void setGui(CustomerGui g) {
		customerGui = g;
	}

	public CustomerGui getGui() {
		return customerGui;
	}
	@Override
	public void msgHeresYourCheck(Check check) {
		// TODO Auto-generated method stub

	}
	@Override
	public void msgRepay() {
		print("Your a filthy thief and you will be killed on sight if you try to enter here again");

	}
	@Override
	public String getRoleName() {
		// TODO Auto-generated method stub
		return null;
	}
}

