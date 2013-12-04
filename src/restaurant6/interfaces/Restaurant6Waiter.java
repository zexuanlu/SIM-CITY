package restaurant6.interfaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import restaurant6.Restaurant6Check;
import restaurant6.Restaurant6Order;
import restaurant6.Restaurant6Table;
import restaurant6.gui.Restaurant6WaiterGui;

public interface Restaurant6Waiter {
    
	/**
	 * 	Waiter receives check from cashier
	 */
	public abstract void msgHereIsTheCheck(Restaurant6Check c);

	/**
	 * Returns name of waiter
	 * @return
	 */
	public abstract String getName();

	/**
	 * Indicates ready to order by the customer
	 * @param customerAgent
	 */
	public abstract void readyToOrder(Restaurant6Customer customerAgent);

	/**
	 * Sent by the customer when the customer is leaving the restaurant
	 * @param customerAgent
	 */
	public abstract void doneEatingAndLeaving(Restaurant6Customer customerAgent);

	/**
	 * Sent by the customer when the customer can't afford anything and will leave the restaurant
	 * @param customerAgent
	 */
	public abstract void cannotAffordAnything(Restaurant6Customer customerAgent);

	/**
	 * Sent by the customer when ordering food
	 * @param myChoice
	 * @param customerAgent
	 */
	public abstract void hereIsMyOrder(String myChoice,
			Restaurant6Customer customerAgent);
	
	/**
	 * Sent by host to tell waiter to seat customer
	 * @param customer
	 * @param tableNum
	 */
	public abstract void seatAtTable(Restaurant6Customer customer, int tableNum);

	/**
	 * Lets the waiter know that the customer is at the front
	 */
	public abstract void msgCustomerAtFront();
	
	/**
	 * When the cook is out of food
	 * @param o
	 */
	public abstract void outOfFood(Restaurant6Order o);

	/**
	 * When the cook is done cooking the food, he calls the waiter
	 * @param o
	 */
	public abstract void orderIsReady(Restaurant6Order o);

	/**
	 * Host tells waiter whether or not it's okay to go on break
	 * @param allowedToGoOnBreak
	 */
	public abstract void goOnBreak(boolean allowedToGoOnBreak);
}
