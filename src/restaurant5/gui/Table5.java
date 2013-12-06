package restaurant5.gui;

import restaurant5.CustomerAgent5;

public class Table5 {
	public CustomerAgent5 occupiedBy;
	public int tableNumber;

	public Table5(int tableNumber) {
		this.tableNumber = tableNumber;
	}
	
	public void setOccupant(CustomerAgent5 cust) {
		occupiedBy = cust;
	}

	public void setUnoccupied() {
		occupiedBy = null;
	}

	public CustomerAgent5 getOccupant() {
		return occupiedBy;
	}

	public boolean isOccupied() {
		return occupiedBy != null;
	}

	public String toString() {
		return "table " + tableNumber;
	}
}
