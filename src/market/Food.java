package market;

public class Food {
	public String choice;
	public int amount;
	
	public Food(String name, int amount){
		this.choice = name;
		this.amount = amount;
	}
	
	public boolean equals(Food food){
		if(this.choice.equals(food.choice) && this.amount == food.amount)
			return true;
		else
			return false;
	}
}
