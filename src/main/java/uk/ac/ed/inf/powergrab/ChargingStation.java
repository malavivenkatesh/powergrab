package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.Point;

public class ChargingStation {
	
	private Point position;
	private double coins;
	private double power;
	private boolean isGood;
	
	public ChargingStation(Point position, double coins, double power, boolean isGood) {
		this.position = position;
		this.coins = coins;
		this.power = power;
		this.isGood = isGood;
	}
	
	public void charge(Drone drone) {
		drone.setPower(drone.getPower() + power);
		drone.setCoins(drone.getCoins() +coins);
		
		this.coins = 0;
		this.power = 0;
		this.isGood = false;
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

}
