package org.roaringbitmap;

import au.com.bytecode.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class dbinternals {

    public static void main(String[] args) {

        HashMap<String, HashMap> database = setUpBitmaps(); // get bitmaps created by column values
        int numberOfConstrains = 5; // number of constraints in the query
        int threshold = 3; // threshold of the constraints
        RoaringBitmap[] conditionBitmaps = new RoaringBitmap[numberOfConstrains];  // array of bitmaps corresponds to constraints in the query
        int[] data = new int[8]; // A temporary array to store all combination one by on
        ArrayList<RoaringBitmap> combinedBitmaps = new ArrayList<>();  // bitmaps result from AND operations of combinations
        int maxCardinality = 0; // get the maximum cardinality of constraint bitmaps
        Iterator iterator; // Iterator object is require to do a OR operation between many bitmaps
        RoaringBitmap finalBitmap; // final bitmap after all operations
        long startTime; // variables for time calculations
        long duration;

        // columns in query
        String[] columns = { "c_preferred_cust_flag", "c_birth_country", "c_birth_month", "c_birth_day", "c_salutation"};

        // query params
        String[] params = { "Y", "SPAIN", "2", "3", "Miss"};

        // columns in query
        //String[] columns = { "c_preferred_cust_flag", "c_birth_country", "c_birth_month", "c_birth_day", "c_salutation", "c_birth_yea", "c_first_name", "c_first_sales_date_sk"};

        // query params
        //String[] params = { "Y", "SPAIN", "2", "3", "Miss", "1930", "John", "2451092"};

        // retrieve necessary bitmaps
        for (int i=0; i<numberOfConstrains; i++){
            conditionBitmaps[i] = (RoaringBitmap) database.get(columns[i]).get(params[i]);
            if(maxCardinality < conditionBitmaps[i].getLongCardinality())
                maxCardinality = (int) conditionBitmaps[i].getLongCardinality();
        }

        Scanner s = new Scanner(System.in);
        while (true){
            System.out.print("Enter the threshold size : ");
            threshold = s.nextInt();
            startTime = System.nanoTime();
            // find bitmaps result from AND operations of combinations
            combinationUtility(conditionBitmaps, combinedBitmaps, data, 0, numberOfConstrains-1, 0, threshold, maxCardinality);
            duration = System.nanoTime() - startTime;

            // find the union of bitmaps result from AND operations of combinations
            iterator = combinedBitmaps.iterator();
            finalBitmap = RoaringBitmap.or(iterator, 1, maxCardinality);

            System.out.println("\nFinal Results");
            for(int i : finalBitmap) {
                System.out.print(i + ", ");
            }

            System.out.println("\n\nExecution Time in ms : " + duration/1000000);
        }

        /*startTime = System.nanoTime();
        // find bitmaps result from AND operations of combinations
        combinationUtility(conditionBitmaps, combinedBitmaps, data, 0, numberOfConstrains-1, 0, threshold, maxCardinality);
        duration = System.nanoTime() - startTime;

        // find the union of bitmaps result from AND operations of combinations
        iterator = combinedBitmaps.iterator();
        finalBitmap = RoaringBitmap.or(iterator, 1, maxCardinality);

        System.out.println("\nFinal Results");
        for(int i : finalBitmap) {
            System.out.print(i + ", ");
        }

        System.out.println("\n\nExecution Time in ms : " + duration/1000000);*/
    }

    static void combinationUtility(RoaringBitmap conditionBitmaps[], ArrayList<RoaringBitmap> combinedBitmaps, int data[], int start, int end, int index, int threshold, int maxCardinality) {

        ArrayList<RoaringBitmap> localCombination = new ArrayList<>();  // to hold the bitmaps of a particular combination
        Iterator itr; // Iterator object is required to do ALL operation between many bitmaps

        // Another combination is formed
        if (index == threshold) {
            //System.out.println("\nCombination No : " + (combinedBitmaps.size() + 1));

            // add the bitmaps correspond to this combination into localCombination ArrayList
            for (int j=0; j<threshold; j++) {
                //System.out.printf("%d ", data[j]);
                localCombination.add(conditionBitmaps[data[j]-1]);
            }
            //System.out.println();

            // do ALL operation between bitmaps correspond to this combination and add the resulting bitmap to combinedBitmaps List
            itr = localCombination.iterator();
            combinedBitmaps.add(RoaringBitmap.and(itr, 1, maxCardinality));

            for(int i : combinedBitmaps.get(combinedBitmaps.size()-1)) {
                //System.out.print(i + ", ");
            }
            //System.out.println();
            return;
        }

        /*replace index with all possible elements. The condition
         "end-i+1 >= r-index" makes sure that including one element
         at index will make a combination with remaining elements
         at remaining positions*/
        for (int i=start; i<=end && end-i+1 >= threshold-index; i++) {
            data[index] = i+1;
            combinationUtility(conditionBitmaps, combinedBitmaps, data, i+1, end, index+1, threshold, maxCardinality);
        }
    }

    static HashMap<String, HashMap> setUpBitmaps(){
        String[] column_names = {"c_customer_sk", "c_customer_id", "c_current_cdemo_sk", "c_current_hdemo_sk", "c_current_addr_sk", "c_first_shipto_date_sk", "c_first_sales_date_sk", "c_salutation", "c_first_name", "c_last_name", "c_preferred_cust_flag", "c_birth_day", "c_birth_month", "c_birth_year", "c_birth_country"};
        HashMap<String, HashMap> database = new HashMap<>();
        HashMap<String,RoaringBitmap> attribute_bitmap = new HashMap<>();
        attribute_bitmap.clear();
        ArrayList<String> column = new ArrayList<>();
        for(int column_count = 0;column_count<column_names.length;column_count++)
        {
            try {
                //csv file containing data
                String strFile = "/home/thilina/Desktop/SEMESTER - 8/DB Internals/customer.csv";
                CSVReader reader = new CSVReader(new FileReader(strFile));
                String [] nextLine;
                int lineNumber = 0;
                while ((nextLine = reader.readNext()) != null) {
                    column.add(nextLine[column_count]);
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
        //System.out.println(database);
        return database;
    }
}
