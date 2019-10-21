package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.Point;

public class ChargingStation {
	
	private Point position;
	private double coins;
	private double power;
	private boolean isGood;
	private String id;
	
	public ChargingStation(Point position, double coins, double power, boolean isGood, String id) {
		this.position = position;
		this.coins = coins;
		this.power = power;
		this.isGood = isGood;
		this.id = id;
	}
	
	public void charge(Drone drone) {
		drone.setPower(drone.getPower() + power);
		drone.setCoins(drone.getCoins() + coins);
		
		this.coins = 0;
		this.power = 0;
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

}
