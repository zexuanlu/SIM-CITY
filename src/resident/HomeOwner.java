package resident;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import agent.Role;

public class HomeOwner extends Role {
	/**
	 * Data for Homeowner
	 * @author jenniezhou
	 *
	 */
	private static class MyPriority {
		public enum Task {NeedToEat, Cooking, Eating, WashDishes, Washing, PayHousekeeper, GoToMarket, RestockFridge, RestockFridgeThenCook, GoToRestaurant, NoFood}
		private Task task;
		double timeDuration;
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
		CheckState state;

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
	private int houseNumber;
	private String name;
	private double myMoney;
	private double debt;
	private double maintenanceCost; // Static for now.
	private double myTime; // Keeps track of how much time the resident has
	private double minCookingTime; // Time it takes to cook the fastest food
	private double marketTime; // Time it takes to go to the market and come back
	//MaintenancePerson housekeeper;
	
	/**
	 * Messages for Homeowner
	 * @author jenniezhou
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

	public void msgDoneMaintaining(MyCheck c) {
		checks.add(new MyCheck("Maintenance", c.amount));
		stateChanged();
	}

	public void msgReceivedPayment(double amount) {
		for (MyCheck check : checks) {
			if (check.amount == amount) {
				checks.remove(check);
				stateChanged(); 
			}
		}
	}

	/**
	 * Scheduler for Homeowner
	 * @author jenniezhou
	 *
	 */
	protected boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		/*if (globalTimer%70 == 0) { // Assumng house needs to be maintained every week, and global timer 10 is equivalent to one day
			callHousekeeper();
			return true;
		}*/
		if (!checks.isEmpty()) {
			for (MyCheck c : checks) {
				if (c.state == MyCheck.CheckState.New) {
					payHousekeeper(c);
					return true;
				}
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
	 * Actions for Homeowner
	 */
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
//		// Semaphore to determine if the GUI has arrived at sink locatin
//
//		washingDishesTimer.start{msgDoneWashing(p)};
	}

	private void callHousekeeper() {
		housekeeper.msgPleaseComeMaintain(this, houseNumber);
	}

	private void payHousekeeper(MyCheck c) {
		if (myMoney >= c.amount) {
			housekeeper.msgHereIsThePayment(this, c.amount);
			myMoney -= c.amount;

		}
		else {
			housekeeper.msgHereIsThePayment(this, myMoney);
			c.amount = myMoney;
			myMoney = 0;
		}
		c.state = MyCheck.CheckState.Paid;
	}
}
