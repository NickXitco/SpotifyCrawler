package com.nxitco.maven.quickstart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.wrapper.spotify.models.Artist;
import com.wrapper.spotify.models.Followers;

public class Reader {
	BufferedReader dbReader;
    Set<String> idSet;
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
		this.idSet = new HashSet<String>();
		this.artistQueue = new LinkedList<Artist>();
	}
	
	public Set<String> readin() throws IOException {
	    
		System.out.println("Reading in file...");
		
		while ((this.line = dbReader.readLine()) != null) {
		    
			//Split line by the data separator "|"
			String[] data = this.line.split("\\|");
			
			if (data[1].length() != 22) {
				System.out.println(data[0]);
        		System.out.println(data[1]);
				this.go = false;
			}
			
	        //Put all the checked (leftmost) IDs in the discovered map.
            if (this.idSet.add(data[1]) == false) {
                this.go = false;
                System.err.println("DUPLICATE " + data[1]);
            }
        }
		return this.idSet;
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
            		
            		if (this.idSet.add(data[i + 1]) == true) {
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
