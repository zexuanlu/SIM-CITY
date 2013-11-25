package resident;

import java.text.DecimalFormat;
import java.util.ArrayList; 
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import market.Food;
import person.Event;
import person.Location;
import person.Position;
import person.Event.EventType;
import person.interfaces.Person;
import resident.gui.HomeOwnerGui;
import resident.interfaces.HomeOwner;
import resident.test.mock.EventLog;
import resident.test.mock.LoggedEvent;
import agent.Agent;
import agent.Role;

public class HomeOwnerRole extends Role implements HomeOwner {
	
	// For the purposes of JUnit testing
	public EventLog log = new EventLog();
	
	/**
	 * Data for Homeowner
	 * @author jenniezhou
	 *
	 */
	// Constructor
	public HomeOwnerRole(Person p, String n, int hn) {
		super(p);
		name = n;
		houseNumber = hn;
		person = p;
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
		public enum Task {NeedToEat, Cooking, Eating, WashDishes, Washing, MaintainHome, GoToMarket, RestockFridge, GoToRestaurant, NoFood}
		public Task task;
		public int timeDuration;
		private Map<Task, Integer> taskTime = new HashMap<Task, Integer>(); // Will have importance preinitialized

		MyPriority(Task t) {
			task = t;
			
			// Initializing all of the tasks and their times
			taskTime.put(Task.NeedToEat, 0);
			taskTime.put(Task.Cooking, 30);
			taskTime.put(Task.Eating, 30);
			taskTime.put(Task.WashDishes, 0);
			taskTime.put(Task.Washing, 10);
			taskTime.put(Task.MaintainHome, 30);
			taskTime.put(Task.GoToMarket, 20);
			taskTime.put(Task.RestockFridge, 5);
			taskTime.put(Task.GoToRestaurant, 40);
			taskTime.put(Task.NoFood, 0);
			
			timeDuration = taskTime.get(t);
//			levelOfImportance = taskImportance.get(t);
		}
	}

	public List<MyPriority> toDoList = Collections.synchronizedList(new ArrayList<MyPriority>());
	public List<Food> myFridge = Collections.synchronizedList(new ArrayList<Food>());
	private Timer cookingTimer = new Timer(); // Times the food cooking
	private Timer eatingTimer = new Timer();
	private Timer washingDishesTimer = new Timer();
	private Timer sleepingTimer = new Timer();
	private int houseNumber;
	private String name;
	private double myMoney;
	private double debt;
	private double maintenanceCost; // Static for now.
	private int myTime; // Keeps track of how much time the resident has
	private static int minRestaurantMoney = 70; // Time it takes to cook the fastest food
	private static int hungerThreshold = 3;
	//private MaintenancePerson housekeeper;
	private Person person;
	private HomeOwnerGui homeGui;
	
	// All the gui semaphores
	private Semaphore atFridge = new Semaphore(0, true);
	private Semaphore atFrontDoor = new Semaphore(0, true);
	private Semaphore waitForReturn = new Semaphore(0, true);
	private Semaphore atStove = new Semaphore(0, true);
	private Semaphore atTable = new Semaphore(0, true);
	private Semaphore atSink = new Semaphore(0, true);
	private Semaphore atBed = new Semaphore(0, true);
	
	// Hack to establish connection between maintenance person and home owner
	/*public void setMaintenance(MaintenancePerson m) {
		housekeeper = m;
	}*/
	
	// Hack to establish connection between GUI and role
	public void setGui(HomeOwnerGui gui) {
		homeGui = gui;
	}
	
	/**
	 * Messages for Homeowner
	 * @author jenniezhou
	 *
	 */
	public void updateVitals(int hunger, int timer) {
		if (hunger >= hungerThreshold) {
			// Add eating to the list of priorities that the resident has
			toDoList.add(new MyPriority(MyPriority.Task.NeedToEat));
			
			// Log that the message has been received
			log.add(new LoggedEvent("I'm hungry."));
			
			print("I'm hungry");
			
			stateChanged();
		}
	}

	public void msgFoodDone() {
		// Add getting cooked food to the list of priorities 
		toDoList.add(new MyPriority(MyPriority.Task.Eating));
		
		log.add(new LoggedEvent("My food is ready! I can eat now."));
		
		print("My food is ready! I can eat now.");
		
		stateChanged();
	}

	public void msgDoneEating() {
		// Add washing dishes to the list of priorities
		toDoList.add(new MyPriority(MyPriority.Task.WashDishes));
		
		log.add(new LoggedEvent("Done eating. I'm going to wash dishes now."));
		
		print("Done eating. I'm going to wash dishes now.");
		
		stateChanged();
	}

	public void msgDoneWashing(MyPriority p) {
		// Removes washing from the list of priorities
		toDoList.remove(p);
		
		log.add(new LoggedEvent("Done washing dishes!"));
		
		print("Done washing dishes!");
		
		stateChanged();
	}

