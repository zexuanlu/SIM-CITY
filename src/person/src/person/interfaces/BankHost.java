package person.interfaces;

public interface BankHost {
	public abstract void msgGoToBank(String task, int amount);
	public abstract void msgBackToWork();
}
