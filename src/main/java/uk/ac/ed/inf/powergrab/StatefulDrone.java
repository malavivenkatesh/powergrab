package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.mapbox.geojson.Point;

public class StatefulDrone extends Drone {
	
	int lastMovePowerGain = -1;
	// TODO: change variable name
	List<ChargingStation> okaybelikethat = new ArrayList<ChargingStation>();
	
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
//		System.out.println();
		
		goodStations.removeAll(okaybelikethat);
		
		if (goodStations.size() == 0) {
//			System.out.print(getCoins());
			if (!okaybelikethat.isEmpty()) {
				for (ChargingStation cs : okaybelikethat) {
					System.out.println(cs.getId());
					System.out.println();
					List<Direction> moves = aStarSearch(cs, stations, badStations);
					for (Direction dir: moves) {
						Point prevPos = getCurPos();
						move(dir);
						addPathTrace(this.getCurPos());
						System.out.print("Moves: " + getMoves() + "    ");
						Logging.logToTxt(prevPos, getCurPos(), dir, getCoins(), getPower());
						
						boolean charged = inRangeOfStation(stations);	
						if (charged) {
							lastMovePowerGain = getMoves();
//							System.out.println("Charged");
						}
					}
					
					System.out.println(Map.nearestFeature(stations, getCurPos()).getId());
				}
			}
			
			avoidanceStrategy(badStations);
			System.out.println();
//			int j = 0;
//			for (ChargingStation s: okaybelikethat) {
//				j++;
//			}
//			System.out.println(j);
			
			okaybelikethat.forEach(station -> System.out.println(station.getId() + " " + station.getPosition().latitude() + " " + station.getPosition().longitude() + " -- "));
			System.out.println();
			
			return;
		}
		
		ChargingStation nearestStation = Map.nearestFeature(goodStations, getCurPos());
		
		if (getMoves() - lastMovePowerGain > 30) {
			lastMovePowerGain = getMoves();
			okaybelikethat.add(nearestStation);
			goodStations.remove(nearestStation);
			if (goodStations.size() > 0) {
				nearestStation = Map.nearestFeature(goodStations, getCurPos());
			}
			else {
				if (!okaybelikethat.isEmpty()) {
					for (ChargingStation cs : okaybelikethat) {
						System.out.println(cs.getId());
						System.out.println();
						List<Direction> moves = aStarSearch(cs, stations, badStations);
						for (Direction dir: moves) {
							Point prevPos = getCurPos();
							move(dir);
							addPathTrace(this.getCurPos());

							
							boolean charged = inRangeOfStation(stations);	
							if (charged) {
								lastMovePowerGain = getMoves();
//								System.out.println("Charged");
							}							
							System.out.print("Moves: " + getMoves() + "    ");
							Logging.logToTxt(prevPos, getCurPos(), dir, getCoins(), getPower());
						}
						System.out.println(Map.nearestFeature(stations, getCurPos()).getId());
					}
				}
				
				avoidanceStrategy(badStations);
				System.out.println();
//				int j = 0;
//				for (ChargingStation s: okaybelikethat) {
//					j++;
//				}
//				System.out.println(j);
				
				okaybelikethat.forEach(station -> System.out.println(station.getId() + " " + station.getPosition().latitude() + " " + station.getPosition().longitude() + " -- "));
				System.out.println();
				
				return;
			}
		}
		
//		System.out.println(nearestStation.getId());
		
//		aStarSearch(nearestStation, badStations);
		Direction bestDir = findPath(nearestStation, badStations);
		
		Point prevPos = getCurPos();
		move(bestDir);
		System.out.print("Moves: " + getMoves() + "    ");
		Logging.logToTxt(prevPos, getCurPos(), bestDir, getCoins(), getPower());
		
		boolean charged = inRangeOfStation(stations);	
		if (charged) {
			lastMovePowerGain = getMoves();
//			System.out.println("Charged");
		}
		
