package restaurant6;

public class Restaurant6Table {
		
		private Restaurant6CustomerRole occupiedBy;
		private int tableNumber;
		private int tableXPos;
		private int tableYPos;

		public int getXPos()
		{
			return tableXPos;
		}
		
		public int getYPos()
		{
			return tableYPos;
		}
		
		public void setXPos(int x)
		{
			tableXPos = x;
		}
		
		public void setYPos(int y)
		{
			tableYPos = y;
		}
		
		public int getTableNum()
		{
			return tableNumber;
		}
		
		public Restaurant6Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}

		public void setOccupant(Restaurant6CustomerRole cust) {
			occupiedBy = cust;
		}

		public void setUnoccupied() {
			occupiedBy = null;
		}

		public Restaurant6CustomerRole getOccupant() {
			return occupiedBy;
		}

		public boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}
}
