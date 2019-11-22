package uk.ac.ed.inf.powergrab;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.mapbox.geojson.Point;

public class StatefulDrone extends Drone {
	
	public StatefulDrone(Point curPos, int seed) {
		super(curPos, seed);
	}

	public StatefulDrone(double power, double coins, Point pos, int seed) {
		super(power, coins, 0, pos, seed);
	}
	
	@Override
	public void searchStrategy(List<ChargingStation> stations) {
		addPathTrace(this.getCurPos());
		// End conditions
		if (getPower() < 1.25 || getMoves() >= 250) {
			return;
		}		
		
		
		List<ChargingStation> goodStations = stations.
				stream().filter(station -> station.isGood() && !station.isVisited()).
				collect(Collectors.toList());
		List<ChargingStation> badStations =  stations.
				stream().filter(station -> !station.isGood()).
				collect(Collectors.toList());
		
//		goodStations.forEach(station -> System.out.print(station.getId() + " " + station.getPosition().latitude() + " " + station.getPosition().longitude()));
		System.out.println();
		
		if (goodStations.size() == 0) {
			System.out.print(getCoins());
			return;
		}
		
		ChargingStation nearestStation = Map.nearestFeature(goodStations, getCurPos());
		System.out.println(nearestStation.getId());
		
//		aStarSearch(nearestStation, badStations);
		Direction bestDir = findPath(nearestStation, badStations);
		
		Point prevPos = getCurPos();
		move(bestDir);
		Logging.logToTxt(prevPos, getCurPos(), bestDir, getCoins(), getPower());
		
		boolean charged = inRangeOfStation(stations);	
		if (charged) {
			System.out.println("Charged");
		}
		
		searchStrategy(stations);
		
	}
	
	public void aStarSearch(ChargingStation goal, List<ChargingStation> badStations) {
		
	}
	
	public Direction findPath(ChargingStation goal, List<ChargingStation> badStations) {
		
		Set<Direction> avoidDirs = new HashSet<Direction>();
		
		for (Direction dir : Direction.values()) {
			Position pos = new Position(getCurPos());
			Position nextPos = pos.nextPosition(dir);
			Point nextPoint = Point.fromLngLat(nextPos.longitude, nextPos.latitude);
			ChargingStation nearestFeature = Map.nearestFeature(badStations, nextPoint);
			double distToStation = Position.pythDistanceFrom(nextPoint, nearestFeature.getPosition());
			System.out.println("Direction: " + dir.toString() + " Closest station: " + nearestFeature.getId() + " Distance: " + distToStation);
			
			if (!nearestFeature.isGood() && distToStation < 0.00025) {
					avoidDirs.add(dir);
				
			}
			if (!nextPos.inPlayArea()) {
				avoidDirs.add(dir);
			}
		}
		
		Set<Direction> possibleDirs = new HashSet<Direction>();
		Collections.addAll(possibleDirs, Direction.values());
		
		possibleDirs.removeAll(avoidDirs);
		
		
		double shortestDistance = Integer.MAX_VALUE;
		Direction bestDir = Direction.N;
		
		for (Direction dir : possibleDirs) {
			
			Position pos = new Position(getCurPos());
			Position nextPos = pos.nextPosition(dir);
			Point nextPoint = Point.fromLngLat(nextPos.longitude, nextPos.latitude);
			
			double dist = Position.pythDistanceFrom(nextPoint, goal.getPosition());
			System.out.println("Direction: " + dir.toString() + " Distance: " + dist);
			
			if (dist < shortestDistance) {
				shortestDistance = dist;
				bestDir = dir;
			}
		}
		
		System.out.println("Moves: " + getMoves() +  " Power: " + getPower() + " Next Direction: " + bestDir.toString());
		avoidDirs.forEach(dir -> System.out.print(dir.toString() + " "));
		System.out.println();
		possibleDirs.forEach(dir -> System.out.print(dir.toString() + " "));
		System.out.println();
		
		return(bestDir);
					
	}
	
	public static void main() {
	}

}
