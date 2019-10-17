package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Feature;

public abstract class Drone {
	
	private double power;
	private double coins;
	private List<Integer> pathTrace = new ArrayList<Integer>();
	
	public Drone() {
		this.setPower(250.0);
		this.coins = 0;
	}
	
	public Drone(double power, double coins) {
		this.setPower(power);
		this.coins = coins;
	}
	
	public abstract void searchStrategy();
	
	public void setPathTrace(int i) {
		this.pathTrace.add(i);
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}
	
}
