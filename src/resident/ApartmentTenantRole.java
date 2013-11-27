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
import person.Market;
import person.Restaurant;
import person.SimEvent;
import person.Location.LocationType;
import person.interfaces.Person;
import resident.gui.ApartmentTenantGui;
import resident.interfaces.ApartmentLandlord;
import resident.interfaces.ApartmentTenant;
import resident.test.mock.EventLog;
import resident.test.mock.LoggedEvent;
import agent.Role;

public class ApartmentTenantRole extends Role implements ApartmentTenant {
	/**
	 * Data for Apartment Tenant
	 *
	 */
	// For the purposes of JUnit testing
	public EventLog log = new EventLog();
	
	// Constructor
	public ApartmentTenantRole(String n, int an, Person p) {
		super(p);
		name = n;
		apartmentNumber = an;
		this.person = p;
		state = MyState.Awake;
	}
	
	// States for the Apartment Tenant
	public enum MyState {Sleeping, Awake};
	private MyState state;
	
	public String getName() {
		return name;
	}
	
	public int getAptNum() {
		return apartmentNumber;
	}

	public static class MyPriority {
		public enum Task {NeedToEat, Cooking, Eating, WashDishes, Washing, GoToMarket, RestockFridge, PayRent, GoToRestaurant, NoFood}
		public Task task;
		private double timeDuration;
		private int levelOfImportance;
		private Map<Task, Integer> taskTime = new HashMap<Task, Integer>();

		public MyPriority(Task t) {
			task = t;
			
			// All basic tasks of a resident
			taskTime.put(Task.NeedToEat, 0);
			taskTime.put(Task.Cooking, 30);
			taskTime.put(Task.Eating, 30);
			taskTime.put(Task.WashDishes, 0);
			taskTime.put(Task.Washing, 10);
			taskTime.put(Task.GoToMarket, 20);
			taskTime.put(Task.RestockFridge, 5);
			taskTime.put(Task.GoToRestaurant, 40);
			taskTime.put(Task.NoFood, 0);
			
			// Maintenance person is an apartment tenant, so pays rent. This adds the new task of paying rent
			taskTime.put(Task.PayRent, 10);
			
			timeDuration = taskTime.get(t);
		}
	}

	public List<MyPriority> toDoList = Collections.synchronizedList(new ArrayList<MyPriority>());
	public List<Food> myFridge = Collections.synchronizedList(new ArrayList<Food>());
	private Timer cookingTimer = new Timer(); // Times the food cooking
	private Timer eatingTimer = new Timer();
	private Timer washingDishesTimer = new Timer();
	private Timer sleepingTimer = new Timer();
	private String name;
	private double myMoney;
	
	public void setMoney(double m) {
		myMoney = m;
	}
	
	public double getMoney() {
		return myMoney;
	}
	
	private double debt;
	private int apartmentNumber;
	private static int hungerThreshold = 3;
	private static double minRestaurantMoney = 70; // Minimum amount in the restaurant
	private static double rentCost = 100; // Static for now.
	private ApartmentLandlord landlord;
	public ApartmentTenantGui aptGui;
	
	public void setGui(ApartmentTenantGui g) {
		aptGui = g;
	}
	
	// All the gui semaphores
	private Semaphore atFridge = new Semaphore(0, true);
	private Semaphore atFrontDoor = new Semaphore(0, true);
	private Semaphore waitForReturn = new Semaphore(0, true);
	private Semaphore atStove = new Semaphore(0, true);
	private Semaphore atTable = new Semaphore(0, true);
	private Semaphore atSink = new Semaphore(0, true);
	
	public void setLandlord(ApartmentLandlord l) {
		landlord = l;
	}
	
