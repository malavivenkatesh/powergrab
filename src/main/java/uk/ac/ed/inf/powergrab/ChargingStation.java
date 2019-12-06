package uk.ac.ed.inf.powergrab;

public class ChargingStation {
	
	private Position position;
	private float coins;
	private float power;
	private boolean isGood;
	private String id;
	private boolean visited;
	public static final double chargeRange = 0.00025;
	
	public ChargingStation(Position position, double coins, double power, boolean isGood, String id) {
		this.position = position;
		this.coins = (float) coins;
		this.power = (float) power;
		this.isGood = isGood;
		this.id = id;
		this.visited = false;
	}

	public Position getPos() {
		return position;
	}

	public double getCoins() {
		return coins;
	}

	public void setCoins(double coins) {
		this.coins = (float) coins;
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = (float) power;
	}

	public boolean isGood() {
		return isGood;
	}

	public void setGood(boolean isGood) {
		this.isGood = isGood;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	/**
	 * Charges a drone for being within range of this station.
	 * If the drone has 0 power/coins it will keep the remaining power/coins
	 * @param drone - the drone to charge
	 */
	
	public void charge(Drone drone) {
		float newCoins = (float) (drone.getCoins() + coins);
		float newPower = (float) (drone.getPower() + power);
		double remainingCoins = 0;
		double remainingPower = 0;
		
		// Dealing with case where the coins or power become negative after charging
		if (newCoins < 0) {
			remainingCoins = newCoins;
			newCoins = 0;
		}
		
		if (newPower < 0) {
			remainingPower = newPower;
			newPower = 0;
		}
		
		// Setting new drone and charging station power and coins
		drone.setPower(newPower);
		drone.setCoins(newCoins);
		
		this.coins = (float) remainingCoins;
		this.power = (float) remainingPower;
		this.visited = true;
	}

}
