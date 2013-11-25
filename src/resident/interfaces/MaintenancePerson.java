package resident.interfaces;

public interface MaintenancePerson {

	public abstract void msgPleaseComeMaintain(HomeOwner homeOwnerAgent, int houseNumber);
	
	public abstract void msgPleaseComeIn(HomeOwner homeOwnerAgent,
			int houseNumber);

	public abstract void msgHereIsThePayment(HomeOwner homeOwnerAgent, double maintenanceCost);

}
