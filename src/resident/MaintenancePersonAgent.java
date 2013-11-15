package resident;

import interfaces.MaintenancePerson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import agent.Role;

public class MaintenancePersonAgent extends Role implements MaintenancePerson {
	/**
	 * Data for MaintenancePerson
	 *
	 */
	private static class MyCustomer {
		private HomeOwnerAgent customer;
		private int houseNumber;
		public enum MyCustomerState {NeedsMaintenance, InMaintenance, Maintained, NeedsToPay, Paid}
		private MyCustomerState state;
		private double amountOwed;
		private double amountPaid;

		MyCustomer(HomeOwnerAgent h, int n) {
			customer = h;
			houseNumber = n;
			state = MyCustomerState.NeedsMaintenance;
		}
	}

	private List<MyCustomer> homesToBeMaintained = Collections.synchronizedList(new ArrayList<MyCustomer>());
	private double maintenanceCost;

	private static class MyPriority {
		public enum Task {NeedToEat, Cooking, Eating, WashDishes, Washing, PayHousekeeper, GoToMarket, RestockFridge, RestockFridgeThenCook, GoToRestaurant, NoFood}
		private Task task;
		private double timeDuration;
		private int levelOfImportance;
//		Map<Task, Double> taskTime; // Will have times preinitialized 
//		Map<Task, Integer> taskImportance; // Will have importance preinitialized

		MyPriority(Task t) {
			task = t;
//			timeDuration = taskTime.get(t);
//			levelOfImportance = taskImportance.get(t);
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

	private static class MyCheck {
		private String service;
		private double amount;
		public enum CheckState {New, Paid}
		private CheckState state;

		MyCheck(String s, double a) {
			service = s;
			amount = a;
			state = CheckState.New;
		}
	}

	private List<MyPriority> toDoList = Collections.synchronizedList(new ArrayList<MyPriority>());
	private List<MyFood> myFridge = Collections.synchronizedList(new ArrayList<MyFood>());;
	private List<MyCheck> checks = Collections.synchronizedList(new ArrayList<MyCheck>());
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
//	ApartmentLandlord landlord;
	
	/**
	 * Messages for MaintenancePerson
	 * 
	 */
	public void msgGotHungry() {
		// Add eating to the list of priorities that the resident has
		toDoList.add(new MyPriority(MyPriority.Task.NeedToEat));
		stateChanged();
	}

	public void msgFoodDone() {
		// Add getting cooked food to the list of priorities 
		toDoList.add(new MyPriority(MyPriority.Task.Eating));
		stateChanged();
	}

	public void msgDoneEating() {
		// Add washing dishes to the list of priorities
		toDoList.add(new MyPriority(MyPriority.Task.WashDishes));
		stateChanged();
	}

	public void msgDoneWashing(MyPriority p) {
		// Removes washing from the list of priorities
		toDoList.remove(p);
		stateChanged();
	}

	public void msgDoneGoingToMarket(List<MyFood> groceries) {
		for (MyPriority p : toDoList) {
			if (p.task == MyPriority.Task.GoToMarket) {
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
				toDoList.remove(p);
				toDoList.add(new MyPriority(MyPriority.Task.RestockFridge));
			}
		}

		for (MyFood f : groceries) {
			myFridge.add(new MyFood(f.foodItem, f.foodAmount));
		}

		stateChanged();
	}

	public void msgYouHaveDebt(double amount) {
		debt += amount;
		stateChanged();
	}

	public void msgReceivedRent(double amount) {
		for (MyCheck check : checks) {
			if (check.amount == amount) {
				checks.remove(check);
				stateChanged(); 
			}
		}
	}

	public void msgPleaseComeMaintain(HomeOwnerAgent cust, int houseNumber) {
		homesToBeMaintained.add(new MyCustomer(cust, houseNumber));
		stateChanged();
	}

	public void msgFinishedMaintenance(MyCustomer customer) {
		customer.state = MyCustomer.MyCustomerState.Maintained;
		stateChanged();
	}

	public void msgHereIsThePayment(HomeOwnerAgent cust, double amount) {
		for (MyCustomer c : homesToBeMaintained) {
			if (c.customer == cust) {
				c.state = MyCustomer.MyCustomerState.Paid;
				c.amountPaid = amount;
				c.amountOwed = maintenanceCost - amount;
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
	 * Actions for MaintenancePerson
	 */
	private void maintainHouse(MyCustomer c) {
//		DoGoToCustomerHouse(c);
		// Semaphore to indicate when at customer's house
		c.state = MyCustomer.MyCustomerState.InMaintenance;
//		maintenanceTimer.start{msgFinishedMaintenance(c)};
	}

	private void letCustomerKnow(MyCustomer c) {
		c.customer.msgDoneMaintaining(maintenanceAmount);
		c.state = MyCustomer.MyCustomerState.NeedsToPay;
		c.amountOwed = maintenanceCost;
	}

	private void tellCustomerReceivedPayment(MyCustomer c) {
		c.customer.msgReceivedPayment(c.amountPaid);
		if (c.amountOwed > 0) {
			c.customer.msgYouHaveDebt(c.amountOwed);
		}
		homesToBeMaintained.remove(c);
	}

	private void payLandlord() {
		if (myMoney >= rentCost) {
//			landlord.msgHereIsTheRent(this, rentCost);
			myMoney -= rentCost;
			checks.add(new MyCheck("Rent", rentCost));
		}
		else {
//			landlord.msgHereIsTheRent(this, myMoney);
			checks.add(new MyCheck("Rent", myMoney));
			myMoney = 0;
		}
	}

	private void checkFridge(MyPriority p) {
		toDoList.remove(p);

//		DoGoToFridge(); // GUI goes to the fridge

		// Semaphore to see if the GUI gets to the fridge

		if (!myFridge.isEmpty()) { // Checks to see if the list is empty
			// Adds going to the market or restaurant to the list
			toDoList.add(new MyPriority(MyPriority.Task.NoFood));
			stateChanged(); // Legal?
		}
		
		else { // Cook the food
			toDoList.add(new MyPriority(MyPriority.Task.Cooking));
			stateChanged(); // Legal?
		}	
	}

	private void decideMarketOrGoOut(MyPriority p) {
		toDoList.remove(p);

		if (myTime > (marketTime + minCookingTime)) { 
//			DoGoToMarket(); // GUI will go to market
			toDoList.add(new MyPriority(MyPriority.Task.GoToMarket)); 
		}
		
		else { 
//			DoGoToMarketThenRestaurant(); // GUI will go to market then restaurant
			toDoList.add(new MyPriority(MyPriority.Task.GoToRestaurant));
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

		for (MyFood f : myFridge) { // Searches for the food item with the most inventory
			if (f.foodAmount > max) {
				max = f.foodAmount;
				maxChoice = f.foodItem;
			}
		}

		for (MyFood f : myFridge) { // Searches for and decreases the amount of food for the one with the most inventory
			if (f.foodItem == maxChoice) {
				--f.foodAmount;
				if (f.foodAmount == 0) { // If the food item's amount is 0, remove it from the fridge list
					myFridge.remove(f);
				}
			}
		}
//
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
