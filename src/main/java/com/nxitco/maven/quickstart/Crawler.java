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
import com.wrapper.spotify.methods.RelatedArtistsRequest;
import com.wrapper.spotify.methods.authentication.ClientCredentialsGrantRequest;
import com.wrapper.spotify.models.Artist;
import com.wrapper.spotify.models.ClientCredentials;

/**
 * Hello world!
 *
 */
public class Crawler 
{
	public static final int DATABASE_SIZE = 5000;
	public static final String clientId = "5b00769425ca43019b6072c9fe842472";
	public static final String clientSecret = "cc91b1e3ee824e0a8e94d64b5c5201b3";
	private static final String DEFAULT_ID = "6eUKZXaKkcviH0Ku9w2n3V";
	private static final String DEFAULT_NAME = "Ed Sheeran";
	
	@SuppressWarnings("deprecation")
	public static void main( String[] args ) throws IOException, WebApiException, InterruptedException
	{
		final Api api = Api.builder()
		  .clientId(clientId)
		  .clientSecret(clientSecret)
		  .build();
		
		Writer writer = new Writer();
		Reader reader = new Reader();
		Map<String, String> idMap = new HashMap<String, String>();
		Queue<String> idQueue = new LinkedList<String>();
		String currentID;
		String currentArtist;
		int i = 0;
		
		reader.readin();
		reader.addDiscoveredArtists();
		reader.closeReader();
		idMap = reader.idMap;
		idQueue = reader.idQueue;
		
		if (idMap.isEmpty()) {
			idMap.put(DEFAULT_ID, DEFAULT_NAME);
			idQueue.add(DEFAULT_ID);
		}
		
		/*
		 * Inject artists here (but you probably shouldn't
		 * cause they'd be disconnected which will give you all
		 * sorts of problems later on).
		 * 
		 * idQueue.add(injectedArtist);
		 * 
		 */
		
		if (reader.go == false) {
			System.out.println("Error in readin");
			return;
		}
		System.out.println("Starting crawler...");
		while (i <= DATABASE_SIZE) {
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

			while (i <= DATABASE_SIZE) {
				
				if ((currentID = idQueue.poll()) == null) {
					System.out.println("Queue Empty.");
					break;
				}
				currentArtist = idMap.get(currentID);
				writer.writeHeadArtist(currentArtist, currentID);
				try {
					final RelatedArtistsRequest request = api.getArtistRelatedArtists(currentID).build();
					final List<Artist> artists = request.get();
				    for (Artist artist : artists) {
				    	if (idMap.put(artist.getId(), artist.getName()) == null) {
				    		idQueue.add(artist.getId());
				    	}
			    		writer.writeChildArtist(artist.getName(), artist.getId());
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
					System.out.println("Queue size: " + idQueue.size());
				}
				if (++i % 50 == 0) {
					System.out.println("Completed " + i
										+ " of " + DATABASE_SIZE);
				}

			}
		}
		System.out.println("Finished proccess.");
		writer.closeWriter();
    }
	
	
}
