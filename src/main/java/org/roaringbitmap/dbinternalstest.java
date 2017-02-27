package org.roaringbitmap;

import java.util.ArrayList;

/**
 * Created by thilina on 2/27/17.
 */
public class dbinternalstest {

    public static void main(String[] args) {

        int arr[] = {1, 2, 3, 4, 5};
        int r = 2;
        int n = arr.length;

        // A temporary array to store all combination one by one
        int[] data = new int[r];

        // Print all combination using temprary array 'data[]'
        combinationUtil(arr, data, 0, n-1, 0, r);
    }

    static void combinationUtil(int arr[], int data[], int start, int end,
                         int index, int r)
    {
        // Current combination is ready to be printed, print it
        if (index == r)
        {
            for (int j=0; j<r; j++)
                System.out.print(data[j] + ", ");
            System.out.print("\n");
            return;
        }

        // replace index with all possible elements. The condition
        // "end-i+1 >= r-index" makes sure that including one element
        // at index will make a combination with remaining elements
        // at remaining positions
        for (int i=start; i<=end && end-i+1 >= r-index; i++)
        {
            data[index] = i+1;
            combinationUtil(arr, data, i+1, end, index+1, r);
        }
    }
}
