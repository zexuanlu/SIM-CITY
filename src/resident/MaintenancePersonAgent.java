package resident;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import resident.HomeOwnerAgent.MyFood;
import resident.HomeOwnerAgent.MyPriority;
import resident.HomeOwnerAgent.MyPriority.Task;
import resident.interfaces.HomeOwner;
import resident.interfaces.MaintenancePerson;
import resident.test.mock.EventLog;
import resident.test.mock.LoggedEvent;
import agent.Role;

public class MaintenancePersonAgent extends Role implements MaintenancePerson {
	/**
	 * Data for MaintenancePerson
	 *
	 */
	// For the purposes of JUnit testing
	public EventLog log = new EventLog();
	
	// Constructor
	public MaintenancePersonAgent(String n, int an) {
		super();
		name = n;
		apartmentNumber = an;
	}
	
	public String getName() {
		return name;
	}
	
	public int getAptNum() {
		return apartmentNumber;
	}
	
	private static class MyCustomer {
		private HomeOwner customer;
		private int houseNumber;
		public enum MyCustomerState {NeedsMaintenance, InMaintenance, Maintained, NeedsToPay, Paid}
		private MyCustomerState state;
		private double amountOwed;
		private double amountPaid;

		MyCustomer(HomeOwner h, int n) {
			customer = h;
			houseNumber = n;
			state = MyCustomerState.NeedsMaintenance;
		}
	}

	private List<MyCustomer> homesToBeMaintained = Collections.synchronizedList(new ArrayList<MyCustomer>());
	private double maintenanceCost;

	private static class MyPriority {
		public enum Task {NeedToEat, Cooking, Eating, WashDishes, Washing, GoToMarket, RestockFridge, PayRent, GoToRestaurant, NoFood}
		private Task task;
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

	private List<MyPriority> toDoList = Collections.synchronizedList(new ArrayList<MyPriority>());
	private List<MyFood> myFridge = Collections.synchronizedList(new ArrayList<MyFood>());;
	private Timer globalTimer; // Reference to the global timer
	private Timer cookingTimer; // Times the food cooking
	private Timer eatingTimer;
	private Timer washingDishesTimer;
	private String name;
	private double myMoney;
	private double debt;
	private double rentCost; // Static for now.
	private double maintenanceAmount; 
	private int apartmentNumber;
	private double myTime; // Keeps track of how much time the resident has
	private double minCookingTime; // Time it takes to cook the fastest food
	private double marketTime; // Time it takes to go to the market and come back
	private ApartmentLandlord landlord;
	
