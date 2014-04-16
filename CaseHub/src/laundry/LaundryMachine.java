package laundry;

/**
 * Represents a washer or dryer.
 */
public class LaundryMachine {

	private int machineNumber;
	private int minutesLeft;
	private String type;
	private String status;
		
	public LaundryMachine(int machineNumber, int minutesLeft, String type, String status) {
		this.machineNumber = machineNumber;
		this.minutesLeft = minutesLeft;
		this.type = type;
		this.status = status;
	}

	public int getMachineNumber() {
		return machineNumber;
	}

	public int getMinutesLeft() {
		return minutesLeft;
	}
	/*
	public Type getType() {
		return type;
	}

	public Status getStatus() {
		return status;
	}
	*/
	enum Type {
		WASHER, DRYER
	}
	
	enum Status {
		AVAILABLE, IN_USE, CYCLE_COMPLETE, UNAVAILABLE
	}
	
}
