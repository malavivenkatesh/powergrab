package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import com.mapbox.geojson.FeatureCollection;

public class Map {
	
	public static void main( String[] args ) {
		String urlPath = "http://homepages.inf.ed.ac.uk/stg/powergrab/2019/01/01/powergrabmap.geojson";
		try {
			URL mapUrl = new URL(urlPath);
			HttpURLConnection conn = (HttpURLConnection) mapUrl.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			
			InputStream inputStream = conn.getInputStream();
			
		    String text = "";
		    Scanner scanner = new Scanner(inputStream);
		    
		    while (scanner.hasNext()) {
		    	text += scanner.next();
		    }
		    scanner.close();
		    conn.disconnect();
		    
		    FeatureCollection ft = FeatureCollection.fromJson(text);
		    
		    List feat = ft.features();
		    System.out.print(feat.get(0));
		    
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
				
	}
}
