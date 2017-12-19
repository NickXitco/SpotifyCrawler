package com.nxitco.maven.quickstart;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.wrapper.spotify.models.Artist;

public class Writer {
	BufferedWriter dbWriter;
	String line;
	
	public Writer(String fileName, boolean append) {
		try {
			this.dbWriter = new BufferedWriter(new FileWriter(fileName, append));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeHeadArtist(Artist artist) throws IOException {
		this.dbWriter.write(stringPrep(artist));
	}
	
	public void writeChildArtist(Artist artist) throws IOException {
		this.dbWriter.append("|" + stringPrep(artist));
	}
	
	public void startNewLine() throws IOException {
		this.dbWriter.append("\n");
	}
	
	public void flushWriter() throws IOException {
		this.dbWriter.flush();
	}
	
	public void closeWriter() throws IOException {
		this.dbWriter.close();
	}
	
	public void errorMessage() throws IOException {
		this.dbWriter.append("//POSSIBLE ERROR//");
	}
	
	public String stringPrep(Artist artist) {
		String name = artist.getName();
		String id = artist.getId();
		int followers = artist.getFollowers().getTotal();
		String genres = genreListStringFix(artist.getGenres());
		
		if (name.contains("|")) {
			name = name.replace("|", "/");
		}
		
		return name + "|" + id + "|" + followers + "|" + genres;
	}
	
	private String genreListStringFix(List<String> genres) {
	    if (genres.isEmpty() || genres == null) {
	        return "n/a";
	    }
	    //Returns a substring without the 1st and last characters (brackets)
	    return genres.toString().substring(1, genres.toString().length() - 1);
    }
}
