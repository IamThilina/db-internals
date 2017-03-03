package org.roaringbitmap;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by thilina on 3/3/17.
 */
public class loopedAlgorithm {

    public static void main(String[] args) {

        HashMap<String, HashMap> database = setUpBitmaps(); // get bitmaps created by column values
        int numberOfConstrains = 5; // number of constraints in the query
        int threshold = 3; // threshold of the constraints
        RoaringBitmap[] conditionBitmaps = new RoaringBitmap[numberOfConstrains];  // array of bitmaps corresponds to constraints in the query
        int[] data = new int[threshold]; // A temporary array to store all combination one by on
        ArrayList<RoaringBitmap> combinedBitmaps = new ArrayList<>();  // bitmaps result from AND operations of combinations
        int maxCardinality = 0; // get the maximum cardinality of constraint bitmaps
        Iterator iterator; // Iterator object is require to do a OR operation between many bitmaps
        RoaringBitmap finalBitmap; // final bitmap after all operations
        final long startTime, duration; // variables for time calculations

        // columns in query
        String[] columns = { "city", "age", "department", "salary", "sex"};

        // query params
        String[] params = { "colombo", "25", "cse", "50000", "M"};

        // retrieve necessary bitmaps
        for (int i=0; i<numberOfConstrains; i++){
            conditionBitmaps[i] = (RoaringBitmap) database.get(columns[i]).get(params[i]);
            if(maxCardinality < conditionBitmaps[i].getLongCardinality())
                maxCardinality = (int) conditionBitmaps[i].getLongCardinality();
        }

        // initialize combinedBitmaps
        for (int i = 0; i<threshold; i++) {
            combinedBitmaps.add(new RoaringBitmap());
        }

        startTime = System.nanoTime();
        // find rows satisfying minimum number of constraints
        finalBitmap = findRowIDs(conditionBitmaps, combinedBitmaps);
        duration = System.nanoTime() - startTime;

        //System.out.println("\nFinal Results");
        for(int i : finalBitmap) {
            System.out.print(i + ", ");
        }

        System.out.println("\n\nExecution Time in ms : " + duration/1000);
    }

    static RoaringBitmap findRowIDs(RoaringBitmap conditionBitmaps[], ArrayList<RoaringBitmap> combinedBitmaps){

        int N = conditionBitmaps.length;
        int threshold = combinedBitmaps.size();
        int min = threshold;
        RoaringBitmap temp;
        combinedBitmaps.set(0, conditionBitmaps[0]);

        for (int i=2; i<N; i++){
            if(i < threshold)
                min = i;
            for (int j=min; j>1; j--){
                temp = RoaringBitmap.and(combinedBitmaps.get(j-2), conditionBitmaps[i-1]);
                combinedBitmaps.get(j-1).or(temp);
            }
            min = threshold;
            combinedBitmaps.get(0).or(conditionBitmaps[i-1]);
        }

        return combinedBitmaps.get(threshold-1);
    }

    static HashMap<String, HashMap> setUpBitmaps(){
        String[] column_names = {"age","city","salary","department","sex"};
        HashMap<String,HashMap> database = new HashMap<>();

        for(int column_count = 0;column_count<column_names.length;column_count++)
        {
            HashMap<String,RoaringBitmap> attribute_bitmap = new HashMap<>();
            attribute_bitmap.clear();
            ArrayList<String> column = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader("src/"+column_names[column_count]+".txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    column.add(line);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set result = new HashSet(column);
            for (Object s : result) {
                ArrayList<Integer> exist_ids = new ArrayList<>();
                for(int i=0;i<column.size();i++)
                {
                    if(s.toString().equals(column.get(i)))
                    {
                        exist_ids.add(i+1);
                    }
                }
                RoaringBitmap rr = RoaringBitmap.bitmapOf(exist_ids);
                exist_ids.clear();
                attribute_bitmap.put(s.toString(),rr);
            }
            database.put(column_names[column_count], attribute_bitmap);
            column.clear();
        }
        return database;
    }

}
