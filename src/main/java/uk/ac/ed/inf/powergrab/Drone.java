package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mapbox.geojson.Point;

public abstract class Drone {
	// TODO remove getters & setters as necessary
	private float power;
	private float coins;
	private int moves;
	private ArrayList<Point> pathTrace = new ArrayList<Point>();
	private Random rnd;
	private Point curPoint;
	public static final double moveRange = 0.0003;
	public static final int maxMoves = 250;
	public static final double powerPerMove = 1.25;
	
	public Drone(Point curPos, int seed) {
		this.setPower((float) 250.0);
		this.setCoins(0);
		this.moves = 0;
		this.setCurPoint(curPos);
		this.rnd = new Random(seed);
		
	}
	
	public Drone(float power, float coins, int moves, Point curPos, int seed) {
		this.setPower(power);
		this.setCoins(coins);
		this.moves = moves;
		this.setCurPoint(curPos);
		this.rnd = new Random(seed);
	}
	
	public void addPathTrace(Point point) {
		this.pathTrace.add(point);
	}
	
	public List<Point> getPathTrace() {
		return(this.pathTrace);
	}

	public float getPower() {
		return power;
	}

	public void setPower(float power) {
		this.power = power;
	}

	public float getCoins() {
		return coins;
	}

	public void setCoins(float coins) {
		this.coins = coins;
	}

	public Point getCurPoint() {
		return curPoint;
	}

	public void setCurPoint(Point curPoint) {
		this.curPoint = curPoint;
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
	public boolean endCondition() {
		return (moves >= maxMoves || power < powerPerMove);
	}
	
	// Returns true or false indicating whether or not the move was successful
	public boolean move(Direction dir) {
		if (endCondition()) {
			return(false);
		}
		
		this.power -= powerPerMove;
		this.moves++;
		Position pos = new Position(getCurPoint());
		Position newPos = pos.nextPosition(dir);
		Point newPoint = Point.fromLngLat(newPos.longitude, newPos.latitude);
		this.setCurPoint(newPoint);
		
		addPathTrace(getCurPoint());
		
		return(true);
	}
	
	// If the drone is in range of a station, transfer power and coins from 
	// the closest station
	public boolean inRangeOfStation() {
		ChargingStation nearestFeature = Map.nearestFeature(Map.getStations(), getCurPoint());
		
		if(Map.inRange(this.getCurPoint(), nearestFeature.getLocation())) {
			nearestFeature.charge(this);
			return(true);
		}
		return(false);
	}
	
	
	// Starts the logging for the search and sets off the drone's search.
	// Closes BufferedWriter for logging after the search is done.
	public void initSearchStrategy(String year, String month, String day, String state) {
		try {
			Logging.setWriter(year, month, day, state);
		} catch (IOException e) {
			e.printStackTrace();
		}
        searchStrategy();
        try {
			Logging.bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Each drone implements a search strategy for a given map
	public abstract void searchStrategy();
		
}
