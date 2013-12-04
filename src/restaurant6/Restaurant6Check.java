package restaurant6;

import restaurant6.interfaces.Restaurant6Customer;
import restaurant6.interfaces.Restaurant6Waiter;

public class Restaurant6Check {
	private String choice;
	private double billAmount;
	private double amountCustomerPaid;
	private double change;
	private Restaurant6Waiter waiter;
	private Restaurant6Customer customer;
	public enum CheckState {toBeComputed, givenToWaiter, customerIsPaying, Paid};
	private CheckState state;
	
	public Restaurant6Check (String c, double t, Restaurant6Waiter w, Restaurant6Customer c2) {
		choice = c;
		billAmount = t;
		amountCustomerPaid = 0;
		waiter = w;
		customer = c2;
		change = 0;
		state = CheckState.toBeComputed;
	}
	
	public String getChoice() {
		return choice;
	}
	
	public double getBillAmount() {
		return billAmount;
	}
	
	public Restaurant6Waiter getWaiter() {
		return waiter;
	}
	
	public Restaurant6Customer getCustomer() {
		return customer;
	}
	
	public CheckState getCheckState() {
		return state;
	}
	
	public void setCheckState(CheckState c) {
		state = c;
	}
	
	public void setCustomerPaymentAmount(double p) {
		amountCustomerPaid = p;
	}
	
	public double getCustomerPaymentAmount() {
		return amountCustomerPaid;
	}
	
	public void setChange(double c) {
		change = c;
	}
	
	public double getChange() {
		return change;
	}
}
