package restaurant5.gui;

import restaurant5.Restaurant5CustomerAgent;

public class Restaurant5Table {
	public Restaurant5CustomerAgent occupiedBy;
	public int tableNumber;

	public Restaurant5Table(int tableNumber) {
		this.tableNumber = tableNumber;
	}
	
	public void setOccupant(Restaurant5CustomerAgent cust) {
		occupiedBy = cust;
	}

	public void setUnoccupied() {
		occupiedBy = null;
	}

	public Restaurant5CustomerAgent getOccupant() {
		return occupiedBy;
	}

	public boolean isOccupied() {
		return occupiedBy != null;
	}

	public String toString() {
		return "table " + tableNumber;
	}
}
