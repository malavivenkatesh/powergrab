package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Feature;

public abstract class Drone {
	
	private double power;
	private double coins;
	private ArrayList<Integer> pathTrace = new ArrayList<Integer>();
	
	public Drone() {
		this.setPower(250.0);
		this.setCoins(0);
	}
	
	public Drone(double power, double coins) {
		this.setPower(power);
		this.setCoins(coins);
	}
	
	public abstract void searchStrategy();
	
	
	// TODO Fix path trace implementation
	public void addPathTrace(int i) {
		this.pathTrace.add(i);
	}
	
	public ArrayList<Integer> getPathTrace() {
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
