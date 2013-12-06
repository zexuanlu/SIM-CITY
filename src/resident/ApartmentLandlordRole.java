package resident;

import java.text.DecimalFormat; 
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import person.interfaces.Person;
import resident.interfaces.ApartmentLandlord;
import resident.interfaces.ApartmentTenant;
import resident.test.mock.EventLog;
import resident.test.mock.LoggedEvent;
import agent.Role;

public class ApartmentLandlordRole extends Role implements ApartmentLandlord {
	/**
	 * Data for Apartment Tenant
	 *
	 */
	// For the purposes of JUnit testing
	public EventLog log = new EventLog();
	
	// Constructor
	public ApartmentLandlordRole(String n, int an, Person p) {
		super(p);
		roleName = "Apartment Landlord";
		name = n;
		apartmentNumber = an;
		person = p;
	}
	
	public String getName() {
		return name;
	}
	
	public int getAptNum() {
		return apartmentNumber;
	}

	public static class MyTenant {
		public ApartmentTenant aptRes;
		public int apartmentNumber;
		public enum TenantState {None, PayingRent, Paid};
		public TenantState state;
		public double amountOwed;
		public double amountPaying;

		public MyTenant(ApartmentTenant at, int apt) {
			aptRes = at;
			apartmentNumber = apt;
			amountOwed = 0;
			state = TenantState.None;
		}
	}

	public List<MyTenant> tenants = Collections.synchronizedList(new ArrayList<MyTenant>());
	private String name;
	private Person person; 
	private double myMoney;
	private static double rentCost = 100; // Static for now	
	private int apartmentNumber;

	/**
	 * Messages for Apartment Landlord
	 */
	
	public void msgNewTenant(ApartmentTenant apartmentTenantAgent, int atNum) {
		tenants.add(new MyTenant(apartmentTenantAgent, atNum));
		stateChanged();
	}

	public void msgHereIsTheRent(ApartmentTenant apartmentTenantAgent, double amount) {
		for (MyTenant t : tenants) {
			if (t.aptRes == apartmentTenantAgent) {
				t.state = MyTenant.TenantState.PayingRent;
				t.amountPaying = amount;
				// person.msgAddMoney(amount);
				log.add(new LoggedEvent("Received payment of $" + t.amountPaying + "."));
				print("Received payment of $" + t.amountPaying + ".");
				stateChanged();
			}
		}
	}

	/**
	 * Scheduler for ApartmentLandlord
	 * 
	 */
	public boolean pickAndExecuteAnAction() {
//		if (!tenants.isEmpty()) {
//			for (MyTenant t : tenants) {
//				if (t.state == MyTenant.TenantState.PayingRent) {
//					collectPayment(t);
//					return true;
//				}
//			}
//		}

		return false;
	}
	
	/**
	 * Actions for Apartment Tenant
	 */
	private void collectPayment(MyTenant t) {
		t.state = MyTenant.TenantState.Paid;
		myMoney += t.amountPaying;
		// person.msgAddMoney(t.amountPaying);
		t.amountOwed = rentCost - t.amountPaying;
		t.aptRes.msgReceivedRent(t.amountOwed);
		person.msgFinishedEvent(this);
	}
	
	public String getRoleName(){
		return roleName;
	}
}
