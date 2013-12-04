package restaurant6;

import restaurant6.interfaces.Restaurant6Waiter;

public class Restaurant6Order
{
	private Restaurant6Waiter waiter;
	private int tableNum;
	private String choice;
	public enum OrderState {None, Pending, Cooking, Cooked, Served};
	private OrderState orderStatus;
	
	// Constructor for order contains the food choice, table, waiter, and status
	public Restaurant6Order(String food, int tNum, Restaurant6Waiter w) {
		waiter = w;
		tableNum = tNum;
		choice = food;
		orderStatus = OrderState.Pending;
	}
	
	public OrderState getOrderStatus() {
		return orderStatus;
	}
	
	public void setOrderStatus(OrderState state) {
		orderStatus = state;
	}
	
	public int getTableNum() {
		return tableNum;
	}
	
	public Restaurant6Waiter getWaiter() {
		return waiter;
	}

	public String getOrder() {
		return choice;
	}
}