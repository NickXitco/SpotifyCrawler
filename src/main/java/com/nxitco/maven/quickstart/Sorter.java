package com.nxitco.maven.quickstart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Sorter {
    
    public static final String DATABASE_WRITE_NAME = "fixedsortedartistDB.txt";
    public static final String DATABASE_READ_NAME = "sortedartistDB.txt";
    
    public static void main( String[] args ) throws IOException
    {
        Reader reader = new Reader(DATABASE_READ_NAME);
        
        List<String> lines = new ArrayList<String>();
        List<Integer> followers = new ArrayList<Integer>();
        
        String line;
        int i = 0;
        
        
        reader.resetFile();
        while ((line = reader.dbReader.readLine()) != null) {
            String[] data = line.split("\\|");
            followers.add(Integer.parseInt(data[2]));
            lines.add(line);
            i++;
            if (i % 100000 == 0) {
                System.out.println(i/100000 + " hundred thousand lines read");
            }
        }
        reader.closeReader();
        
        Writer writer = new Writer(DATABASE_WRITE_NAME, false);
        
        System.out.println(followers.size());
        System.out.println(lines.size());    
        
        i = 0;
        
        while (!followers.isEmpty()) {
            int max = Integer.MIN_VALUE;
            int maxIndex = -1;
            for (int j = 0; j < followers.size(); j++) {
                if (followers.get(j) > max) {
                    max = followers.get(j);
                    maxIndex = j;
                }
            }
            writer.dbWriter.write(lines.get(maxIndex));
            writer.startNewLine();
            lines.remove(maxIndex);
            followers.remove(maxIndex);
            if (++i % 1000 == 0) {
                System.out.println("Sorted " + i + " lines.");
            }
       }
       writer.closeWriter();
    }
}
