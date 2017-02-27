package org.roaringbitmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class dbinternals {

    public static void main(String[] args) {

        int numberOfConstrains = 5;
        int threshold = 2;
        RoaringBitmap[] conditionBitmaps = new RoaringBitmap[numberOfConstrains];
        // A temporary array to store all combination one by one
        int[] data = new int[threshold];
        ArrayList<RoaringBitmap> combinedBitmaps = new ArrayList<>();

        // setup bitmaps
        conditionBitmaps[0] = RoaringBitmap.bitmapOf(1,2,3,4,5);
        conditionBitmaps[1] = RoaringBitmap.bitmapOf(3,4,5,6,7);
        conditionBitmaps[2] = RoaringBitmap.bitmapOf(5,6,7,8,9);
        conditionBitmaps[3] = RoaringBitmap.bitmapOf(7,8,9,10,11);
        conditionBitmaps[4] = RoaringBitmap.bitmapOf(9,10,11,12,13);

        // Print all combination using temprary array 'data[]'
        combinationUtil(conditionBitmaps, combinedBitmaps, data, 0, numberOfConstrains-1, 0, threshold);

        Iterator itr = combinedBitmaps.iterator();
        RoaringBitmap temp = RoaringBitmap.or(itr, 1, 20);

        System.out.println("\nFinal Results");
        for(int i : temp) {
            System.out.print(i + ", ");
        }
    }

    /* arr[]  ---> Input Array
    data[] ---> Temporary array to store current combination
    start & end ---> Staring and Ending indexes in arr[]
    index  ---> Current index in data[]
    r ---> Size of a combination to be printed */
    static void combinationUtil(RoaringBitmap conditionBitmaps[], ArrayList<RoaringBitmap> combinedBitmaps, int data[], int start, int end, int index, int r) {
        // Current combination is ready to be printed, print it
        ArrayList<RoaringBitmap> localCombination = new ArrayList<>();
        Iterator itr;
        if (index == r) {
            System.out.println("\nCombination No : " + (combinedBitmaps.size() + 1));
            for (int j=0; j<r; j++) {
                System.out.printf("%d ", data[j]);
                localCombination.add(conditionBitmaps[data[j]-1]);
            }
            System.out.println();
            itr = localCombination.iterator();
            combinedBitmaps.add(RoaringBitmap.and(itr, 1, 20));
            // System.out.println("Combination No : " + combinedBitmaps.size());
            for(int i : combinedBitmaps.get(combinedBitmaps.size()-1)) {
                System.out.print(i + ", ");
            }
            System.out.println();
            return;
        }

        // replace index with all possible elements. The condition
        // "end-i+1 >= r-index" makes sure that including one element
        // at index will make a combination with remaining elements
        // at remaining positions
        for (int i=start; i<=end && end-i+1 >= r-index; i++) {
            data[index] = i+1;
            combinationUtil(conditionBitmaps, combinedBitmaps, data, i+1, end, index+1, r);
        }
    }
}
