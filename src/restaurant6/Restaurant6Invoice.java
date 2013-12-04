package restaurant6;

public class Restaurant6Invoice {
	private int marketNumber;
	private double totalAmount;
	private double paidAmount;
	
	public enum InvoiceState {Pending, Paid, Incomplete} 
	private InvoiceState state;
	
	public Restaurant6Invoice(int num, double amount) {
		totalAmount = amount;
		marketNumber = num;
		state = InvoiceState.Pending;
	}
	
	public int getMarket() {
		return marketNumber;
	}
	
	public double getTotal() {
		return totalAmount;
	}
	
	public void setState(InvoiceState st) {
		state = st;
	}
	
	public InvoiceState getState() {
		return state;
	}
	
	public double getPaidAmount() {
		return paidAmount;
	}
	
	public void setPaidAmount(double num) {
		paidAmount = num;
	}
}
