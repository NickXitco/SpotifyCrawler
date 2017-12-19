package com.nxitco.maven.quickstart;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Genres {

   public static final String DATABASE_WRITE_NAME = "genres.txt";
   public static final String DATABASE_READ_NAME = "sortedHeadArtists.txt";
   
   public static void main( String[] args ) throws IOException
   {
       Reader reader = new Reader(DATABASE_READ_NAME);     
       Writer writer = new Writer(DATABASE_WRITE_NAME, false);
       String line;
       
       Set<String> genresList = new HashSet<String>();
       
       reader.resetFile();
       while ((line = reader.dbReader.readLine()) != null) {
           String[] data = line.split("\\|");
           String[] genres = data[3].split(", ");
           for (String s : genres) {
              if (!s.equals("n/a")) {
                 if (genresList.add(s) == true) {
                    writer.dbWriter.write(s + "\n");
                 }
              }
           }
       }
       reader.closeReader(); 
       writer.closeWriter();
   }


}
