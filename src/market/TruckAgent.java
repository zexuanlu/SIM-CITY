package market;

import agent.Agent;

import java.util.*;

import marketinterface.Cashier;
import marketinterface.Cook;
import marketinterface.Truck;

public class TruckAgent extends Agent implements Truck{

	List<Food> order;
	Cook cook;
	Cashier cashier;
	public enum state{receivingOrder, sending }
	public state s;
	
	public void setCashier(Cashier cashier){
		this.cashier = cashier;
	}
	
	public void msgPleaseDiliver(Cook c, List<Food> food){
		s = state.receivingOrder;
		cook = c;
		order = food;
		stateChanged();
		}
	
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		
		if(s == state.receivingOrder){
			SendOrder();
			return true;
		}
		
		return false;
	}
	
	void SendOrder(){
		s = state.sending;
		//DoSendOrder();
		cook.msgHereisYourFood(order);
		//DoGoBack();
		cashier.msgTruckBack(this);
		}

}
