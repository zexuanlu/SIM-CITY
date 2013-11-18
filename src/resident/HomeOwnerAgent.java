package resident;

import java.text.DecimalFormat;
import java.util.ArrayList; 
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import resident.interfaces.HomeOwner;
import resident.interfaces.MaintenancePerson;
import resident.test.mock.EventLog;
import resident.test.mock.LoggedEvent;
import agent.Role;

public class HomeOwnerAgent extends Role implements HomeOwner {
	
	// For the purposes of JUnit testing
	public EventLog log = new EventLog();
	
	/**
	 * Data for Homeowner
	 * @author jenniezhou
	 *
	 */
	// Constructor
	public HomeOwnerAgent(String n, int hn) {
		super();
		name = n;
		houseNumber = hn;
	}
	
	// Returns the name of the home owner
	public String getName() {
		return name;
	}
	
	// Returns the house number of the home owner
	public int getHouseNumber() {
		return houseNumber;
	}
	
	// Sets the amount of money for the home owner
	public void setMoney(double amt) {
		myMoney = amt;
	}
	
	// Returns the amount of money for the home owner
	public double getMoney() {
		return myMoney;
	}
	
	// Sets the amount of time that the home owner has
	public void setTime(int t) {
		myTime = t;
	}
	
	public int getTime() {
		return myTime;
	}
	
	public static class MyPriority {
		public enum Task {NeedToEat, Cooking, Eating, WashDishes, Washing, CallHousekeeper, LetHousekeeperIn, PayHousekeeper, GoToMarket, RestockFridge, RestockFridgeThenCook, GoToRestaurant, NoFood}
		public Task task;
		public int timeDuration;
//		Map<Task, Double> taskTime; // Will have times preinitialized 
		Map<Task, Integer> taskTime = new HashMap<Task, Integer>(); // Will have importance preinitialized

		MyPriority(Task t) {
			task = t;
			
			// Initializing all of the tasks and their times
			taskTime.put(Task.NeedToEat, 0);
			taskTime.put(Task.Cooking, 30);
			taskTime.put(Task.Eating, 30);
			taskTime.put(Task.WashDishes, 0);
			taskTime.put(Task.Washing, 10);
			taskTime.put(Task.CallHousekeeper, 5);
			taskTime.put(Task.LetHousekeeperIn, 5);
			taskTime.put(Task.PayHousekeeper, 5);
			taskTime.put(Task.GoToMarket, 20);
			taskTime.put(Task.RestockFridge, 5);
			taskTime.put(Task.RestockFridgeThenCook, 35);
			taskTime.put(Task.GoToRestaurant, 40);
			taskTime.put(Task.NoFood, 0);
			
			timeDuration = taskTime.get(t);
//			levelOfImportance = taskImportance.get(t);
		}
	}

	public static class MyFood {
		private String foodItem;
		private int foodAmount;

		public MyFood(String f, int a) {
			foodItem = f;
			foodAmount = a;
		}
		
		public String getFoodItem() {
			return foodItem;
		}
		
		public int getFoodAmount() {
			return foodAmount;
		}
	}

	public List<MyPriority> toDoList = Collections.synchronizedList(new ArrayList<MyPriority>());
	public List<MyFood> myFridge = Collections.synchronizedList(new ArrayList<MyFood>());
	private Timer cookingTimer; // Times the food cooking
	private Timer eatingTimer;
	private Timer washingDishesTimer;
	private int houseNumber;
	private String name;
	private double myMoney;
	private double debt;
	private double maintenanceCost; // Static for now.
	private int myTime; // Keeps track of how much time the resident has
	private static int minCookingTime = 70; // Time it takes to cook the fastest food
	private MaintenancePerson housekeeper;
	
	// Hack to establish connection between maintenance person and home owner
	public void setMaintenance(MaintenancePerson m) {
		housekeeper = m;
	}
	
	/**
	 * Messages for Homeowner
	 * @author jenniezhou
	 *
	 */
	public void msgGotHungry() {
		// Add eating to the list of priorities that the resident has
		toDoList.add(new MyPriority(MyPriority.Task.NeedToEat));
		
		// Log that the message has been received
		log.add(new LoggedEvent("I'm hungry."));
		
		stateChanged();
	}

	public void msgFoodDone() {
		// Add getting cooked food to the list of priorities 
		toDoList.add(new MyPriority(MyPriority.Task.Eating));
		
		log.add(new LoggedEvent("My food is ready! I can eat now."));
		
		stateChanged();
	}

	public void msgDoneEating() {
		// Add washing dishes to the list of priorities
		toDoList.add(new MyPriority(MyPriority.Task.WashDishes));
		
		log.add(new LoggedEvent("Done eating. I'm going to wash dishes now."));
		
		stateChanged();
	}

