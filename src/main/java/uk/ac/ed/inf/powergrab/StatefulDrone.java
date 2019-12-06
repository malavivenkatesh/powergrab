package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StatefulDrone extends Drone {	
	
	public StatefulDrone(Position curPos, int seed) {
		super(curPos, seed);
	}

	
	public StatefulDrone(float power, float coins, Position pos, int seed) {
		super(power, coins, 0, pos, seed);
	}
		
	/**
	 * Controls the main outline of the search strategy.
	 * Finds all the good stations and stations to avoid. Then picks the nearest station
	 * and gets a path to it. Then moves following that path.
	 * Repeats until there are no more good stations, then avoids the bad stations
	 * until the end of the game.
	 */
	@Override
	public void searchStrategy() {
		addPathTrace(this.getCurPos());
		
		List<ChargingStation> goodStations = Map.getStations().
				stream().filter(station -> station.isGood() && !station.isVisited()).
				collect(Collectors.toList());
		List<ChargingStation> badStations =  Map.getStations().
				stream().filter(station -> !station.isGood()).
				collect(Collectors.toList());
		// TODO: change variable name
		List<ChargingStation> holdingStations = new ArrayList<ChargingStation>();
		
		while (!goodStations.isEmpty()) {
			ChargingStation station = Map.nearestFeature(goodStations, getCurPos());
			goodStations.remove(station);
			System.out.println("Greedy Search Choice: " + getMoves());
			List<Direction> moves = greedySearch(station, badStations);
			if (moves == null) {
				holdingStations.add(station);
				continue;
			}
			
			boolean moved = makeMoves(moves);
			if (!moved) {
				return;
			}
		}
		
		System.out.println("Done all good stations in " + getMoves() + " moves");
		holdingStations.forEach(x -> System.out.println(x.getId()));
		System.out.println();
		
		while(!holdingStations.isEmpty()) {
			ChargingStation station = Map.nearestFeature(holdingStations, getCurPos());
			holdingStations.remove(station);
			System.out.println("Move: " + getMoves());
			List<Direction> moves = greedySearch(station, badStations);
			if (moves == null) {
				System.out.println("Null search");
				continue;
				}
			
			boolean moved = makeMoves(moves);
			if (!moved) {
				return;
			}
			
		}
		
		holdingStations.forEach(x -> System.out.println(x.getId()));
		System.out.println();
		
		avoidanceStrategy(badStations);
		System.out.println();
		
		return;
	}
	
	/**
	 * Given a list of directions, moves the drone in each direction in turn
	 * @param moves - lit of directions in which to move
	 * @return - boolean based on whether all the moves were successful
	 */
	private boolean makeMoves(List<Direction> moves) {
		for (Direction dir: moves) {
			Position prevPos = getCurPos();
			boolean moved = move(dir);
			
			if (!moved) {
				return(false);
			}
			
			boolean charged = inRangeOfStation();	
			if (charged) {
				System.out.printf("Charged! Moves: %d Power: %3f Coins %3f\n", 
						getMoves(), getPower(), getCoins());
			}				
			System.out.print("Moves: " + getMoves() + "    ");
			Logging.logToTxt(prevPos, getCurPos(), dir, getCoins(), getPower());
		}
		return (true);
	}
		
	
	private List<Direction> greedySearch(ChargingStation goal, List<ChargingStation> badStations) {
		List<Direction> path = new ArrayList<>();
		List<Position> open = new ArrayList<>();
		List<Position> closed = new ArrayList<>();
		int count = 0;
		boolean goalReached = false;
		Comparator<Position> hScoreComp = Comparator.comparing(x -> x.hScore);
		
		Position curPos = (getCurPos());
		closed.add(curPos);
		curPos.hScore = curPos.pythDistanceFrom(goal.getPos());
		
		List<Position> neighbors = addAllNeighbors(open, closed, curPos, goal);
		
		open.addAll(neighbors);
		open.sort(hScoreComp);
		Position curCheck = open.get(0);
		while(!(goalReached) && count < 50) {
			if (open.isEmpty()) {
				return(null);
			}
			closed.add(curCheck);
			open.removeAll(open);
			
			neighbors = addAllNeighbors(open, closed, curCheck, goal);
			open.addAll(neighbors);
			
			if (open.isEmpty()) {
				return(null);
			}
			
			open.sort(hScoreComp);
			
			path.add(curCheck.dirToGetHere);
			count++;
			
			curCheck = open.get(0);
			
			goalReached = Map.nearestFeature(Map.getStations(), curCheck).getId().equals(goal.getId()) 
					&& Map.inRange(curCheck, goal.getPos());
		}
		path.add(curCheck.dirToGetHere);
		
		if (goalReached) {return (path);}
		else {return (null);}
	}
	
	/**
	 * Finds all the valid (in the play area, not searched and not in range of a bad station)
	 * neighboring positions (positions one move away). Adds a hScore to each based on how  
	 * close each is to the goal.
	 * @param open - list of positions already being considered
	 * @param closed - list of positions already considered
	 * @param curPos - the position from which to find all the nieghbors
	 * @param goal - the final target of the search 
	 * @return - a list of the valid neighbors
	 */
	private List<Position> addAllNeighbors(List<Position> open, List<Position> closed, 
			Position curPos, ChargingStation goal) {
		
		List<Position> neighbors = new ArrayList<>();
		for (Direction dir: Direction.values()) {
			Position nextPos = curPos.nextPosition(dir);
			nextPos.dirToGetHere = dir;
			ChargingStation nearestStation = Map.nearestFeature(Map.getStations(), nextPos);
			double dist = nextPos.pythDistanceFrom(nearestStation.getPos());
			if (nextPos.inPlayArea()) {
					if ((nearestStation.isGood() || (!nearestStation.isGood() && dist >= ChargingStation.chargeRange))
							&& !checkPosInList(open, nextPos) && !checkPosInList(closed, nextPos)) {
					double goalDist = nextPos.pythDistanceFrom(goal.getPos());
					nextPos.hScore = (goalDist);
					neighbors.add(nextPos);
				}
			}
		}
		
		return neighbors;
	}
	
	/**
	 * Checks if a position is in a list, only considering its latitude and longitude
	 * @param list - the list to check
	 * @param pos - the position to find in the list
	 * @return - boolean based on whether the position is in the list or not
	 */
	private boolean checkPosInList(List<Position> list, Position pos) {
		return(list.stream().anyMatch((p) -> 
		(p.getLongitude() == pos.getLongitude() && p.getLatitude() == pos.getLatitude())));
	}
	
	/**
	 * Avoids the given list of bad stations until the drone runs out of moves
	 * or runs out of power
	 * @param badStations - the list of charging stations to avoid
	 */
	private void avoidanceStrategy(List<ChargingStation> badStations) {
		
		for (int i = getMoves(); i < maxMoves; i++) {
			if (endCondition()) {
				break;
			}
			
			for(Direction dir : Direction.values()) {
				
				Position curPos = getCurPos();
				Position nextPos = curPos.nextPosition(dir);
				
				ChargingStation nearestFeature = Map.nearestFeature(badStations, nextPos);
				double dist = nextPos.pythDistanceFrom(nearestFeature.getPos());
					
				if (dist < ChargingStation.chargeRange || !nextPos.inPlayArea()) {
					continue;
				}
				
				else {
					Position prevPos = getCurPos();
					move(dir);
					System.out.print("Moves: " + getMoves() + "    ");
					Logging.logToTxt(prevPos, getCurPos(), dir, getCoins(), getPower());
					break;
				}
			}
		}
		
		return;
	}

}
