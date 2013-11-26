package restaurant.test.mock;


import java.util.Map;

import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockCustomer extends Mock implements Customer {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	public EventLog log = new EventLog();
	public MockCustomer(String name) {
		super(name);

	}

	@Override
	public void msgHereisYourBill(int bill) {
		log.add(new LoggedEvent("Received HereIsYourTotal from cashier. Total = "+ bill));

		if(this.name.toLowerCase().contains("thief")){

		}else if (this.name.toLowerCase().contains("rich")){
			//test the non-normative scenario where the customer overpays if their name contains the string "rich"
			cashier.msgPayment(this, bill);

		}else{
			//test the normative scenario
			cashier.msgPayment(this, bill);
		}
	}

	@Override
	public void msgHereisYourChange(double change) {
		if(change > 0){
		log.add(new LoggedEvent("Received HereIsYourChange from cashier. Change = "+ change));
		}
		else {
			log.add(new LoggedEvent("I will pay the rest next time"));
		}
	}

	@Override
	public void gotHungry() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAskforStatus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgSitAtTable(int a, Map<String, Double> s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAnimationFinishedGoToSeat() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgReorder(String choice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgwhatyouwant() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgordercooked() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAnimationFinishedLeaveRestaurant() {
		// TODO Auto-generated method stub
		
	}

	

}