	public void msgDoneWashing(MyPriority p) {
		// Removes washing from the list of priorities
		toDoList.remove(p);
		
		log.add(new LoggedEvent("Done washing dishes!"));
		
		stateChanged();
	}

	public void msgDoneGoingToMarket(List<MyFood> groceries) {
		// Add restocking fridge
		for (MyPriority p : toDoList) {
			if (p.task == MyPriority.Task.GoToMarket) {
				// If the customer has just finished going to the market, restock the fridge and then cook
				log.add(new LoggedEvent("I just finished going to the market. Time to put all my groceries in the fridge."));
				toDoList.remove(p);
				toDoList.add(new MyPriority(MyPriority.Task.RestockFridgeThenCook));
			}
		}

		for (MyFood f : groceries) {
			myFridge.add(new MyFood(f.foodItem, f.foodAmount));
		}

		stateChanged();
	}

	public void msgDoneEatingOut(List<MyFood> groceries) {
		for (MyPriority p : toDoList) {
			if (p.task == MyPriority.Task.GoToRestaurant) {
				// If the customer has just finished going to the market, just restock the fridge
				toDoList.remove(p);
				toDoList.add(new MyPriority(MyPriority.Task.RestockFridge));
			}
		}

		for (MyFood f : groceries) {
			myFridge.add(new MyFood(f.foodItem, f.foodAmount));
		}

		stateChanged();
	}
	
	public void msgMaintainHome() {
		// Adds calling housekeeper to the list of priorities
		toDoList.add(new MyPriority(MyPriority.Task.CallHousekeeper));
		
		log.add(new LoggedEvent("It's been a day. I need to call the housekeeper now!"));
		
		stateChanged();
	}
	
	public void msgReadyToMaintain() {
		// Adds letting housekeeper into the home to list of priorities
		toDoList.add(new MyPriority(MyPriority.Task.LetHousekeeperIn));
		
		log.add(new LoggedEvent("The housekeeper is here, so I need to let him or her in."));
		
		stateChanged();
	}

	/*public void msgYouHaveDebt(double amount) {
		debt += amount;
		stateChanged();
	}*/

	public void msgDoneMaintaining(double amount) {
		toDoList.add(new MyPriority(MyPriority.Task.PayHousekeeper));
		maintenanceCost = amount;
		
		log.add(new LoggedEvent("I received the housekeeper's bill of " + maintenanceCost + "."));
		
		stateChanged();
	}

	public void msgReceivedPayment(double amount) {
		DecimalFormat df = new DecimalFormat("###.##");
		
		if (amount == 0) {
			debt = 0;
		}
		else {
			debt += amount;
		}
		
		log.add(new LoggedEvent("I now have debt of $" + df.format(debt) + "."));
	}

