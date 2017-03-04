package org.roaringbitmap;

import au.com.bytecode.opencsv.CSVReader;

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
        int numberOfConstrains = 8; // number of constraints in the query
        int threshold = 3; // threshold of the constraints
        RoaringBitmap[] conditionBitmaps = new RoaringBitmap[numberOfConstrains];  // array of bitmaps corresponds to constraints in the query
        ArrayList<RoaringBitmap> combinedBitmaps = new ArrayList<>();  // bitmaps result from AND operations of combinations
        int maxCardinality = 0; // get the maximum cardinality of constraint bitmaps
        RoaringBitmap finalBitmap; // final bitmap after all operations
        long startTime; // variables for time calculations
        long duration;

        // columns in query
        String[] columns = { "c_preferred_cust_flag", "c_birth_country", "c_birth_month", "c_birth_day", "c_salutation", "c_birth_yea", "c_first_name", "c_first_sales_date_sk"};

        // query params
        String[] params = { "Y", "SPAIN", "2", "3", "Miss", "1930", "John", "2451092"};

        // retrieve necessary bitmaps
        for (int i=0; i<numberOfConstrains; i++){
            conditionBitmaps[i] = (RoaringBitmap) database.get(columns[i]).get(params[i]);
            if(maxCardinality < conditionBitmaps[i].getLongCardinality())
                maxCardinality = (int) conditionBitmaps[i].getLongCardinality();
        }

        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.print("Enter the threshold size : ");
            threshold = s.nextInt();
            // initialize combinedBitmaps
            for (int i = 0; i < threshold; i++) {
                combinedBitmaps.add(new RoaringBitmap());
            }

            startTime = System.nanoTime();
            // find rows satisfying minimum number of constraints
            finalBitmap = findRowIDs(conditionBitmaps, combinedBitmaps);
            duration = System.nanoTime() - startTime;

            //System.out.println("\nFinal Results");
            for (int i : finalBitmap) {
                System.out.print(i + ", ");
            }

            System.out.println("\n\nExecution Time in ms : " + duration / 1000000);
            combinedBitmaps.clear();
        }
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
