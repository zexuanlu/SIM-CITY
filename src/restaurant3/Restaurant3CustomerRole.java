package restaurant3;

import java.util.*;
import java.util.concurrent.Semaphore;

import agent.Role;
import person.PersonAgent;
import restaurant3.Restaurant3HostRole;
import restaurant3.interfaces.Restaurant3Customer;
import restaurant3.interfaces.Restaurant3Waiter;
import restaurant3.gui.Restaurant3AnimationPanel;
import restaurant3.gui.Restaurant3CustomerGui;

public class Restaurant3CustomerRole extends Role implements Restaurant3Customer {
	//MEMBER DATA
	private String name;
	private int tableNum;
	private boolean isHungry = false;
	private int hungerLevel = 5;
	private Timer timer = new Timer();
	private int choice;
	private String myFood;
	private double money;
	private double bill;
	
	
	int sCount = 0;
	//Enums to keep track of the fsm
	public enum aState {idle, waiting, seated, orderPlaced, doneEating, billPaid, leaving};
	public enum cEvent {none, followWaiter, waiterReady, foodDelivered, billReceived, changeReceived}
	aState state = aState.idle;
	cEvent event = cEvent.none;
	
	//References to agents
	Restaurant3HostRole host;
	Restaurant3Waiter myWaiter;
	
	//GUI references
	Restaurant3CustomerGui custGui;
	
	//Utilities
	Map<String, Double> menu = new HashMap<String, Double>();
	private Semaphore atTable = new Semaphore(0, true);
	
	//CONSTRUCTOR *********************************
	public Restaurant3CustomerRole(String name, PersonAgent pa){
		super(pa);
		this.name = name;
		roleName = "Restaurant 3 Customer";
		money = 100.00;
	}
	
	//HELPER METHODS ******************************
	public String getName(){
		return name;
	}
	
	public String getRoleName(){
		return roleName;
	}
	
	public void setHost(Restaurant3HostRole h){
		this.host = h;
	}
	
	public void setGui(Restaurant3CustomerGui cg){
		this.custGui = cg;
	}

	//MESSAGES ************************************
	@Override
	public void gotHungry() {
		print(name + " got hungry");
		isHungry = true;
		stateChanged();
	}

	@Override
	public void msgFollowMeToTable(Restaurant3Waiter w, int table, Map<String, Double> menu) {
		print(name + ": my waiter is " + w.getName());
		myWaiter = w;
		this.tableNum = table;
		this.menu = menu;
		event = cEvent.followWaiter;
		stateChanged();
	}

	@Override
	public void msgWhatWouldYouLike() {
		print(name + ": " + myWaiter + " is ready to take my order");
		event = cEvent.waiterReady;
		stateChanged();
	}

	@Override
	public void msgHereIsYourFood(String choice) {
		print(name + ": received my food");
		event = cEvent.foodDelivered;
		myFood = choice;
		stateChanged();	
	}

	@Override
	public void msgHereIsYourBill(double bill) {
		print(name + ": received bill from waiter");
		this.bill = bill;
		event = cEvent.billReceived;
		stateChanged();
	}

	@Override
	public void msgHereIsChangeAndReceipt(double change) {
		print(name + ": received change and receipt");
		money += change;
		event = cEvent.changeReceived;
		stateChanged();
	}
	
	@Override
	public void msgAtTableRelease(){
		atTable.release();
		stateChanged();
	}

	//SCHEDULER ***************************************
	@Override
	public boolean pickAndExecuteAnAction() {
		if(isHungry && state == aState.idle && event == cEvent.none){
			print("im about to go restaurant");
			goToRestaurant();
			return true;
		}
		
		//Check if we need to follow waiter
		if(state == aState.waiting && event == cEvent.followWaiter){
			followMyWaiter();
			return true;
		}
		
		//Check if waiter is ready to take order
		if(state == aState.seated && event == cEvent.waiterReady){
			placeOrder();
			return true;
		}
		
		//Check if food is delivered
		if(state == aState.orderPlaced && event == cEvent.foodDelivered){
			eatFood(this);
			return true;
		}
		
		//Check if bill is received
		if(state == aState.doneEating && event == cEvent.billReceived){
			payBill();
			return true;
		}
		
		//Check if change is received
		if(state == aState.billPaid && event == cEvent.changeReceived){
			leaveRestaurant();
			return true;
		}
		
		return false;
	}
	
	//ACTIONS ******************************************
	public void goToRestaurant(){
		print(name + ": going to restaurant with money = " + money);
		state = aState.waiting;
		host.msgIWantFood(this);
		custGui.gotHungry();
	}

	public void followMyWaiter(){
		print(name + ": following waiter to table " + tableNum);
		custGui.DoFollowWaiterToTable(tableNum);	//GUI CODE
		try{
			atTable.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		state = aState.seated;
		myWaiter.msgReadyToOrder(this);
	}
	
	public void placeOrder(){
		print(name + ": deciding order");
		
		while(state == aState.seated){
			choice = (int)(Math.random() * 3);
			//ADD NON NORM CODE HERE
			if(choice == 0){
				if(money > menu.get("Steak")){
					print(name + ": order decided");
					myWaiter.msgHereIsMyChoice(this, "Steak");
					custGui.ordered("Steak");
					state = aState.orderPlaced;
				}
			}
			else if(choice == 1){
				if(money > menu.get("Pizza")){
					print(name + ": order decided");
					myWaiter.msgHereIsMyChoice(this, "Pizza");
					custGui.ordered("Pizza");
					state = aState.orderPlaced;
				}
			}
			else if(choice == 2){
				if(money > menu.get("Chicken")){
					print(name + ": order decided");
					myWaiter.msgHereIsMyChoice(this, "Chicken");
					custGui.ordered("Chicken");
					state = aState.orderPlaced;
				}
			}
			else {
				if(money > menu.get("Salad")){
					print(name + ": order decided");
					myWaiter.msgHereIsMyChoice(this, "Salad");
					custGui.ordered("Salad");
					state = aState.orderPlaced;
				}
			}
		}
	}
	
	public void eatFood(final Restaurant3Customer c){
		print(name + ": eating my " + myFood);
		custGui.eatingFood();
		state = aState.idle;
		timer.schedule(new TimerTask(){
			public void run(){
				state = aState.doneEating;
				myWaiter.msgDoneEatingCheckPls(c);
				custGui.done();
				stateChanged();
			}
		}, (hungerLevel*1000));
	}
	
	public void payBill(){
		print(name + ": paying bill");
		money -= bill;
		state = aState.billPaid;
		myWaiter.msgHereIsMyMoney(this, bill);
	}
	
	public void leaveRestaurant(){
		print(name + ": leaving restaurant with money = " + money);
		myWaiter.msgLeavingTable(this);
		isHungry = false;
		choice = 0;
		myFood = null;
		state = aState.idle;
		event = cEvent.none;
		custGui.DoLeaveRestaurant();	//GUI CODE
	
	}
}