		searchStrategy(stations);
		
	}
	
	public List<Direction> aStarSearch(ChargingStation goal, List<ChargingStation> stations, List<ChargingStation> badStations) {
		List<Direction> path = new ArrayList<>();
		List<Position> open = new ArrayList<>();
		List<Position> closed = new ArrayList<>();
		int count = 0;
		
		Position curPos = new Position(getCurPos());
		
		closed.add(curPos);
		List<Position> neighbors = addAllNeighbors(open, stations, closed, curPos, goal);
		
		open.addAll(neighbors);
		
		Comparator<Position> c = Comparator.comparing(x -> x.g_score + x.h_score);
		open.sort(c);
		
		Position curCheck = open.get(0);
		Point curCheckPoint = Point.fromLngLat(curCheck.longitude, curCheck.latitude);
//		path.add(curCheck.dirToGetHere);
		while(!((Map.nearestFeature(stations, curCheckPoint).getId()).equals(goal.getId()) && Map.inRange(curCheckPoint, goal.getPosition())) && count < 50) {
			open.remove(0);
			closed.add(curCheck);
			open.addAll(addAllNeighbors(open, stations, closed, curCheck, goal));
			open.sort(c);
			path.add(curCheck.dirToGetHere);
			count++;
			System.out.println(curCheckPoint.latitude() + " " + curCheckPoint.longitude() + " " + curCheck.dirToGetHere);
			System.out.println(Map.nearestFeature(stations, curCheckPoint).getId());
			curPos = curCheck;
			curCheck = open.get(0);
			curCheckPoint = Point.fromLngLat(curCheck.longitude, curCheck.latitude);
		}
		path.add(curCheck.dirToGetHere);
		path.forEach(dir -> System.out.print(dir + " "));
		System.out.println(Map.nearestFeature(stations, curCheckPoint).getId());
		return (path);
		
	}
	
	public List<Position> addAllNeighbors(List<Position> open, List<ChargingStation> stations, 
			List<Position> closed, Position curPos, ChargingStation goal) {
		
		List<Position> neighbors = new ArrayList<>();
		for (Direction dir: Direction.values()) {
			Position nextPos = curPos.nextPosition(dir);
			nextPos.dirToGetHere = dir;
			Point nextPoint = Point.fromLngLat(nextPos.longitude, nextPos.latitude);
			ChargingStation nearestStation = Map.nearestFeature(stations, nextPoint);
			double dist = Position.pythDistanceFrom(nextPoint, nearestStation.getPosition());
			
			if (nextPos.inPlayArea() && (nearestStation.isGood() || (!nearestStation.isGood() && dist >= 0.00025))
					&& !checkInList(open, nextPos) && !checkInList(closed, nextPos)) {
				nextPos.g_score += 1;
				double goalDist = Position.pythDistanceFrom(nextPoint, goal.getPosition());
				nextPos.h_score = (goalDist / 0.0003);
				neighbors.add(nextPos);
			}
		}
		return neighbors;
	}
	
	public boolean checkInList(List<Position> array, Position pos) {
		return(array.stream().anyMatch((p) -> (p.longitude == pos.longitude && p.latitude == pos.latitude)));
	}
	
	public Direction findPath(ChargingStation goal, List<ChargingStation> badStations) {
		
		Set<Direction> avoidDirs = new HashSet<Direction>();
		Direction comeBackToDir;
		
		for (Direction dir : Direction.values()) {
			Position pos = new Position(getCurPos());
			Position nextPos = pos.nextPosition(dir);
			Point nextPoint = Point.fromLngLat(nextPos.longitude, nextPos.latitude);
			ChargingStation nearestFeature = Map.nearestFeature(badStations, nextPoint);
			double distToStation = Position.pythDistanceFrom(nextPoint, nearestFeature.getPosition());
//			System.out.println("Direction: " + dir.toString() + " Closest station: " + nearestFeature.getId() + " Distance: " + distToStation);
			
			if (!nearestFeature.isGood() && distToStation < 0.00025) {
					avoidDirs.add(dir);
				
			}
			if (!nextPos.inPlayArea()) {
				avoidDirs.add(dir);
			}
			
			if (okaybelikethat.contains(nearestFeature)) {
				comeBackToDir = dir;
				return(comeBackToDir);
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
//			System.out.println("Direction: " + dir.toString() + " Distance: " + dist);
			
			if (dist < shortestDistance) {
				shortestDistance = dist;
				bestDir = dir;
			}
		}
		
//		System.out.println("Moves: " + getMoves() +  " Power: " + getPower() + " Next Direction: " + bestDir.toString());
//		avoidDirs.forEach(dir -> System.out.print(dir.toString() + " "));
//		System.out.println();
//		possibleDirs.forEach(dir -> System.out.print(dir.toString() + " "));
//		System.out.println();
		
		return(bestDir);
					
	}
	
	public void avoidanceStrategy(List<ChargingStation> badStations) {
		
		for (int i = getMoves(); i < 250; i++) {
			for(Direction dir : Direction.values()) {
				
				Position pos = new Position(getCurPos());
				Position nextPos = pos.nextPosition(dir);
				Point nextPoint = Point.fromLngLat(nextPos.longitude, nextPos.latitude);
				
				ChargingStation nearestFeature = Map.nearestFeature(badStations, nextPoint);
				
				double dist = Position.pythDistanceFrom(nextPoint, nearestFeature.getPosition());
					
				if (dist < 0.00025 || !nextPos.inPlayArea()) {
					continue;
				}
				else {
					Point prevPos = getCurPos();
					addPathTrace(this.getCurPos());
					move(dir);
					System.out.print("Moves: " + getMoves());
					Logging.logToTxt(prevPos, getCurPos(), dir, getCoins(), getPower());
					
					break;
				}
			}
		}
		
		return;
	}
		
	
	public static void main() {
	}

}
