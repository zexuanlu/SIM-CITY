package resident;

import java.text.DecimalFormat; 
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import person.Event;
import person.Location;
import person.Event.EventType;
import person.Position;
import person.interfaces.Person;
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
		super();
		name = n;
		apartmentNumber = an;
		person = p;
	}
	
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

		MyPriority(Task t) {
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

	private class MyFood {
		private String foodItem;
		private int foodAmount;

		MyFood(String f, int a) {
			foodItem = f;
			foodAmount = a;
		}
	}

	public List<MyPriority> toDoList = Collections.synchronizedList(new ArrayList<MyPriority>());
	private List<MyFood> myFridge = Collections.synchronizedList(new ArrayList<MyFood>());
	private Timer cookingTimer; // Times the food cooking
	private Timer eatingTimer;
	private Timer washingDishesTimer;
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
	private double myTime; // Keeps track of how much time the resident has
	private static int hungerThreshold = 3;
	private static double minRestaurantMoney = 70; // Minimum amount in the restaurant
	private static double rentCost = 100; // Static for now.
	private ApartmentLandlord landlord;
	private Person person;
	
	public void setLandlord(ApartmentLandlord l) {
		landlord = l;
	}
	
	/**
	 * Messages for Apartment Tenant
	 * 
	 */
	public void msgUpdateVitals(int hunger, int time) {
		if (hunger >= hungerThreshold) {
			myTime = time;
			
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

	public void msgDoneGoingToMarket(List<MyFood> groceries) {		
		// If the customer has just finished going to the market, restock the fridge and then cook
		log.add(new LoggedEvent("I just finished going to the market. Time to put all my groceries in the fridge."));
		
		print("I just finished going to the market. Time to put all my groceries in the fridge.");
		
		// Add restocking fridge to the to do list
		toDoList.add(new MyPriority(MyPriority.Task.RestockFridge));		

		for (MyFood f : groceries) {
			myFridge.add(new MyFood(f.foodItem, f.foodAmount));
		}

		stateChanged();
	}

	public void msgDoneEatingOut() {
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

	/**
	 * Scheduler for MaintenancePerson
	 * 
	 */
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
//		if (!person.toDo.peek() && myTime >= 20 && hunger <= 2) {
//			sleep();
//			return true;
//		}
		if (!toDoList.isEmpty()) {
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
//	private void activatePerson() {
//		person.msgFinishedEvent(this);
//		log.add(new LoggedEvent("I have nothing else to do!"));
//		print("I have nothing else to do!");
//	}
	
	private void sleep() {
		// Gui goes to bed and timer begins to start sleeping
		// Message update vitals and cook at home
	}
	
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

	private void checkFridge(MyPriority p) {
		toDoList.remove(p);

//		DoGoToFridge(); // GUI goes to the fridge

		// Semaphore to see if the GUI gets to the fridge

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
			
			log.add(new LoggedEvent("I don't have enough time to cook. I'm going to go to the restaurant instead, and go to the market when I have time."));
			
			print("I don't have enough time to cook. I'm going to go to the restaurant instead, and go to the market when I have time.");
		}
	}
	
	private void goToRestaurant(MyPriority p) {
		toDoList.remove(p);
		
		Location location = new Location("Restaurant", Location.LocationType.Restaurant, new Position(50,50));
		
		// GUI goes to restaurant, lets person agent know that no longer going to be a resident role
		person.msgAddEvent(new Event("Go to restaurant", location, 2, EventType.CustomerEvent));
	}
	
	private void goToMarket(MyPriority p) {
		toDoList.remove(p);
		
		Location location = new Location("Market", Location.LocationType.Market, new Position(50,50));
		
		// GUI goes to market, lets person agent know that no longer going to be a resident role
		person.msgAddEvent(new Event("Go to market", location, 2, EventType.MarketEvent));
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
				print("I'm going to cook " + f.foodItem + ". My inventory of it is now " + f.foodAmount + ".");
				break;
			}
		}
		
		// If there's no more of an item after the resident has removed it, then 
		if (myFridge.get(index).foodAmount == 0) {
			myFridge.remove(index);
			log.add(new LoggedEvent("My fridge has no more " + maxChoice + "."));
			print("My fridge has no more " + maxChoice + ".");
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
		
		// person.hungerLevel = 0;
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
}
