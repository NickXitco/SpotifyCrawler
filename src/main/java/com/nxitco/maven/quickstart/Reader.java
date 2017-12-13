package com.nxitco.maven.quickstart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Reader {
	BufferedReader dbReader;
	Map<String, String> idMap;
	Queue<String> idQueue;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.idMap = new HashMap<String, String>();
		this.idQueue = new LinkedList<String>();
	}
	
	public Map<String, String> readin() throws IOException {
		System.out.println("Reading in file...");
		while ((line = dbReader.readLine()) != null) {
			//Split line by the data separator "|"
			String[] data = line.split("\\|");
			//Put all the checked (leftmost) artists in the discovered map
			if (data[1].length() != 22) {
				System.out.println(data[0]);
        		System.out.println(data[1]);
				this.go = false;
			}
            this.idMap.put(data[1], data[0]);
        }
		return this.idMap;
	}
	
	public void resetFile() throws IOException {
		this.dbReader = new BufferedReader(
				new FileReader(
						new File(this.databaseName)));
	}
	
	public Queue<String> addDiscoveredArtists() throws IOException  {
		System.out.println("Updating Maps...");
		this.resetFile();
		while ((this.line = this.dbReader.readLine()) != null) {
            String[] data = this.line.split("\\|");
            //Try to put the rest of the artists in the map and add them to the
            //Checking queue if they're not in the discovered map (aka, haven't been checked)
            for (int i = 2; i < data.length; i += 2) {
            	try {
        			if (i > 40) {
        				System.err.println("line over 40");
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
        			
            		if (this.idMap.put(data[i+1], data[i]) == null) {
	            		this.idQueue.add(data[i+1]);
	            	}
	            	
            	} catch (ArrayIndexOutOfBoundsException e) {
    				System.err.println("Out of bounds");
            		System.out.println(data[0]);
            		System.out.println(data[1]);
            		this.go = false;
            	}
            }   
        }
		return this.idQueue;
	}
	
	public void closeReader() throws IOException {
		this.dbReader.close();
	}	
}
