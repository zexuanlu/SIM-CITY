package restaurant6;

public class Food {
	private String choice;
	private int cookTime;
	private int amount;
	private int amountMissing;
	
	Food(String f, int ct, int a) {
		choice = f;
		cookTime = ct;
		amount = a;
		amountMissing = 0;
	}
	
	public String getChoice() {
		return choice;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int a) {
		amount = a;
	}
	
	public void setAmountMissing(int a) {
		amountMissing = a;
	}
	
	public int getAmountMissing() {
		return amountMissing;
	}
	
	public int getCookTime() {
		return cookTime;
	}
}
