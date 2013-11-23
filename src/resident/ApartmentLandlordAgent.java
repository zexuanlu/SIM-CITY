package resident;

import java.text.DecimalFormat; 
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import resident.interfaces.ApartmentLandlord;
import resident.interfaces.ApartmentTenant;
import resident.test.mock.EventLog;
import resident.test.mock.LoggedEvent;
import agent.Role;

public class ApartmentLandlordAgent extends Role implements ApartmentLandlord {
	/**
	 * Data for Apartment Tenant
	 *
	 */
	// For the purposes of JUnit testing
	public EventLog log = new EventLog();
	
	// Constructor
	public ApartmentLandlordAgent(String n, int an) {
		super();
		name = n;
		apartmentNumber = an;
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

		MyTenant(ApartmentTenant at, int apt) {
			aptRes = at;
			apartmentNumber = apt;
			amountOwed = 0;
			state = TenantState.None;
		}
	}

	public List<MyTenant> tenants = Collections.synchronizedList(new ArrayList<MyTenant>());
	private String name;
	private double myMoney;
	private double debt;
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
				log.add(new LoggedEvent("Received payment of $" + t.amountPaying + "."));
				print("Received payment of $" + t.amountPaying + ".");
				stateChanged();
			}
		}
	}

	/**
	 * Scheduler for MaintenancePerson
	 * 
	 */
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if (!tenants.isEmpty()) {
			for (MyTenant t : tenants) {
				if (t.state == MyTenant.TenantState.PayingRent) {
					collectPayment(t);
					return true;
				}
			}
		}

		return false;
	}
	
	/**
	 * Actions for Apartment Tenant
	 */
	private void collectPayment(MyTenant t) {
		t.state = MyTenant.TenantState.Paid;
		t.amountOwed = rentCost - t.amountPaying;
		t.aptRes.msgReceivedRent(t.amountOwed);
	}
}
