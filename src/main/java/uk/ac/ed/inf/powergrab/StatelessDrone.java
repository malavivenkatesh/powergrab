package uk.ac.ed.inf.powergrab;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.mapbox.geojson.Point;

public class StatelessDrone extends Drone {

	
	public StatelessDrone(Point curPos, int seed) {
		super(curPos, seed);
	}
	
	
	public StatelessDrone(double power, double coins, Point pos, int seed) {
		super(power, coins, 0, pos, seed);
	}

	/*
	 * Implements the Drone class's abstract method to search without memory and with limited lookahead.
	 * The drone checks the stations in range at each step, moves in the best direction and then charges
	 * if in range of a station. 
	 * It recursively continues to search until it reaches the end conditions.
	 */
	@Override
	public void searchStrategy() {
		addPathTrace(this.getCurPoint());
		
		if (endCondition()) {
			return;
		}
		
		// Automatically charge after every move
		if (getMoves() > 1) {
			boolean charged = inRangeOfStation();
			
			if (charged) {
				System.out.printf("Charged! Moves: %d Power: %3f Coins %3f\n", getMoves(), getPower(), getCoins());
			}
		}
		
		// Keep track of the stations in range
		HashMap<ChargingStation, Direction> goodStationRange = new HashMap<ChargingStation, Direction>();
		HashMap<ChargingStation, Direction> badStationRange = new HashMap<ChargingStation, Direction>();
		Set<Direction> avoidDirs = new HashSet<Direction>();

		
		for (Direction dir : Direction.values()) {
			Position curPos = new Position(getCurPoint());
			Position nextPos = curPos.nextPosition(dir);
			Point nextPoint = Point.fromLngLat(nextPos.longitude, nextPos.latitude);
			ChargingStation nearestFeature = Map.nearestFeature(Map.getStations(), nextPoint);
			
			
			if (Map.inRange(nextPoint, nearestFeature.getLocation())) {
				if (nearestFeature.getCoins() > 0 && nearestFeature.getPower() > 0) {
					goodStationRange.put(nearestFeature, dir);
				}
				else if (!nearestFeature.isGood()) {
					badStationRange.put(nearestFeature, dir);
					avoidDirs.add(dir);
				}
			}
			if (!nextPos.inPlayArea()) {
				avoidDirs.add(dir);
			}
		}
		
		// Picking the best value based on adding together the power and coins at each station
		// and choosing the station with the max value
		Direction nextDir;
		
		// Compare the numerical value of the station by adding power and coins together
		Comparator<ChargingStation> compCoinsAndPower = 
				Comparator.comparing(x-> x.getCoins() + x.getPower());
		
		// Pick the best good station in range
		if (goodStationRange.size() > 0) {			
			ChargingStation bestStation = Collections.max(goodStationRange.keySet(), compCoinsAndPower);
			nextDir = goodStationRange.get(bestStation);
		}
		// Or the least bad station if surrounded by bad stations
		else if (badStationRange.size() == 16) {
			ChargingStation bestStation = Collections.max(badStationRange.keySet(), compCoinsAndPower);
			nextDir = badStationRange.get(bestStation);
		}
		// Otherwise move so not in range of any bad station
		else {
			Set<Direction> possibleDirs = new HashSet<Direction>();
			Collections.addAll(possibleDirs, Direction.values());
			
			possibleDirs.removeAll(avoidDirs);
			
			int possibilities = possibleDirs.size();
			int randomInt = getRnd().nextInt(possibilities);
			nextDir = (Direction) possibleDirs.toArray()[randomInt];			
		}
		
		
		Point prevPos = getCurPoint();
		move(nextDir);
		System.out.print("Moves " + getMoves() + "    ");
		Logging.logToTxt(prevPos, getCurPoint(), nextDir, getCoins(), getPower());
		
		searchStrategy();
		
	}

}
