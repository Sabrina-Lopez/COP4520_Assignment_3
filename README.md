# COP4520-Assignment-3

## Problem 1: The Birthday Presents Party

### How to run:

Navigate to the directory where the program files are located and run the following commands in the terminal:

```
javac p1.java
java p1
```

### Explanation and Experimental Evaluation:

This approach uses the Lock free list implemented. The chain of presents to tag and write thank-you cards for is represented by a lock-free list, and each of the 4 servants is represented by a thread. The unordered bag of gifts is represented by a shuffled arraylist. The amount of presents added and removed from the chain in atomic integers is stored, and, while there are more presents that need to be tracked, each servant takes presents from the unordered bag and adds them to the chain. When a present is removed from the chain, a thank-you card is written with it.

The program takes about 6 ms for 50 presents, 11 ms for 500 presents, 91 ms for 5000 presents, 6986 ms for 5000 presents, and 1221811 ms to run for the 500000 presents specified. Additionally, data racing is used as each thread is constantly taking presents from the unordered bag and adding them to the chain. The program runs until all presents have thank-you cards written for them, which is guaranteed by the lock-free list approach.

If the servants skipped over some presents while they removed presents from the chain, they could have had more presents than thank-you notes. This is because thank-you notes are written when presents are removed. The solution to this problem is by running the program strictly until the specified number of presents have been taken from the chain via using atomic integers.

## Problem 2: Atmospheric Temperature Reading Module

### How to run:

Navigate to the directory where the program files are located and run the following commands in the terminal:

```
javac p2.java
java.p2
```

### Explanation and Experimental Evaluation:

With there being a list of integers containing the temperatures recorded by each sensor, the temperatures recorded by each of the 8 sensors is stored in an arraylist of arraylists. Extending thread, the rover class collects a temperature in the range -100F to 70F each minute and stores each value in the list. This runs concurrently for each of the 8 sensors. After each hour, a report is created, containing the top 5 highest temperatures from that hour, the top 5 lowest temperatures from that hour, and the 10-minute interval with the greatest temperature difference. By dividing the temperatures recorded into separate lists, the intervals are created.

For 3 hours, this program takes about 22804 ms. The threads run concurrently with each individual list of temperatures being added to the greater arraylist. Temperatures are constantly being taken each hour. The Arrays.sort method used in generating the report has an O(nlogn) runtime and Arraylists have an O(1) runtime for add operations, which is why arraylists were chosen for this problem's approach.