	/**
	 * Messages for Apartment Tenant
	 * 
	 */
	public void updateVitals(int hunger, int timer) {
		if (hunger >= hungerThreshold) {
			// Add eating to the list of priorities that the resident has
			toDoList.add(new MyPriority(MyPriority.Task.NeedToEat));
			
			// Log that the message has been received
			log.add(new LoggedEvent("I'm hungry."));
			
			print("I'm hungry.");
			
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
	
	public void msgPayRent() {
		log.add(new LoggedEvent("It's been a day. Time to pay rent."));
		print("It's been a day. Time to pay rent.");
		
		toDoList.add(new MyPriority(MyPriority.Task.PayRent));
		stateChanged();
	}
	
	public void msgReceivedRent(double amount) {
		DecimalFormat df = new DecimalFormat("###.##");
		
		debt += amount; 
		
		log.add(new LoggedEvent("I now have debt of $" + df.format(debt) + "."));
		print("I now have debt of $" + df.format(debt) + ".");
		stateChanged();
	}
	
	// GUI semaphore release messages
	// For when the apt tenant is at the fridge
	public void msgAtFridge() {
		atFridge.release();
	}
	
	// For when apt tenant is at the door
	public void msgAtDoor() {
		atFrontDoor.release();
	}
	
	// For when the apt tenant is at the stove
	public void msgAtStove() {
		atStove.release();
	}
	
	// For when the apt tenant has reached the dining table
	public void msgAtTable() {
		atTable.release();
	}
	
	// For when the home owner has reached the sink
	public void msgAtSink() {
		atSink.release();
	}

	/**
	 * Scheduler for Apartment Tenant
	 * 
	 */
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if (person.getTime() >= 22 && state == MyState.Awake) {
			sleep();
			return true;
		}
		if (!toDoList.isEmpty() && state == MyState.Awake) {
			for (MyPriority p : toDoList) {
				if (p.task == MyPriority.Task.PayRent) {
					payLandlord(p);
					return true;
				}
			}
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
	 * Actions for Apartment Tenant
	 */

	private void payLandlord(MyPriority p) {
		toDoList.remove(p);
		
		// If the amount of money the maintenance person has is more than rent cost, pay rent cost.
		if (myMoney >= rentCost) {
			log.add(new LoggedEvent("Paying the landlord $" + rentCost + "."));
			print("Paying the landlord $" + (debt + rentCost) + ".");
			landlord.msgHereIsTheRent(this, debt + rentCost);
			myMoney -= rentCost;
		}
		// Otherwise, pay as much as you can 
		else {
			log.add(new LoggedEvent("Paying the landlord $" + myMoney + ", because I don't have enough."));
			print("Paying the landlord $" + myMoney + ", because I don't have enough.");
			landlord.msgHereIsTheRent(this, myMoney);
			myMoney = 0;
		}
	}
	
	private void sleep() {
		// Set home owner's state to sleeping
		state = MyState.Sleeping;
		
		// Gui goes to bed and timer begins to start sleeping
		print("Going to bed. It's sleepytime!");
		
		DoGoToBed();
		
		sleepingTimer.schedule(new TimerTask() 
        {
            public void run() 
            {
            	updateVitals(3, 7);
            	state = MyState.Awake;
            }
        }, 540000);
	}
		
	private void checkFridge(MyPriority p) {
		toDoList.remove(p);
		
		DoGoToFridge();

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

		if (person.msgCheckWallet() < minRestaurantMoney) { 
			toDoList.add(new MyPriority(MyPriority.Task.GoToMarket)); 
			toDoList.add(new MyPriority(MyPriority.Task.Cooking));
			
			log.add(new LoggedEvent("I'm going to go to the market. I have enough money to go and come home."));
			
			print("I'm going to go to the market. I have enough money to go and come home.");
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
		
		DoGoToFrontDoor();
		
		if (person.getMap() != null) {
			Restaurant location = (Restaurant)person.getMap().chooseByType(LocationType.Restaurant);

			// GUI goes to restaurant, lets person agent know that no longer going to be a resident role
			person.msgAddEvent(new SimEvent("Go to restaurant", location, 2, SimEvent.EventType.CustomerEvent));
		}
		
		person.msgFinishedEvent(this);
	}
	
	private void goToMarket(MyPriority p) {
		toDoList.remove(p);
		
		DoGoToFrontDoor();
		
		if (person.getMap() != null) {
			Market location = (Market)person.getMap().chooseByType(LocationType.Market);
			
			// Lets person agent know that no longer going to be a resident role
			person.msgAddEvent(new SimEvent(location, 2, person.getTime(), SimEvent.EventType.CustomerEvent));
		}
		
		person.msgFinishedEvent(this);
	}

	private void restockFridge(MyPriority p) {
		toDoList.remove(p);

		DoGoToFridge();
	}

	private void cookFood(MyPriority p) {
		toDoList.remove(p);
		
		DoGoToFridge();

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
		
		DoCookFood(maxChoice);
	}
	

	private void eatFood(MyPriority p) {
		toDoList.remove(p);

		DoGetCookedFood();
        
		// person.hungerLevel = 0;
	}

	private void washDishes(MyPriority p) {
		toDoList.remove(p);

		final MyPriority prior = new MyPriority(MyPriority.Task.Washing);
		final HomeOwnerRole temp = new HomeOwnerRole(this.person, this.name, this.apartmentNumber);
		toDoList.add(prior);

		DoWashDishes();

		// Timer to wash dishes
        washingDishesTimer.schedule(new TimerTask() 
        {
            public void run() 
            {
            	msgDoneWashing(prior);
            	aptGui.DoGoToHome();
            	person.msgFinishedEvent(temp);
            }
        }, 2000);
	}
	
	
	/**
	 * GUI ACTIONS
	 */
	private void DoGoToBed() {
		if (aptGui != null) {
			aptGui.DoGoToBed();
		}
	}
	
	private void DoGoToFridge() {
		if (aptGui != null) {
			// GUI goes to the fridge
			aptGui.DoGoToFridge();
	
			atFridge.drainPermits();
			
			// Semaphore to see if the GUI gets to the fridge
			try {
				atFridge.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void DoGoToFrontDoor() {
		if (aptGui != null) {
			// GUI goes to market 
			aptGui.DoGoToFrontDoor();
			
			atFrontDoor.drainPermits();
			
			try {
				atFrontDoor.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			aptGui.isPresent = false;
		}
	}
	
	private void DoCookFood(String s) {
		if (aptGui != null) {
			aptGui.setState(ApartmentTenantGui.AptCookingState.GettingIngredients, s);

			// GUI animation to go to the stove and start cooking
			aptGui.DoGoToStove(); 
			
			atStove.drainPermits();

			// Semaphore to determine if the GUI has gotten to the stove location
			try {
				atStove.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			aptGui.state = ApartmentTenantGui.AptCookingState.Cooking;

			print("Cooking food..");
			
	        // Timer to cook the food
	        cookingTimer.schedule(new TimerTask() 
	        {
	            public void run() 
	            {
	            	msgFoodDone();
	            }
	        }, 5000);
	        
	        aptGui.DoGoToHome();
		}
	}
	
	private void DoGetCookedFood() {
		if (aptGui != null) {
			// GUI animation to go to the stove and start cooking
			aptGui.DoGoToStove(); 

			atStove.drainPermits();
			
			// Semaphore to determine if the GUI has gotten to the stove location
			try {
				atStove.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			aptGui.state = ApartmentTenantGui.AptCookingState.GettingCookedFood;

			aptGui.DoGoToTable(); // GUI animation to go to the dining table

			atTable.drainPermits();
			
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
		}
	}
	
	private void DoWashDishes() {
		if (aptGui != null) {
			aptGui.DoGoToSink(); // GUI animation to go to the sink
			
			atSink.drainPermits();

			// Semaphore to determine if the GUI has arrived at sink location
			try {
				atSink.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			aptGui.setState(ApartmentTenantGui.AptCookingState.Nothing, null);
		}
	}
}
