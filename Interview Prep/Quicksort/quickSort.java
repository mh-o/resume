import java.util.*;
import java.io.*;

/*
 * This class accepts a string of integers in the form of:
 *  -> 1 2 3 4 5 ...
 * and returns a String of the sorted array using the QuickSort algorithm.
 */
public class quickSort {
  static Scanner in;

  public static void main(String[] args) {
    in = new Scanner(new BufferedReader(new InputStreamReader(System.in)));

    // Initialize the array.
    String[] vals = in.nextLine().split(" ");
    int[] arr = new int[vals.length];

    for (int i = 0; i < vals.length; i++) {
      arr[i] = Integer.parseInt(vals[i]);
    }

    // Sort the array.
    qSort(arr, 0, (arr.length - 1));

    System.out.print(Arrays.toString(arr));
  }

  /*
   *
   */
  public static void qSort(int[] arr, int start, int end) {
    int partition = partition(arr, start, end);

    // If one less than partition (new start) is greater than the old start,
    // sort again using start = new start and end = old start.
    if ((partition - 1) > start) {
      qSort(arr, start, (partition - 1));
    }

    // If one more than partition (new start) is less than the end, sort again
    // using start = (old start + 1) and end.
    if ((partition + 1) < end) {
      qSort(arr, (partition + 1), end);
    }
  }

  public static int partition(int[] arr, int start, int end) {
    int pivot = arr[end];

    // For each element in the array, if the element is less than the pivot,
    // swap it with the value at the start position, then incriment the start
    // position.
    for (int i = start; i < end; i++) {
      if (arr[i] < pivot) {
        int temp = arr[start];
        arr[start] = arr[i];
        arr[i] = temp;

        start++;
      }
    }

    // After looping from start to end, swap the start and pivot.
    int temp = arr[start];
    arr[start] = pivot;
    arr[end] = temp;

    return start;
  }
}