	public void msgDoneGoingToMarket(List<Food> groceries) {		
		waitForReturn.release();
		
		// If the customer has just finished going to the market, restock the fridge and then cook
		log.add(new LoggedEvent("I just finished going to the market. Time to put all my groceries in the fridge."));
		
		print("I just finished going to the market. Time to put all my groceries in the fridge.");
		
		// Add restocking fridge to the to do list
		toDoList.add(new MyPriority(MyPriority.Task.RestockFridge));		

		for (Food f : groceries) {
			myFridge.add(new Food(f.choice, f.amount));
		}

		stateChanged();
	}

	public void msgDoneEatingOut() {
		waitForReturn.release();
		
		log.add(new LoggedEvent("I just finished eating out. I'm full now!"));
		
		print("I just finished eating out. I'm full now!");
		
		stateChanged();
	}
	
	public void msgMaintainHome() {
		// Adds calling housekeeper to the list of priorities
		toDoList.add(new MyPriority(MyPriority.Task.MaintainHome));
		
		log.add(new LoggedEvent("It's been a day. I need to clean my house!"));
		
		print("It's been a day. I need to clean my house!");
		
		stateChanged();
	}
	
	// GUI semaphore release messages
	// For when the home owner is at the fridge
	public void msgAtFridge() {
		atFridge.release();
	}
	
	// For when home owner is at the door
	public void msgAtDoor() {
		atFrontDoor.release();
	}
	
	// For when the home owner is at the stove
	public void msgAtStove() {
		atStove.release();
	}
	
	// For when the home owner has reached the dining table
	public void msgAtTable() {
		atTable.release();
	}
	
	// For when the home owner has reached the sink
	public void msgAtSink() {
		atSink.release();
	}
	
	// For when the home owner has reached the bed
	public void msgAtBed() {
		atBed.release();
	}

