package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Drone {
	private float power;
	private float coins;
	private int moves;
	private List<Position> pathTrace = new ArrayList<Position>();
	private Random rnd;
	private Position curPos;
	public static final double moveRange = 0.0003;
	public static final int maxMoves = 250;
	public static final double powerPerMove = 1.25;
	
	public Drone(Position curPos, int seed) {
		this.setPower((float) 250.0);
		this.setCoins(0);
		this.moves = 0;
		this.setCurPos(curPos);
		this.rnd = new Random(seed);
	}
	
	public Drone(float power, float coins, int moves, Position curPos, int seed) {
		this.setPower(power);
		this.setCoins(coins);
		this.moves = moves;
		this.setCurPos(curPos);
		this.rnd = new Random(seed);
	}
	
	/**
	 * Adds to the list of previous positions the drone has been to before
	 * @param point - the point to add to the list 
	 */
	public void addPathTrace(Position point) {
		this.pathTrace.add(point);
	}
	
	public List<Position> getPathTrace() {
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

	public Position getCurPos() {
		return curPos;
	}

	public void setCurPos(Position curPoint) {
		this.curPos = curPoint;
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
	
	/**
	 * Moves the drone to a new position and charges it for the move
	 * @param dir - the direction in which to move
	 * @return - true or false if the move was successful or not
	 */
	public boolean move(Direction dir) {
		if (endCondition()) {
			return(false);
		}
		
		this.power -= powerPerMove;
		this.moves++;
		Position newPos = getCurPos().nextPosition(dir);
		this.setCurPos(newPos);
		
		addPathTrace(getCurPos());
		
		return(true);
	}
	
	/**
	 *  If the drone is in range of a station, transfer power and coins from
	 *  the closest station 
	 * @return - true or false for if the drone was in range of a station or not
	 */
	public boolean inRangeOfStation() {
		ChargingStation nearestFeature = Map.nearestFeature(Map.getStations(), getCurPos());
		
		if(Map.inRange(this.getCurPos(), nearestFeature.getPos())) {
			nearestFeature.charge(this);
			return(true);
		}
		return(false);
	}
	
	/**
	 * Starts the logging for the search and sets off the drone's search.
	 * Closes BufferedWriter for logging after the search is done.
	 * @param year
	 * @param month
	 * @param day
	 * @param state
	 */
	public void initSearchStrategy(String year, String month, String day, String state) {
		Logging.setWriter(year, month, day, state);
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
