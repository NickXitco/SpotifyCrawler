package com.nxitco.maven.quickstart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.wrapper.spotify.models.Artist;
import com.wrapper.spotify.models.Followers;

public class Reader {
	BufferedReader dbReader;
    Map<String, Artist> artistMap;
    Queue<Artist> artistQueue;
	String databaseName;
	boolean go = true;
	String line;
	
	public Reader(String databaseReadName) {
		try {
			this.dbReader = new BufferedReader(
					new FileReader(
							new File(databaseReadName)));
			this.databaseName = databaseReadName;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.artistMap = new HashMap<String, Artist>();
		this.artistQueue = new LinkedList<Artist>();
	}
	
	public Map<String, Artist> readin() throws IOException {
	    
		System.out.println("Reading in file...");
		
		while ((this.line = dbReader.readLine()) != null) {
		    
			//Split line by the data separator "|"
			String[] data = this.line.split("\\|");
			
			Artist readArtist = new Artist();
			if (data[1].length() != 22) {
				System.out.println(data[0]);
        		System.out.println(data[1]);
				this.go = false;
			}
			readArtist.setName(data[0]);
	        readArtist.setId(data[1]);
	        readArtist.setFollowers(new Followers());
	        readArtist.getFollowers().setTotal(Integer.parseInt(data[2]));
	        readArtist.setGenres(genresFormat(data[3]));
			
	         //Put all the checked (leftmost) artists in the discovered map.
            this.artistMap.put(data[1], readArtist);
        }
		return this.artistMap;
	}
	
	private List<String> genresFormat(String string) {
        String[] data = string.split(", ");
        List<String> list = new ArrayList<String>();
        for (String s : data) {
            list.add(s);
        }
        return list;
    }

    public void resetFile() throws IOException {
		this.dbReader = new BufferedReader(
				new FileReader(
						new File(this.databaseName)));
	}
	
	public Queue<Artist> addDiscoveredArtists() throws IOException  {
		System.out.println("Updating Maps...");
		this.resetFile();
		while ((this.line = this.dbReader.readLine()) != null) {
            String[] data = this.line.split("\\|");
            //Try to put the rest of the artists in the map and add them to the
            //Checking queue if they're not in the discovered map (aka, haven't been checked)
            for (int i = 4; i < data.length; i += 4) {
            	try {
        			if (i > 80) {
        				System.err.println("line over 80");
        				System.out.println(data[0]);
                		System.out.println(data[1]);
                		this.go = false;
        			}
            		
            		if (data[i+1].length() != 22) {
        				System.err.println("Id issue");
        				System.out.println(data[0]);
                		System.out.println(data[1]);
        				this.go = false;
        			}
        			
            		Artist discoveredArtist = new Artist();
            		discoveredArtist.setName(data[i]);
            		discoveredArtist.setId(data[i + 1]);
            		discoveredArtist.setFollowers(new Followers());
            		discoveredArtist.getFollowers().setTotal(Integer.parseInt(data[i + 2]));
                    discoveredArtist.setGenres(genresFormat(data[i + 3]));     		
            		
            		if (this.artistMap.put(data[i + 1], discoveredArtist) == null) {
	            		this.artistQueue.add(discoveredArtist);
	            	}
	            	
            	} catch (ArrayIndexOutOfBoundsException e) {
    				System.err.println("Out of bounds");
            		System.out.println(data[0]);
            		System.out.println(data[1]);
            		this.go = false;
            	}
            }   
        }
		return this.artistQueue;
	}
	
	public void closeReader() throws IOException {
		this.dbReader.close();
	}	
}
