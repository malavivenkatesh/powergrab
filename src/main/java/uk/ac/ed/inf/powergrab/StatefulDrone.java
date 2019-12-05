package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.mapbox.geojson.Point;

public class StatefulDrone extends Drone {	
	
	public StatefulDrone(Point curPos, int seed) {
		super(curPos, seed);
	}

	
	public StatefulDrone(float power, float coins, Point pos, int seed) {
		super(power, coins, 0, pos, seed);
	}
	
	
	@Override
	public void searchStrategy() {
		addPathTrace(this.getCurPoint());
		
		List<ChargingStation> goodStations = Map.getStations().
				stream().filter(station -> station.isGood() && !station.isVisited()).
				collect(Collectors.toList());
		List<ChargingStation> badStations =  Map.getStations().
				stream().filter(station -> !station.isGood()).
				collect(Collectors.toList());
		// TODO: change variable name
		List<ChargingStation> okaybelikethat = new ArrayList<ChargingStation>();
		
		while (!goodStations.isEmpty()) {
			ChargingStation station = Map.nearestFeature(goodStations, getCurPoint());
			goodStations.remove(station);
			System.out.println("Greedy Search Choice: " + getMoves());
			List<Direction> moves = greedySearch(station, badStations);
			if (moves == null) {
				okaybelikethat.add(station);
				continue;
			}
			
			boolean moved = makeMoves(moves);
			if (!moved) {
				return;
			}
		}
		
		System.out.println("Done all good stations in " + getMoves() + " moves");
		okaybelikethat.forEach(x -> System.out.println(x.getId()));
		System.out.println();
		
		if (!okaybelikethat.isEmpty()) {
			for (ChargingStation cs : okaybelikethat) {
//					System.out.println(cs.getId());
//					System.out.println();
				System.out.println("Move: " + getMoves());
				List<Direction> moves = greedySearch(cs, badStations);
				if (moves == null) {
					System.out.println("Null search");
					continue;
					}
				
				boolean moved = makeMoves(moves);
				if (!moved) {
					return;
				}
			}
		}
		okaybelikethat.forEach(x -> System.out.println(x.getId()));
		System.out.println();
		
		avoidanceStrategy(badStations);
		System.out.println();
		
		return;
	}
	
	private boolean makeMoves(List<Direction> moves) {
		for (Direction dir: moves) {
			Point prevPos = getCurPoint();
			boolean moved = move(dir);
			
			if (!moved) {
				return(false);
			}
			
			boolean charged = inRangeOfStation();	
			if (charged) {
				System.out.printf("Charged! Moves: %d Power: %3f Coins %3f\n", getMoves(), getPower(), getCoins());
			}				
			System.out.print("Moves: " + getMoves() + "    ");
			Logging.logToTxt(prevPos, getCurPoint(), dir, getCoins(), getPower());
		}
		return (true);
	}
		
	
	public List<Direction> greedySearch(ChargingStation goal, List<ChargingStation> badStations) {
		List<Direction> path = new ArrayList<>();
		List<Position> open = new ArrayList<>();
		List<Position> closed = new ArrayList<>();
		int count = 0;
		boolean goalReached = false;
		
		Position curPos = new Position(getCurPoint());
		
		closed.add(curPos);
		curPos.h_score = Position.pythDistanceFrom(goal.getLocation(), getCurPoint());
		List<Position> neighbors = addAllNeighbors(open, Map.getStations(), closed, curPos, goal);
		
		open.addAll(neighbors);
		
		Comparator<Position> c = Comparator.comparing(x -> x.h_score);
		open.sort(c);
		
		Position curCheck = open.get(0);
		Point curCheckPoint = Point.fromLngLat(curCheck.longitude, curCheck.latitude);
		while(!(goalReached) && count < 50) {
			
			if (open.isEmpty()) {
				return (null);
			}
			
			closed.add(curCheck);
			open.removeAll(open);
			
			neighbors = addAllNeighbors(open, Map.getStations(), closed, curCheck, goal);
			open.addAll(neighbors);
			
			if (open.isEmpty()) {
				return (null);
			}
			
			open.sort(c);
			path.add(curCheck.dirToGetHere);
			count++;
			if (open.isEmpty()) {
				return (null);
			}
			
			open.sort(c);
			curCheck = open.get(0);
			curCheckPoint = Point.fromLngLat(curCheck.longitude, curCheck.latitude);
			goalReached = Map.nearestFeature(Map.getStations(), curCheckPoint).getId().equals(goal.getId()) 
					&& Map.inRange(curCheckPoint, goal.getLocation());
		}
		
		path.add(curCheck.dirToGetHere);
		
		if (goalReached) {return (path);}
		else {return (null);}
	}
	
	
	public List<Position> addAllNeighbors(List<Position> open, List<ChargingStation> stations, 
			List<Position> closed, Position curPos, ChargingStation goal) {
		
		List<Position> neighbors = new ArrayList<>();
		for (Direction dir: Direction.values()) {
			Position nextPos = curPos.nextPosition(dir);
			nextPos.dirToGetHere = dir;
			Point nextPoint = Point.fromLngLat(nextPos.longitude, nextPos.latitude);
			ChargingStation nearestStation = Map.nearestFeature(Map.getStations(), nextPoint);
			double dist = Position.pythDistanceFrom(nextPoint, nearestStation.getLocation());
			if (nextPos.inPlayArea()) {
					if ((nearestStation.isGood() || (!nearestStation.isGood() && dist >= ChargingStation.chargeRange))
							&& !checkPosInList(open, nextPos) && !checkPosInList(closed, nextPos)) {
					double goalDist = Position.pythDistanceFrom(nextPoint, goal.getLocation());
					nextPos.h_score = (goalDist);
					neighbors.add(nextPos);
				}
			}
		}
		
		return neighbors;
	}
	
	
	public boolean checkPosInList(List<Position> list, Position pos) {
		return(list.stream().anyMatch((p) -> 
		(p.longitude == pos.longitude && p.latitude == pos.latitude)));
	}
	
	
	public void avoidanceStrategy(List<ChargingStation> badStations) {
		
		for (int i = getMoves(); i < maxMoves; i++) {
			if (endCondition()) {
				break;
			}
			
			for(Direction dir : Direction.values()) {
				
				Position curPos = new Position(getCurPoint());
				Position nextPos = curPos.nextPosition(dir);
				Point nextPoint = Point.fromLngLat(nextPos.longitude, nextPos.latitude);
				
				ChargingStation nearestFeature = Map.nearestFeature(badStations, nextPoint);
				double dist = Position.pythDistanceFrom(nextPoint, nearestFeature.getLocation());
					
				if (dist < ChargingStation.chargeRange || !nextPos.inPlayArea()) {
					continue;
				}
				
				else {
					Point prevPos = getCurPoint();
					move(dir);
					System.out.print("Moves: " + getMoves() + "    ");
					Logging.logToTxt(prevPos, getCurPoint(), dir, getCoins(), getPower());
					break;
				}
			}
		}
		
		return;
	}

}
