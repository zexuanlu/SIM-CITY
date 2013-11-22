package resident.interfaces;

public interface HomeOwner {

	public abstract void msgDoneMaintaining(double maintenanceAmount);

	public abstract void msgReceivedPayment(double amt);

	//public abstract void msgYouHaveDebt(double amountOwed);

	public abstract String getName();

	public abstract void msgReadyToMaintain();

}
