package org.roaringbitmap;

import java.util.ArrayList;
import java.util.Iterator;

public class dbinternals {

    public static void main(String[] args) {

        int numberOfConstrains = 5; // number of constraints in the query
        int threshold = 2; // threshold of the constraints
        RoaringBitmap[] conditionBitmaps = new RoaringBitmap[numberOfConstrains];  // array of bitmaps corresponds to constraints in the query
        int[] data = new int[threshold]; // A temporary array to store all combination one by on
        ArrayList<RoaringBitmap> combinedBitmaps = new ArrayList<>();  // bitmaps result from AND operations of combinations
        int maxCardinality = 15; // get the maximum cardinality of constraint bitmaps
        Iterator iterator; // Iterator object is require to do a OR operation between many bitmaps
        RoaringBitmap finalBitmap; // final bitmap after all operations

        // setup bitmaps
        conditionBitmaps[0] = RoaringBitmap.bitmapOf(1,2,3,4,5);
        conditionBitmaps[1] = RoaringBitmap.bitmapOf(3,4,5,6,7);
        conditionBitmaps[2] = RoaringBitmap.bitmapOf(5,6,7,8,9);
        conditionBitmaps[3] = RoaringBitmap.bitmapOf(7,8,9,10,11);
        conditionBitmaps[4] = RoaringBitmap.bitmapOf(9,10,11,12,13);

        // find bitmaps result from AND operations of combinations
        combinationUtility(conditionBitmaps, combinedBitmaps, data, 0, numberOfConstrains-1, 0, threshold, maxCardinality);

        // find the union of bitmaps result from AND operations of combinations
        iterator = combinedBitmaps.iterator();
        finalBitmap = RoaringBitmap.or(iterator, 1, maxCardinality);

        System.out.println("\nFinal Results");
        for(int i : finalBitmap) {
            System.out.print(i + ", ");
        }
    }

    static void combinationUtility(RoaringBitmap conditionBitmaps[], ArrayList<RoaringBitmap> combinedBitmaps, int data[], int start, int end, int index, int threshold, int maxCardinality) {

        ArrayList<RoaringBitmap> localCombination = new ArrayList<>();  // to hold the bitmaps of a particular combination
        Iterator itr; // Iterator object is required to do ALL operation between many bitmaps

        // Another combination is formed
        if (index == threshold) {
            System.out.println("\nCombination No : " + (combinedBitmaps.size() + 1));

            // add the bitmaps correspond to this combination into localCombination ArrayList
            for (int j=0; j<threshold; j++) {
                System.out.printf("%d ", data[j]);
                localCombination.add(conditionBitmaps[data[j]-1]);
            }
            System.out.println();

            // do ALL operation between bitmaps correspond to this combination and add the resulting bitmap to combinedBitmaps List
            itr = localCombination.iterator();
            combinedBitmaps.add(RoaringBitmap.and(itr, 1, maxCardinality));

            for(int i : combinedBitmaps.get(combinedBitmaps.size()-1)) {
                System.out.print(i + ", ");
            }
            System.out.println();
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
}
