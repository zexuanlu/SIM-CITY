package resident.interfaces;

public interface HomeOwner {

	public abstract void msgDoneMaintaining(double maintenanceAmount);

	public abstract void msgReceivedPayment(double amountPaid);

	public abstract void msgYouHaveDebt(double amountOwed);

}
