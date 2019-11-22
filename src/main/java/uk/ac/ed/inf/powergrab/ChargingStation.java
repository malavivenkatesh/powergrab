package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.Point;

public class ChargingStation {
	
	private Point position;
	private double coins;
	private double power;
	private boolean isGood;
	private String id;
	private boolean visited;
	
	public ChargingStation(Point position, double coins, double power, boolean isGood, String id) {
		this.position = position;
		this.coins = coins;
		this.power = power;
		this.isGood = isGood;
		this.id = id;
		this.visited = false;
	}
	
	public void charge(Drone drone) {
		double newCoins = drone.getCoins() + coins;
		double newPower = drone.getPower() + power;
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
		
		this.coins = remainingCoins;
		this.power = remainingPower;
		this.visited = true;
	}

	public Point getPosition() {
		return position;
	}

	public double getCoins() {
		return coins;
	}

	public void setCoins(double coins) {
		this.coins = coins;
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
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

}
