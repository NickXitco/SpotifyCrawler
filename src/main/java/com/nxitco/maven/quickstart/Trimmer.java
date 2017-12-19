package com.nxitco.maven.quickstart;

import java.io.IOException;

public class Trimmer {

    public static final String DATABASE_WRITE_NAME = "sortedHeadArtists.txt";
    public static final String DATABASE_READ_NAME = "sortedartistDB.txt";
    
    public static void main( String[] args ) throws IOException
    {
        Reader reader = new Reader(DATABASE_READ_NAME);     
        Writer writer = new Writer(DATABASE_WRITE_NAME, false);
        String line;
        
        reader.resetFile();
        while ((line = reader.dbReader.readLine()) != null) {
            String[] data = line.split("\\|");
            writer.dbWriter.write(data[0] + "|" + data[1] + "|" + data[2] + "|" + data[3] + "\n");
        }
        reader.closeReader(); 
        writer.closeWriter();
    }

}
