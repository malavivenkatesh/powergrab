package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;

public abstract class Drone {
	
	private double power;
	private double coins;
	private ArrayList<Position> pathTrace = new ArrayList<Position>();
	
	public Drone() {
		this.setPower(250.0);
		this.setCoins(0);
	}
	
	public Drone(double power, double coins) {
		this.setPower(power);
		this.setCoins(coins);
	}
	
	public abstract void searchStrategy();
	
	public void addPathTrace(Position p) {
		this.pathTrace.add(p);
	}
	
	public ArrayList<Position> getPathTrace() {
		return(this.pathTrace);
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}

	public double getCoins() {
		return coins;
	}

	public void setCoins(double coins) {
		this.coins = coins;
	}
	
}
