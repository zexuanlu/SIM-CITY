package restaurant5;
//for part 1 to close stuff
import restaurant5.gui.CustomerGui5;

import java.util.concurrent.Semaphore;

import agent.Role;
import person.PersonAgent; 
import restaurant5.interfaces.Customer5; 
import restaurant5.interfaces.Waiter5; 
import restaurant5.gui.FoodGui5; 

import java.util.Timer;
import java.util.TimerTask;

/**
 * Restaurant customer agent.
 */
public class CustomerAgent5 extends Role implements Customer5 {
	private Semaphore atRestaurant = new Semaphore(0,true);
	PersonAgent myPerson; 
	private enum State{
		paid, waitingforCheck, nothing, waitinginRestaurant,hungry,following, seated,ordered,eating,done
	}
	Timer timer = new Timer();
	CashierAgent5 myCashier; 
	int myCheck; 
	int myMoney;  
	private State customerState;
	FoodGui5 fgui; 
	public CustomerGui5 customerGui; 
	public String name; 
	private enum Action{
		gotChange, gotHungry, followingWaiter, leaving, atSeat, whatdoIwant, choosing, beingServed, becomeFull, seated, receivedCheck, doneLeaving, reorder
	}
	private Action customerAction;

	private HostAgent5 myHost;
	Waiter5 myWaiter;
	String choice; 
	Menu5 myMenu; 

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public CustomerAgent5(String name, PersonAgent p){
		super(p);
		myPerson = p; 
		this.name = name;
		if (name.equals("Broke")){
			myMoney = 6; 
		}
		else if(name.equals("Flake")){
			myMoney = 5; 
		}
		else {
			myMoney = 20;
		}
		customerState = State.nothing;
	}
	
	public void gotHungry(){
		customerAction = Action.gotHungry;
		print("Customer got Hungry");
		myWaiter = null; 
		stateChanged();
	}
	
	public void msgRestaurantFull(){
		int rando = (int)(Math.random() * 2);
		if (rando == 0){
			print ("Customer this restaurant is full so I'm leaving");
			customerAction = Action.leaving; 
			stateChanged();
			return; 
		}
		else {
			print ("Customer I'll wait in line for this restaurant.");
		}
	}
	
	public void msgSendCheck(int Check){
		myCheck = Check; 
		customerAction = Action.receivedCheck; 
		stateChanged();
	}
	
	public void msgChange(int Cash){
		myMoney += Cash; 
		customerAction = Action.gotChange; 
		stateChanged();
	}
	
