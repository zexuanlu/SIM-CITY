package restaurant5.test.mock;

/**
 * This is the base class for all mocks.
 *
 * @author Sean Turner
 *
 */
public class Mock5 {
	private String name;

	public Mock5(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return this.getClass().getName() + ": " + name;
	}

}
