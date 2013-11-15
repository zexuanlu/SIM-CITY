package resident;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import agent.Role;

public class MaintenancePerson extends Role {
	/**
	 * Data for MaintenancePerson
	 *
	 */
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
	private double rentCost; // Static for now.
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

	public void msgPleaseComeMaintain(HomeOwner cust, int houseNumber) {
		homesToBeMaintained.add(new MyCustomer(cust, houseNumber));
		stateChanged();
	}

	public void msgFinishedMaintenance(MyCustomer customer) {
		customer.state = MyCustomer.MyCustomerState.Maintained;
		stateChanged();
	}

	public void msgHereIsThePayment(HomeOwner cust, double amount) {
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
		
		return false;
	}
}