	public void msgOutofChoice(Menu5 m){
		choice = null; 
		myMenu = m; 
		customerAction = Action.reorder; 
		stateChanged();
	}
	
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
	//	customerAction = Action.doneLeaving;
		//stateChanged();
	}
	
	public void msgAnimationFinishedGoToSeat() {
		//from animation
		customerAction = Action.atSeat;
		stateChanged();
	}
	
	public void msgfollowMe(Waiter5 w, Menu5 m){
		myWaiter = w;
		customerAction = Action.followingWaiter; 
		myMenu = m;
		stateChanged();
	}
	
	public void msgatRestaurant(){
		atRestaurant.release();
	}
	
	public void msgwhatdoyouWant(Waiter5 w){
		customerAction = Action.choosing;
		stateChanged();
	}
	
	public void msgHeresYourOrder(Waiter5 w, String choice){
		customerGui.doHideIcon();
		customerAction = Action.beingServed;
		stateChanged();
	}
	
	public void msgfinishedFood(){
		customerAction = Action.becomeFull; 
		stateChanged();
	}
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		if (customerState == State.paid && customerAction == Action.gotChange){
			LeaveRestaurant();
			return true;
		}
		
		
		if (customerState == State.nothing && customerAction == Action.gotHungry){
			getHungry();
			return true;
		}
		
		if (customerState == State.waitinginRestaurant && customerAction == Action.leaving){
			LeaveRestaurant();
			return true; 
		}
		
		if (customerState == State.waitinginRestaurant && customerAction == Action.followingWaiter){
			follow();
			return true;
		}
		
		if (customerState == State.following && customerAction == Action.atSeat){
			readytoOrder();
		}
		
		if (customerState == State.seated && customerAction == Action.choosing){
			sendChoice();
			return true;
		}
		
		if (customerState == State.ordered && customerAction == Action.beingServed){
			eatFood();
			return true;
		}
		
		if (customerState == State.ordered && customerAction == Action.reorder){
			readytoOrder();
			return true;
		}
		
		if (customerState == State.waitingforCheck && customerAction == Action.receivedCheck){
			payCheck();
			return true;
		}
		
		if (customerAction == Action.becomeFull && customerState == State.eating){
			DonewithFood();
			return true;
		}
		
		return false;
	}

	// Actions

	private void getHungry(){
		print("Customer wants food.");
		customerState = State.waitinginRestaurant;
		atRestaurant.drainPermits();
		customerGui.enterRestaurant();
		try {
			atRestaurant.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		myHost.msgIWantFood(this);
	}
	
	private void follow(){
	//	doFollow(w); //animation
		int sitat = (myWaiter).getPlace(this);
		customerState = State.following;
		customerGui.DoGoToSeat(sitat);//hack; only one table
	}
	
	private void readytoOrder(){//this is where logically if he can't afford it he leaves
		if (myMenu.getSize() == 0){ //if there's nothing to order
			print ("Customer there's nothing on the Menu");
			customerGui.doHideIcon();
			LeaveRestaurant();
			return; 
		}
		if (myMoney < myMenu.getCheapest() && !name.equals("Flake")){ //if he doesn't have enough money, he'll leave
			print ("Customer can't afford anything"); 
			customerGui.doHideIcon();
			LeaveRestaurant();
			return; 
		}
		else {	
			print("Customer ready to order");
			customerState= State.seated;
			myWaiter.msgReadytoOrder(this);
		}
	}
	
	private void sendChoice(){ 
		print("Customer here is choice");
		 	if (!name.equals("Flake")){
				do{
				choice = pickChoice();
				}while(myMoney < myMenu.prices.get(choice)); //makes him order what he can afford
		 	}
		 	else {
		 		choice = pickChoice();
		 	}
			customerGui.setChoice(choice);
			customerGui.doShowIcon();
			customerState = State.ordered;
			myWaiter.msghereisChoice(this, choice);

	}
	
	private String pickChoice(){
		if (name.equals("Steak")||name.equals("Chicken")||name.equals("Pizza")||name.equals("Salad")){
			if (myMenu.find(name)){
				return name; 
			}
		}
			int rando = (int)(Math.random() * (myMenu.getSize()));
			String _choice = myMenu.at(rando);
			return _choice; 
	}
	
	private void eatFood(){
		customerState = State.eating;
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				print("Done eating, cookie=" + cookie);
				msgfinishedFood(); 
				stateChanged();
			}
		},
		5000);
	}
	

	private void LeaveRestaurant(){
		//customerGui.DoExitRestaurant();
		customerGui.Leave();
		customerState = State.nothing;
		if (name.equals("Flake")){
			myMoney += 10; //has to pay flaked amount;
		}
		if (!name.equals("Broke")){
			myMoney += 10; //gives customer enough money to return
		}
		if (myWaiter != null){
			print("Customer done and leaving");
			myWaiter.msgDoneandLeaving(this);
		}
		else {
			print("Customer leaving");
			myHost.msgLeaving(this);
		}
	}
	
	private void payCheck(){
		if (myMoney >= myCheck){
			myMoney = myMoney - myCheck; //assumption that you have enough money right now
			customerState = State.paid;
			print("Customer make payment of "+ myCheck);
			myCashier.msgPayment(this, myCheck);
		}
		else { //if you're a flake
			int temp = myMoney;
			customerState = State.paid; 
			myMoney = 0; //paid all of Money;
			print ("Customer could not make full payment of " + myCheck + " so paid only "+ temp);
			myCashier.msgPayment(this, temp);		
		}
	}
	
	private void DonewithFood(){
		customerState = State.waitingforCheck; 
		print ("Customer done eating");
		myWaiter.msgDoneEating(this);
	}
	
	// Accessors, etc.
	
	public void setFoodGui(FoodGui5 f){
		fgui = f; 
	}
	
	public FoodGui5 getFoodGui(){
		return fgui; 
	}
	
	public CustomerGui5 getGui() {
		return customerGui;
	}

	public String getName() {
		return name;
	}
	
	public void setGui(CustomerGui5 g) {
		customerGui = g;
	}
	
	public void setHost(HostAgent5 host) {
		this.myHost = host;
	}
	
	public void setCashier(CashierAgent5 c){
		myCashier = c; 
	}

	public String getRoleName(){
		return "Restaurant 5 Customer";
	}

	@Override
	public void msgGotHungry() {
		// TODO Auto-generated method stub
		
	}
	
}

