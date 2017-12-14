package com.nxitco.maven.quickstart;

import java.io.IOException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;

import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.ArtistRequest;
import com.wrapper.spotify.methods.RelatedArtistsRequest;
import com.wrapper.spotify.methods.authentication.ClientCredentialsGrantRequest;
import com.wrapper.spotify.models.Artist;
import com.wrapper.spotify.models.ClientCredentials;

/**
 * Spotify Artist Crawler.
 */
public class Crawler 
{
	public static final int LINES_TO_ADD = 5000;
	public static final String clientId = "5b00769425ca43019b6072c9fe842472";
	public static final String clientSecret = "cc91b1e3ee824e0a8e94d64b5c5201b3";
	private static final String DEFAULT_ID = "6eUKZXaKkcviH0Ku9w2n3V"; //Ed Sheeran
	
	public static final String DATABASE_WRITE_NAME = "artistDB.txt";
	public static final String DATABASE_READ_NAME = "artistDB.txt";
	
	public static final int SLEEP_DELAY_TIME = 50;
	
	public static void main( String[] args ) throws IOException, WebApiException, InterruptedException
	{
		final Api api = Api.builder()
		  .clientId(clientId)
		  .clientSecret(clientSecret)
		  .build();
		
		Writer writer = new Writer(DATABASE_WRITE_NAME);
		Reader reader = new Reader(DATABASE_READ_NAME);
		
		
		Map<String, Artist> artistMap = new HashMap<String, Artist>(); //Map of Artist IDs against Artist Structures
		Queue<Artist> artistQueue = new LinkedList<Artist>(); //Queue of Artist Structures
		
		Artist currentArtist;
	    String currentID = DEFAULT_ID;		
		int i = 0;
		
		reader.readin();
		reader.addDiscoveredArtists();
		artistMap = reader.artistMap;
		artistQueue = reader.artistQueue;
		
		if (artistMap.isEmpty()) {
	        getAccessToken(api);
	        final ArtistRequest request = api.getArtist(DEFAULT_ID).build();
	        currentArtist = request.get();
	        artistMap.put(currentID, currentArtist);
		    artistQueue.add(currentArtist);
		}
		
		/*
		 * Inject artists here (but you probably shouldn't
		 * cause they'd be disconnected which will give you all
		 * sorts of problems later on).
		 * 
		 * idQueue.add(injectedArtist);
		 * 
		 */
		
		//TODO Set the lines to add to be the number of unique artists in the Queue defined by the readin.
		//These artists will, by their very nature, all be distinct and unique.
		//Therefore, you only ever have to update the queue during readin, and then run the queue out.
		//Think a bit more about this but I think it's a huge step forward if it's correct.
		
		
		if (reader.go == false) {
			System.out.println("Error in readin");
			return;
		}
		
		System.out.println("Starting crawler...");
		while (i < LINES_TO_ADD) {
			getAccessToken(api);
		    while (i < LINES_TO_ADD) {
				
		        currentArtist = artistQueue.poll();
				if (currentArtist == null) {
					System.out.println("Queue Empty.");
					return;
				}				
				
				writer.writeHeadArtist(currentArtist);
				
				try {
				    
					final RelatedArtistsRequest request = api.getArtistRelatedArtists(currentArtist.getId()).build();
					final List<Artist> artists = request.get();
					
				    for (Artist newArtist : artists) {
				        if (artistMap.put(newArtist.getId(), newArtist) == null) {
				    		artistQueue.add(newArtist);
				    	}
			    		writer.writeChildArtist(newArtist);
				    }
				    writer.startNewLine();		
				    
				} catch (Exception e) {
				    
					writer.errorMessage();
					writer.flushWriter();
					
					if (e.getMessage().equals("429")) {
						System.err.println("Too many requests! " + e.getMessage());
						System.err.println("Sleeping for 5 minutes.");
						Thread.sleep(300000);
						System.err.println("Resuming...");
					} else if (e.getMessage().equals("401")) {
						System.err.println("Access Token Expired! " + e.getMessage());
					} else {
						System.err.println("Unknown Exception: " + e.getMessage());
					}
					
					i++;
					break;
				}
				
				if (i % 500 == 0) {
					System.out.println("Current Queue Size: " + artistQueue.size());
				}
				
				if (i % 25 == 0) {
					System.out.println("Completed " + i
										+ " of " + LINES_TO_ADD);
				}
                Thread.sleep(SLEEP_DELAY_TIME);
				i++;

			}
		}
		System.out.println("Finished proccess.");
		writer.closeWriter();
		reader.closeReader();
    }	
	
	@SuppressWarnings("deprecation")
	private static void getAccessToken(final Api api) {
        System.err.println("Requesting new access token...");
        ClientCredentialsGrantRequest credRequest = api.clientCredentialsGrant().build();

        SettableFuture<ClientCredentials> responseFuture = credRequest.getAsync();
        
        Futures.addCallback(responseFuture, new FutureCallback<ClientCredentials>() {
          public void onSuccess(ClientCredentials clientCredentials) {  
            api.setAccessToken(clientCredentials.getAccessToken());
            System.err.println("Access token granted!");
          }
          public void onFailure(Throwable throwable) {
              System.err.println("Access token denied.");
          }
        });
	}
}
