package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mapbox.geojson.Point;

public abstract class Drone {
	// TODO remove getters & setters as necessary
	private double power;
	private double coins;
	private int moves;
	private ArrayList<Point> pathTrace = new ArrayList<Point>();
	private Random rnd;
	private Point curPos;
	
	public Drone(Point curPos, int seed) {
		this.setPower(250.0);
		this.setCoins(0);
		this.moves = 0;
		this.setCurPos(curPos);
		this.rnd = new Random(seed);
		
	}
	
	public Drone(double power, double coins, int moves, Point curPos, int seed) {
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
		Position pos = new Position(getCurPos());
		Position newPos = pos.nextPosition(dir);
		Point newPoint = Point.fromLngLat(newPos.longitude, newPos.latitude);
		this.setCurPos(newPoint);
		
		return(true);
	}
	
	// If the drone is in range of a station, transfer power and coins from 
	// the closest station
	public boolean inRangeOfStation(List<ChargingStation> stations) {
		double shortestDistance = 0;
		ChargingStation closestFeature = stations.get(0);
		
		for (ChargingStation feat : stations) {
			Point point = (Point) feat.getPosition();
			
			double dist = Position.pythDistanceFrom(getCurPos(), point);
			
			if (dist < shortestDistance) {
				shortestDistance = dist;
				closestFeature = feat;
			}
		}
		
		if(shortestDistance <= 0.00025) {
			closestFeature.charge(this);
			return(true);
		}
		
		return(false);
	}
	
	// Each drone should implement a search strategy for a given map
	public abstract void searchStrategy(List<ChargingStation> stations);
	
	public void addPathTrace(Point point) {
		this.pathTrace.add(point);
	}
	
	public ArrayList<Point> getPathTrace() {
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

	public Point getCurPos() {
		return curPos;
	}

	public void setCurPos(Point curPos) {
		this.curPos = curPos;
	}

	public Random getRnd() {
		return rnd;
	}
	
	public int getMoves() {
		return (this.moves);
	}
	
	public void setMoves(int moves) {
		this.moves = moves;
	}
	
}
