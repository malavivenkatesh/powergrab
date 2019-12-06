package uk.ac.ed.inf.powergrab;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class Logging {
	// BufferedWriter used to write to the txt file line by line
	protected static BufferedWriter bw;
	/**
	 * Logs a geojson file showing the drone's flight path
	 * @param featureList - list of features from the map
	 * @param dronePathTrace - drone's movements
	 * @param year
	 * @param month
	 * @param day
	 * @param state - stateful or stateless
	 */
	public static void logToGJson(List<Feature> featureList, List<Position> dronePathTrace, 
			String year, String month, String day, String state) {
		
		String filename = String.join("-", state, day, month, year) + ".geojson";
		
		// Adding drone's path to the feature list for required log file
		// Converting Positions to Points
		List<Point> dronePathPoints = new ArrayList<>();
		dronePathTrace.forEach(x -> dronePathPoints.add(
				Point.fromLngLat(x.getLongitude(), x.getLatitude())));
		LineString lineTrace = LineString.fromLngLats(dronePathPoints);
		Feature lineTraceFeature = Feature.fromGeometry(lineTrace);
		featureList.add(lineTraceFeature);
    	FeatureCollection featureCol = FeatureCollection.fromFeatures(featureList);
        String jsonString = featureCol.toJson();
        String filePath = String.join(File.separator, ".", filename);
		
        try {
        	FileWriter writer = new FileWriter(filePath);
            writer.write(jsonString);
            writer.close();
        } catch (IOException e) {
            System.out.println("exception " + e.getMessage());
            e.printStackTrace();
        }
	}
	
	/**
	 * Initialises the BufferedWriter
	 * @param year
	 * @param month
	 * @param day
	 * @param state
	 * @throws IOException - if there's been an error creating the file writer
	 */
	public static void setWriter(String year, String month, String day, String state){
		String filename = String.join("-", state, day, month, year) + ".txt";
        String filePath = String.join(File.separator, ".", filename);
		
        File f = new File(filePath);
        f.delete();
        
		FileWriter fw;
		try {
			fw = new FileWriter(f, true);
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Writes one line to the txt log file with the drone's relevant information
	 * @param curPos
	 * @param nextPos
	 * @param dir
	 * @param coins
	 * @param power
	 */
	public static void logToTxt(Position curPos, Position nextPos, Direction dir, double coins, double power) {
		String info = String.join(",", Double.toString(curPos.getLatitude()),
				 		Double.toString(curPos.getLongitude()),
				 		dir.toString(),
				 		Double.toString(nextPos.getLatitude()),
				 		Double.toString(nextPos.getLongitude()),
				 		Double.toString(coins),
				 		Double.toString(power)
						);
		try {
			bw.write(info);
			System.out.print(info);
			System.out.println();
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