	/**
	 * Scheduler for Homeowner
	 * @author jenniezhou
	 *
	 */
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
//		if (!person.toDo.peek() && myTime >= 20 && hunger <= 2) {
//		sleep();
//		return true;
//	}
		if (!toDoList.isEmpty()) {
			for (MyPriority p : toDoList) { // Eating is the most important
				if (p.task == MyPriority.Task.NeedToEat) {
					checkFridge(p);
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
				if (p.task == MyPriority.Task.GoToRestaurant) {
					goToRestaurant(p);
					return true;
				}
			}
			for (MyPriority p : toDoList) {
				if (p.task == MyPriority.Task.GoToMarket) {
					goToMarket(p);
					return true;
				}
			}
			for (MyPriority p : toDoList) { // Assuming house needs to be maintained every day
				if (p.task == MyPriority.Task.MaintainHome) {
					maintainHome(p);
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
	private void sleep() {
		// Gui goes to bed and timer begins to start sleeping
		homeGui.DoGoToBed();
		
		sleepingTimer.schedule(new TimerTask() 
        {
            public void run() 
            {
            	updateVitals(3, 7);
            }
        }, 10000);
	}
		
	private void checkFridge(MyPriority p) {
		toDoList.remove(p);
		
		// GUI goes to the fridge
		homeGui.DoGoToFridge();

		// Semaphore to see if the GUI gets to the fridge
		try {
			atFridge.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (myFridge.isEmpty()) { // Checks to see if the list is empty
			// Adds going to the market or restaurant to the list
			toDoList.add(new MyPriority(MyPriority.Task.NoFood));
			log.add(new LoggedEvent("My fridge has no food. I must now decide if I should go to the market or go out to eat."));
			print("My fridge has no food. I must now decide if I should go to the market or go out to eat.");
		}
		else { // Cook the food
			toDoList.add(new MyPriority(MyPriority.Task.Cooking));
			log.add(new LoggedEvent("My fridge has food. I can cook now!"));
			print("My fridge has food. I can cook now!");
		}	
	}

	private void decideMarketOrGoOut(MyPriority p) {
		toDoList.remove(p);

		if (myMoney < minRestaurantMoney) { 
			toDoList.add(new MyPriority(MyPriority.Task.GoToMarket)); 
			toDoList.add(new MyPriority(MyPriority.Task.Cooking));
			
			log.add(new LoggedEvent("I'm going to go to the market. I have enough time to go and come home."));
			
			print("I'm going to go to the market. I have enough time to go and come home.");
		}
		else { 
			toDoList.add(new MyPriority(MyPriority.Task.GoToRestaurant));
			toDoList.add(new MyPriority(MyPriority.Task.GoToMarket));
			
			log.add(new LoggedEvent("I have enough money to go to the restaurant, and go to the market when I have time."));
			
			print("I have enough money to go to the restaurant, and go to the market when I have time.");
		}
	}
	
	private void goToRestaurant(MyPriority p) {
		toDoList.remove(p);
		
		//Location location = new Location("Restaurant", Location.LocationType.Restaurant, new Position(50,50));
		
		// GUI goes to restaurant, lets person agent know that no longer going to be a resident role
		//person.msgAddEvent(new Event("Go to restaurant", location, 2, EventType.CustomerEvent));
		
		// GUI goes to market 
		homeGui.DoGoToFrontDoor();
		
		try {
			atFrontDoor.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			waitForReturn.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void goToMarket(MyPriority p) {
		toDoList.remove(p);
		
//		Location location = new Location("Market", Location.LocationType.Market, new Position(50,50));
//		
//		Event event = new Event("Go to market", location, 2, EventType.MarketEvent);
		
		// Lets person agent know that no longer going to be a resident role
		//person.msgAddEvent(new Event("Go to market", location, 2, EventType.MarketEvent));
		
		// GUI goes to market 
		homeGui.DoGoToFrontDoor();
		
		try {
			atFrontDoor.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			waitForReturn.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void restockFridge(MyPriority p) {
		toDoList.remove(p);

		// GUI goes to the fridge
		homeGui.DoGoToFridge();

		// Semaphore to see if the GUI gets to the fridge
		try {
			atFridge.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void cookFood(MyPriority p) {
		toDoList.remove(p);
		
		homeGui.DoGoToFridge();
		
		try {
			atFridge.acquire();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		int max = -1;
		String maxChoice = null;
		int index = -1;

		for (Food f : myFridge) { // Searches for the food item with the most inventory
			if (f.amount > max) {
				max = f.amount;
				maxChoice = f.choice;
			}
		}

		for (Food f : myFridge) { // Searches for and decreases the amount of food for the one with the most inventory
			if (f.choice == maxChoice) {
				--f.amount;
				++index;
				log.add(new LoggedEvent("I'm going to cook " + f.choice + ". My inventory of it is now " + f.amount + "."));
				print("I'm going to cook " + f.choice + ". My inventory of it is now " + f.amount + ".");
				break;
			}
		}
		
		// If there's no more of an item after the resident has removed it, then 
		if (myFridge.get(index).amount == 0) {
			myFridge.remove(index);
			log.add(new LoggedEvent("My fridge has no more " + maxChoice + "."));
			print("My fridge has no more " + maxChoice + ".");
		}
		
		homeGui.setState(HomeOwnerGui.HomeCookingState.GettingIngredients, maxChoice);

		// GUI animation to go to the stove and start cooking
		homeGui.DoGoToStove(); 
		
		atStove.drainPermits();

		// Semaphore to determine if the GUI has gotten to the stove location
		try {
			atStove.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		homeGui.state = HomeOwnerGui.HomeCookingState.Cooking;

		print("Cooking food..");
		
        // Timer to cook the food
        cookingTimer.schedule(new TimerTask() 
        {
            public void run() 
            {
            	msgFoodDone();
            }
        }, 5000);
        
        homeGui.DoGoToHome();
        
        
	}

	private void eatFood(MyPriority p) {
		toDoList.remove(p);

		// GUI animation to go to the stove and start cooking
		homeGui.DoGoToStove(); 

		// Semaphore to determine if the GUI has gotten to the stove location
		try {
			atStove.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		homeGui.state = HomeOwnerGui.HomeCookingState.GettingCookedFood;

		homeGui.DoGoToTable(); // GUI animation to go to the dining table

		// Semaphore to determine if the GUI has gotten to the table location
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		print("Eating my food..");

		// Timer to eat the food
        eatingTimer.schedule(new TimerTask() 
        {
            public void run() 
            {
            	msgDoneEating();
            }
        }, 8000);
        
		// person.hungerLevel = 0;
	}

	private void washDishes(MyPriority p) {
		toDoList.remove(p);

		final MyPriority prior = new MyPriority(MyPriority.Task.Washing);
		toDoList.add(prior);

		homeGui.DoGoToSink(); // GUI animation to go to the sink
		
		atSink.drainPermits();

		// Semaphore to determine if the GUI has arrived at sink location
		try {
			atSink.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		homeGui.setState(HomeOwnerGui.HomeCookingState.Nothing, null);

		// Timer to wash dishes
        washingDishesTimer.schedule(new TimerTask() 
        {
            public void run() 
            {
            	msgDoneWashing(prior);
            	homeGui.DoGoToHome();
            }
        }, 2000);
	}

	private void maintainHome(MyPriority p) {
		toDoList.remove(p);
		
		homeGui.DoGoToBed();
		try {
			atBed.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		homeGui.DoGoToFridge();
		try {
			atFridge.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		homeGui.DoGoToSink();
		try {
			atSink.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		homeGui.DoGoToStove();
		try {
			atStove.acquire(); 
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		homeGui.DoGoToTable();
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		print("Done maintaining home!");
	}
	
	/*private void callHousekeeper(MyPriority p) {
		toDoList.remove(p);
		housekeeper.msgPleaseComeMaintain(this, houseNumber);
	}
	
	private void letHousekeeperIn(MyPriority p) {
		toDoList.remove(p);
		
		homeGui.DoGoToFrontDoor();
		
		try {
			atFrontDoor.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		housekeeper.msgPleaseComeIn(this, houseNumber);
	}

	private void payHousekeeper(MyPriority p) {
		toDoList.remove(p);
		
		homeGui.DoGoToFrontDoor();
		
		try {
			atFrontDoor.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (myMoney >= (debt+maintenanceCost)) {
			housekeeper.msgHereIsThePayment(this, debt+maintenanceCost);
			myMoney -= debt+maintenanceCost;

		}
		else {
			housekeeper.msgHereIsThePayment(this, myMoney);
			myMoney = 0;
		}
	}*/
}
