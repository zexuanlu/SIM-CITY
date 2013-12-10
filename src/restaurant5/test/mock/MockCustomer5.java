package restaurant5.test.mock;
import restaurant5.gui.Restaurant5FoodGui;
import restaurant5.interfaces.Waiter5; 
import restaurant5.interfaces.Cashier5;
import restaurant5.interfaces.Customer5;
import restaurant5.Restaurant5Cashier; 
import restaurant5.Menu5;
import restaurant5.Restaurant5WaiterAgent;

public class MockCustomer5 extends Mock5 implements Customer5 {
	public EventLog5 log = new EventLog5(); 
	public int myChange; 

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Restaurant5Cashier cashier;

	public MockCustomer5(String name) {
		super(name);
	}
	
	public void msgChange(int Change){
		log.add(new LoggedEvent5("Received Change from Cashier of " + Change));
		myChange = Change; 
	}
	public void msgSendCheck(int Check){}; 
	public void msgGotHungry(){}; 
	public void msgRestaurantFull(){}; 
	public void msgOutofChoice(Menu5 m){}; 
	public void msgAnimationFinishedLeaveRestaurant(){}; 
	public void msgAnimationFinishedGoToSeat(){}; 
	public void msgfollowMe(Waiter5 w, Menu5 m){};
	public void msgwhatdoyouWant(Waiter5 w){}; 
	public void msgHeresYourOrder(Waiter5 w, String choice){};
	public void msgfinishedFood(){};
	
	
/**
	@Override
	public void HereIsYourTotal(double total) {
		log.add(new LoggedEvent("Received HereIsYourTotal from cashier. Total = "+ total));

		if(this.name.toLowerCase().contains("thief")){
			//test the non-normative scenario where the customer has no money if their name contains the string "theif"
			cashier.IAmShort(this, 0);

		}else if (this.name.toLowerCase().contains("rich")){
			//test the non-normative scenario where the customer overpays if their name contains the string "rich"
			cashier.HereIsMyPayment(this, Math.ceil(total));

		}else{
			//test the normative scenario
			cashier.HereIsMyPayment(this, total);
		}
	}

	@Override
	public void HereIsYourChange(double total) {
		log.add(new LoggedEvent("Received HereIsYourChange from cashier. Change = "+ total));
	}

	@Override
	public void YouOweUs(double remaining_cost) {
		log.add(new LoggedEvent("Received YouOweUs from cashier. Debt = "+ remaining_cost));
	}
*/
}
