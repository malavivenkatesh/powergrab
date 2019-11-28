package uk.ac.ed.inf.powergrab;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class Logging {
	
	protected static BufferedWriter bw;
	
	public static void logToGJson(ArrayList<Feature> featureList, ArrayList<Point> dronePathTrace, 
			String year, String month, String day, String state) {
		
		String filename = String.join("-", state, day, month, year) + ".geojson";
		
		new File("logs").mkdir();
		
		// Adding drone's path to the feature list for required log file
		LineString lineTrace = LineString.fromLngLats(dronePathTrace);
		Feature lineTraceFeature = Feature.fromGeometry(lineTrace);
		featureList.add(lineTraceFeature);
		
        try {
        	FeatureCollection featureCol = FeatureCollection.fromFeatures(featureList);
            String jsonString = featureCol.toJson();
            String filePath = String.join(File.separator, ".", "logs", filename);
            FileWriter writer = new FileWriter(filePath);
            writer.write(jsonString);
            writer.close();
        } catch (IOException e) {
            System.out.println("exception " + e.getMessage());
            e.printStackTrace();
        }
	}
	
	public static void setWriter(String year, String month, String day, String state) throws IOException {
		String filename = String.join("-", state, day, month, year) + ".txt";
		new File("logs").mkdir();
        String filePath = String.join(File.separator, ".", "logs", filename);
		
        File f = new File(filePath);
        f.delete();
        
		FileWriter fw = new FileWriter(f, true);
		bw = new BufferedWriter(fw);
	}
	
	public static void logToTxt(Point curPos, Point nextPos, Direction dir, double coins, double power) {
		String info = String.join(",", Double.toString(curPos.latitude()),
				 		Double.toString(curPos.longitude()),
				 		dir.toString(),
				 		Double.toString(nextPos.latitude()),
				 		Double.toString(nextPos.longitude()),
				 		Double.toString(coins),
				 		Double.toString(power)
						);
		try {
			bw.write(info);
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
