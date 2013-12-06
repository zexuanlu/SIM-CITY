package restaurant2;

public class Table {

	Restaurant2CustomerRole occupiedBy;
	int tableNumber;
	private int x;
	private int y;

	Table(int tableNumber) {
		this.tableNumber = tableNumber;
	}

	void setOccupant(Restaurant2CustomerRole cust) {
		occupiedBy = cust;
	}

	void setUnoccupied() {
		occupiedBy = null;
	}
	
	void setXY(int xValue, int yValue){
		x = xValue;
		y = yValue;
	}
	
	Restaurant2CustomerRole getOccupant() {
		return occupiedBy;
	}

	boolean isOccupied() {
		return occupiedBy != null;
	}

	public String toString() {
		return "table " + tableNumber;
	}

}