	/**
	 * Scheduler for Homeowner
	 * @author jenniezhou
	 *
	 */
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if (!toDoList.isEmpty()) {
			for (MyPriority p : toDoList) { // Eating is the most important
				if (p.task == MyPriority.Task.NeedToEat) {
					checkFridge(p);
					return true;
				}
			}
			for (MyPriority p : toDoList) { // Assuming house needs to be maintained every week, and global timer 10 is equivalent to one day
				if (p.task == MyPriority.Task.CallHousekeeper) {
					callHousekeeper(p);
					return true;
				}
			}
			for (MyPriority p : toDoList) {
				if (p.task == MyPriority.Task.LetHousekeeperIn) {
					letHousekeeperIn(p);
					return true;
				}
			}
			for (MyPriority p : toDoList) {
				if (p.task == MyPriority.Task.PayHousekeeper) {
					payHousekeeper(p);
					return true;
				}
			}
			for (MyPriority p : toDoList) { // If fridge is empty
				if (p.task == MyPriority.Task.NoFood) {
					decideMarketOrGoOut(p);
					return true;
				}
			}
			for (MyPriority p : toDoList) {
				if (p.task == MyPriority.Task.RestockFridgeThenCook) {
					restockFridgeThenCook(p);
					return true;
				}
			}
			for (MyPriority p : toDoList) {
				if (p.task == MyPriority.Task.RestockFridge) {
					restockFridge(p);
					return true;
				}
			}
			for (MyPriority p : toDoList) {
				if (p.task == MyPriority.Task.Cooking) {
					cookFood(p);
					return true;
				}
			}
			for (MyPriority p : toDoList) {
				if (p.task == MyPriority.Task.Eating) {
					eatFood(p);
					return true;
				}
			}
			for (MyPriority p : toDoList) {
				if (p.task == MyPriority.Task.WashDishes) {
					washDishes(p);
					return true;
				}
			}
		}
		return false;
	}
		
	/**
	 * Actions for Homeowner
	 */
	private void checkFridge(MyPriority p) {
		toDoList.remove(p);

//		DoGoToFridge(); // GUI goes to the fridge

		// Semaphore to see if the GUI gets to the fridge

		if (myFridge.isEmpty()) { // Checks to see if the list is empty
			// Adds going to the market or restaurant to the list
			toDoList.add(new MyPriority(MyPriority.Task.NoFood));
			log.add(new LoggedEvent("My fridge has no food. I must now decide if I should go to the market or go out to eat."));
		}
		else { // Cook the food
			toDoList.add(new MyPriority(MyPriority.Task.Cooking));
			log.add(new LoggedEvent("My fridge has food. I can cook now!"));
		}	
	}

	private void decideMarketOrGoOut(MyPriority p) {
		toDoList.remove(p);

		if (myTime > minCookingTime) { 
			toDoList.add(new MyPriority(MyPriority.Task.GoToMarket)); 
			
			log.add(new LoggedEvent("I'm going to go to the market. I have enough time to go and come home."));
			
//			DoGoToMarket(); // GUI will go to market
		}
		else { 
			toDoList.add(new MyPriority(MyPriority.Task.GoToRestaurant));
			
			log.add(new LoggedEvent("I don't have enough time to cook. I'm going to go to the restaurant instead."));
			
//			DoGoToMarketThenRestaurant(); // GUI will go to market then restaurant
		}
	}

	private void restockFridgeThenCook(MyPriority p) {
		toDoList.remove(p);

//		DoGoToFridge(); // GUI will go to the fridge 

		// Semaphore to determine if GUI arrived at fridge

		toDoList.add(new MyPriority(MyPriority.Task.Cooking));
	}

	private void restockFridge(MyPriority p) {
		toDoList.remove(p);

//		DoGoToFridge(); // GUI will go to the fridge 

		// Semaphore to determine if GUI arrived at fridge
	}

	private void cookFood(MyPriority p) {
		toDoList.remove(p);

		int max = -1;
		String maxChoice = null;
		int index = -1;

		for (MyFood f : myFridge) { // Searches for the food item with the most inventory
			if (f.foodAmount > max) {
				max = f.foodAmount;
				maxChoice = f.foodItem;
			}
		}

		for (MyFood f : myFridge) { // Searches for and decreases the amount of food for the one with the most inventory
			if (f.foodItem == maxChoice) {
				--f.foodAmount;
				++index;
				log.add(new LoggedEvent("I'm going to cook " + f.foodItem + ". My inventory of it is now " + f.foodAmount + "."));
				break;
			}
		}
		
		// If there's no more of an item after the resident has removed it, then 
		if (myFridge.get(index).foodAmount == 0) {
			myFridge.remove(index);
			log.add(new LoggedEvent("My fridge has no more " + maxChoice + "."));
		}

//		DoGoToStove(); // GUI animation to go to the stove and start cooking
//
//		// Semaphore to determine if the GUI has gotten to the stove location
//
//		cookingTimer.start{msgFoodDone()};
	}

	private void eatFood(MyPriority p) {
		toDoList.remove(p);

//		DoGoToStove(); // GUI animation to go to stove
//
//		// Semaphore to determine if the GUI has gotten to the stove location
//
//		DoGoToDiningTable(); // GUI animation to go to the dining table
//
//		// Semaphore to determine if the GUI has gotten to the table location
//
//		eatingTimer.start{msgDoneEating()};
	}

	private void washDishes(MyPriority p) {
		toDoList.remove(p);

		toDoList.add(new MyPriority(MyPriority.Task.Washing));

//		DoGoToSink(); // GUI animation to go to the sink
//
//		// Semaphore to determine if the GUI has arrived at sink location
//
//		washingDishesTimer.start{msgDoneWashing(p)};
	}

	private void callHousekeeper(MyPriority p) {
		toDoList.remove(p);
		housekeeper.msgPleaseComeMaintain(this, houseNumber);
	}
	
	private void letHousekeeperIn(MyPriority p) {
		toDoList.remove(p);
		housekeeper.msgPleaseComeIn(this, houseNumber);
	}

	private void payHousekeeper(MyPriority p) {
		toDoList.remove(p);
		if (myMoney >= maintenanceCost) {
			housekeeper.msgHereIsThePayment(this, maintenanceCost);
			myMoney -= maintenanceCost;

		}
		else {
			housekeeper.msgHereIsThePayment(this, myMoney);
			myMoney = 0;
		}
	}
}
