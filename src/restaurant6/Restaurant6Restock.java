package restaurant6;

public class Restaurant6Restock {
	private int amount;
	private String choice;
	
	public enum RestockState {Placed, Reordering, Fulfilled}
	private RestockState state;
	
	public Restaurant6Restock(String c, int n) {
		choice = c;
		amount = n;
		state = RestockState.Placed;
	}
	
	public Restaurant6Restock(String c, int n, RestockState st) {
		choice = c;
		amount = n;
		state = st;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public String getOrderChoice() {
		return choice;
	}
	
	public RestockState getState() {
		return state;
	}
	
	public void setState(RestockState st) {
		state = st;
	}
	
	public void setAmount(int a) {
		amount = a;
	}
}