	/**
	 * Messages for MaintenancePerson
	 * 
	 */
	public void msgGotHungry() {
		// Add eating to the list of priorities that the resident has
		toDoList.add(new MyPriority(MyPriority.Task.NeedToEat));
		
		// Log that the message has been received
		log.add(new LoggedEvent("I'm hungry."));
		
		print("I'm hungry.");
		
		stateChanged();
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
		print("It's been a day. TIme to pay rent.");
		
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

	public void msgPleaseComeMaintain(HomeOwner cust, int houseNumber) {
		log.add(new LoggedEvent("Just received a call from " + cust.getName() + "to go maintain house " + houseNumber));
		print("Just received a call from " + cust.getName() + "to go maintain house " + houseNumber);
		homesToBeMaintained.add(new MyCustomer(cust, houseNumber));
		stateChanged();
	}
	
	public void msgPleaseComeIn(HomeOwner homeOwnerAgent, int houseNumber) {
		for (MyCustomer c : homesToBeMaintained) {
			if (c.customer == homeOwnerAgent) {
				c.state = MyCustomer.MyCustomerState.InMaintenance;
				log.add(new LoggedEvent("Going into customer " + c.customer.getName() + "'s house to maintain."));
				print("Going into customer " + c.customer.getName() + "'s house to maintain.");
				stateChanged();
			}
		}
	}

	public void msgFinishedMaintenance(MyCustomer c) {
		log.add(new LoggedEvent("Finished maintaining " + c.customer.getName() + "'s house."));
		print("Finished maintaining " + c.customer.getName() + "'s house.");
		c.state = MyCustomer.MyCustomerState.Maintained;
		stateChanged();
	}

	public void msgHereIsThePayment(HomeOwner cust, double amount) {
		for (MyCustomer c : homesToBeMaintained) {
			if (c.customer == cust) {
				c.state = MyCustomer.MyCustomerState.Paid;
				c.amountPaid = amount;
				c.amountOwed = maintenanceCost - amount;
				log.add(new LoggedEvent("Just received payment of " + c.amountPaid + " from customer " + c.customer.getName()));
				print("Just received payment of " + c.amountPaid + " from customer " + c.customer.getName());
				stateChanged();
			}
		}
	}

	/**
	 * Scheduler for MaintenancePerson
	 * 
	 */
	protected boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		for (MyCustomer c : homesToBeMaintained) {
			if (c.state == MyCustomer.MyCustomerState.NeedsMaintenance) {
				maintainHouse(c);
				return true;
			}
		}

		for (MyCustomer c : homesToBeMaintained) {
			if (c.state == MyCustomer.MyCustomerState.Maintained) {
				letCustomerKnow(c);
			}
		}

		for (MyCustomer c : homesToBeMaintained) {
			if (c.state == MyCustomer.MyCustomerState.Paid) {
				tellCustomerReceivedPayment(c);
			}
		}

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
	 * Actions for MaintenancePerson
	 */
	private void maintainHouse(MyCustomer c) {
		log.add(new LoggedEvent("Maintaining home.."));
		print("Maintaining home..");
//		DoGoToCustomerHouse(c);
		// Semaphore to indicate when at customer's house
		//c.state = MyCustomer.MyCustomerState.InMaintenance;
//		maintenanceTimer.start{msgFinishedMaintenance(c)};
	}

	private void letCustomerKnow(MyCustomer c) {
		log.add(new LoggedEvent("Finished maintaining " + c.customer.getName() + "'s home!"));
		print("Finished maintaining " + c.customer.getName() + "'s home!");
		c.customer.msgDoneMaintaining(maintenanceAmount);
		c.state = MyCustomer.MyCustomerState.NeedsToPay;
		c.amountOwed = maintenanceCost;
	}

	private void tellCustomerReceivedPayment(MyCustomer c) {
		c.customer.msgReceivedPayment(maintenanceCost - c.amountPaid);
		log.add(new LoggedEvent("I received the customer's payment of " + (maintenanceCost - c.amountPaid) + "."));
		homesToBeMaintained.remove(c);
	}

	private void payLandlord(MyPriority p) {
		toDoList.remove(p);
		
		// If the amount of money the maintenance person has is more than rent cost, pay rent cost.
		if (myMoney >= rentCost) {
			landlord.msgHereIsTheRent(this, rentCost);
			myMoney -= rentCost;
		}
		// Otherwise, pay as much as you can 
		else {
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

		if (myTime > minCookingTime) { 
			toDoList.add(new MyPriority(MyPriority.Task.GoToMarket)); 
			toDoList.add(new MyPriority(MyPriority.Task.Cooking));
			
			log.add(new LoggedEvent("I'm going to go to the market. I have enough time to go and come home."));
			
			print("I'm going to go to the market. I have enough time to go and come home.");
			
//			DoGoToMarket(); // GUI will go to market
		}
		else { 
			toDoList.add(new MyPriority(MyPriority.Task.GoToRestaurant));
			toDoList.add(new MyPriority(MyPriority.Task.GoToMarket));
			
			log.add(new LoggedEvent("I don't have enough time to cook. I'm going to go to the restaurant instead, and go to the market when I have time."));
			
			print("I don't have enough time to cook. I'm going to go to the restaurant instead, and go to the market when I have time.");
//			DoGoToMarketThenRestaurant(); // GUI will go to market then restaurant
		}
	}
	
	private void goToRestaurant(MyPriority p) {
		toDoList.remove(p);
		// GUI goes to restaurant, lets person agent know that no longer going to be a resident role
	}
	
	private void goToMarket(MyPriority p) {
		toDoList.remove(p);
		// GUI goes to market, lets person agent know that no longer going to be a resident role
	}

	/*private void restockFridgeThenCook(MyPriority p) {
		toDoList.remove(p);

//		DoGoToFridge(); // GUI will go to the fridge 

		// Semaphore to determine if GUI arrived at fridge

		toDoList.add(new MyPriority(MyPriority.Task.Cooking));
	}*/

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
