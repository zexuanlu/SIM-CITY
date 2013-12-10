package restaurant1.interfaces;



public interface Restaurant1Waiter {
	public String getName();
	
	public abstract void msgIWantFood(Restaurant1Customer cust, int table, int loc);

	public abstract void msgreadytoorder(Restaurant1Customer customer);
	
	public abstract void msgAnimationDoneAtTable(Restaurant1Customer customer);
	
	public abstract void msgorderisready(Restaurant1Customer customer, String choice, int table);
	
	public abstract void msgorderiscooked(int table);
	
	public abstract void msgatCook();
	
	public abstract void msgordertotable(Restaurant1Customer customer);
	
	public abstract void msgLeavingTable(Restaurant1Customer c) ;

	public abstract void msgAtTable() ;
	
	public abstract void msgIsback();

	public abstract void msgOutOfFood(int table);
	
	public abstract void msgHereistheCheck(Restaurant1Customer c, double p);

}
