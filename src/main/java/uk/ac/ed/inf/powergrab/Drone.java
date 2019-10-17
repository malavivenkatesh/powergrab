package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Random;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;

public abstract class Drone {
	// TODO remove getters & setters as necessary
	private double power;
	private double coins;
	private double moves;
	private ArrayList<Position> pathTrace = new ArrayList<Position>();
	private Position curPos;
	private Random rnd;
	
	public Drone(Position curPos, int seed) {
		this.setPower(250.0);
		this.setCoins(0);
		this.moves = 0;
		this.setCurPos(curPos);
		this.rnd = new Random(seed);
		
	}
	
	public Drone(double power, double coins, int moves, Position curPos, int seed) {
		this.setPower(power);
		this.setCoins(coins);
		this.moves = moves;
		this.setCurPos(curPos);
		this.rnd = new Random(seed);
	}
	
	// Returns true or false indicating whether or not the move was successful
	public boolean move(Direction dir) {
		// End conditions for the game
		if (moves >= 250 || power < 1.25) {
			return(false);
		}
		
		this.power -= 1.25;
		this.moves += 1;
		addPathTrace(this.getCurPos());
		this.setCurPos(getCurPos().nextPosition(dir));
		
		return(true);
	}
	
	// If the drone is in range of a station, transfer power and coins from 
	// the closest station
	public boolean inRangeOfStation(ArrayList<Feature> featureList) {
		double shortestDistance = 0;
		Feature closestFeature = featureList.get(0);
		
		for (Feature feat : featureList) {
			Point point = (Point) feat.geometry();
			Position p = new Position(point.latitude(), point.longitude());
			
			double dist = getCurPos().pythDistanceFrom(p);
			
			if (dist < shortestDistance) {
				shortestDistance = dist;
				closestFeature = feat;
			}
		}
		
		if(shortestDistance <= 0.00025) {
			this.coins += closestFeature.getProperty("coins").getAsDouble();
			this.power += (double) closestFeature.getProperty("power").getAsDouble();
			return(true);
		}
		
		return(false);
	}
	
	// Each drone should implement a search strategy for a given map
	public abstract void searchStrategy(ArrayList<Feature> featureList);
	
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

	public Position getCurPos() {
		return curPos;
	}

	public void setCurPos(Position curPos) {
		this.curPos = curPos;
	}

	public Random getRnd() {
		return rnd;
	}
	
}
