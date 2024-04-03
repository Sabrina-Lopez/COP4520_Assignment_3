import java.util.*;

/*

*/

public class problem2 
{
    public static void main(String[] args) 
    {
        final int sensors = 8;
        final int hours = 3;

        // There is a list of temperatures at each sensor
        ArrayList<ArrayList<Integer>> temps = new ArrayList<ArrayList<Integer>>(sensors);

        // Initialize a rover
        Rover[] rv;

        // Variables to find out simulation duration from start to finish
        long start = 0;
        long end = 0;
        long duration = 0;

        // Beginning the simulation
        start = System.currentTimeMillis();

        // For each hour, get the temperatures
        for (int i = 0; i < hours; i++) 
        {
            // Make an array of 8 threads, one for each sensor
            rv = new Rover[sensors];

            // Populate each thread
            for (int j = 0; j < sensors; j++) 
            {
                ArrayList<Integer> sensorTemps = new ArrayList<Integer>();
                rv[i] = new Rover(sensorTemps, j);

                // Add the list of temps from each sensor to the greater list
                temps.add(sensorTemps); 
            }

            // Start each thread
            for (int j = 0; j < sensors; j++) 
            {
                rv[i].run();
            }

            // Stop the threads
            for (int j = 0; j < sensors; j++) 
            {
                try 
                {
                    rv[i].join();
                }
                catch (Exception e) 
                {
                    e.printStackTrace();
                }
            }

            // For each hour, print output from the generated report
            Report rep = new Report(temps, i);
            rep.highTemps();
            rep.lowTemps();
            rep.printOutput();
        }

        // End of the simulation
        end = System.currentTimeMillis();

        // Calculate the duration of the simulation
        duration = end - start;

        // Print output
        System.out.println("It took " + duration + " ms to record the temperature for " + hours + " hours.");
    }
}

// Class to represent the rovers with one thread per rover
class Rover extends Thread 
{
    // The list of temperatures held by this sensor
    ArrayList<Integer> temperatures; 

    // The id of the current sensor
    int id; 

    // Constructor
    Rover(ArrayList<Integer> arr, int id) 
    {
        this.temperatures = arr;
        this.id = id;
    }

    public void run() 
    {
        // Fills a sensor's list with temperatures each minute of an hour
        Random rand = new Random();

        for (int i = 0; i < 60; i++) 
        {
            // Simulate a minute passing
            try 
            {
                Rover.sleep(15);
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }

            // In range -100F to 70F, get a random temperature
            int nextTemp = rand.nextInt(70 - (-100 - 1)) + (-100);

            temperatures.add(nextTemp);
        }
    }
}

class Report 
{
    // The id is the hour that the report is for
    int id; 

    // All the temperatures for all the sensors in this hour
    ArrayList<ArrayList<Integer>> temps; 

    // The interval with the highest range
    int rangedInterval; 

    // The top 5 highest and lowest temperatures of the hour
    ArrayList<Integer> highestTemps = new ArrayList<>();
    ArrayList<Integer> lowestTemps = new ArrayList<>();

    // Constructor for the report class
    Report(ArrayList<ArrayList<Integer>> temperatures, int hourNum) 
    {
        this.temps = temperatures;
        this.id = hourNum;
    }

    // Get the highest temperatures
    public void highTemps() 
    {
        // Merge each sensor's list into one
        ArrayList<Integer> total = new ArrayList<>();

        for (ArrayList<Integer> list : temps) 
        {
            total.addAll(list);
        }

        // Get the 5 top temperatures
        total.sort(Comparator.reverseOrder());

        for (int i = 0; i < 5; i++) highestTemps.add(total.get(i));
    }

    // Get the lowest temperatures
    public void lowTemps() 
    {
        // Merge each sensor's list into one
        ArrayList<Integer> total = new ArrayList<>();

        for (ArrayList<Integer> list : temps) 
        {
            total.addAll(list);
        }

        // Get the 5 lowest temperatures
        total.sort(Comparator.naturalOrder());

        for (int i = 0; i < 5; i++) lowestTemps.add(total.get(i));
    }

    // Get the 10-minute intervals and ranges
    public int greatestRangeInterval() 
    {

        // There are 6 10-minute intervals in the hour, every 10 temperatures for a sensor will be in an interval,
            // and there will be 80 temperatures in each interval

        // Create lists for each interval
        ArrayList<Integer> int1 = new ArrayList<>();
        ArrayList<Integer> int2 = new ArrayList<>();
        ArrayList<Integer> int3 = new ArrayList<>();
        ArrayList<Integer> int4 = new ArrayList<>();
        ArrayList<Integer> int5 = new ArrayList<>();
        ArrayList<Integer> int6 = new ArrayList<>();

        // Every 10 temperatures from each sensor goes into the next interval
        for (ArrayList<Integer> list : temps) 
        {
            if (list.size() != 0) 
            {
                for (int i : list) 
                {
                    for (int j = 0; j < 80; j++) 
                    {
                        int1.add(list.get(j));
                    }
                    for (int j = 80; j < 160; j++) 
                    {
                        int2.add(list.get(j));
                    }
                    for (int j = 160; j < 240; j++) 
                    {
                        int3.add(list.get(j));
                    }
                    for (int j = 240; j < 320; j++) 
                    {
                        int4.add(list.get(j));
                    }
                    for (int j = 320; j < 400; j++) 
                    {
                        int5.add(list.get(j));
                    }
                    for (int j = 400; j < 480; j++) 
                    {
                        int6.add(list.get(j));
                    }
                }
            }

        }

        // Sort each interval
        int1.sort(Comparator.naturalOrder());
        int2.sort(Comparator.naturalOrder());
        int3.sort(Comparator.naturalOrder());
        int4.sort(Comparator.naturalOrder());
        int5.sort(Comparator.naturalOrder());
        int6.sort(Comparator.naturalOrder());

        // Determine the interval ranges
        int range1 = int1.get(79) - int1.get(0);
        int range2 = int2.get(79) - int2.get(0);
        int range3 = int3.get(79) - int3.get(0);
        int range4 = int4.get(79) - int4.get(0);
        int range5 = int5.get(79) - int5.get(0);
        int range6 = int6.get(79) - int6.get(0);

        // Return the largest interval range
        if (range1 >= range2 && range1 >= range3 && range1 >= range4 && range1 >= range5 && range1 >= range6)
            return 1;
        else if (range2 >= range1 && range2 >= range3 && range2 >= range4 && range2 >= range5 && range2 >= range6)
            return 2;
        else if (range3 >= range1 && range2 >= range3 && range3 >= range4 && range3 >= range5 && range3 >= range6)
            return 3;
        else if (range4 >= range1 && range4 >= range2 && range4 >= range3 && range4 >= range5 && range4 >= range6)
            return 4;
        else if (range5 >= range1 && range5 >= range2 && range5 >= range3 && range5 >= range4 && range5 >= range6)
            return 5;
        else if (range6 >= range1 && range6 >= range2 && range6 >= range3 && range6 >= range4 && range6 >= range5)
            return 6;
        else
            return 0;
    }

    public void printOutput() 
    {
        // Print out current hour id
        System.out.println("Report for hour: " + (id + 1));

        // Print top 5 highest temperatures
        System.out.println("Top 5 highest temperatures:");
        for (int i : highestTemps) 
        {
            System.out.println(i);
        }

        // Print top 5 lowest temperatures
        System.out.println("Top 5 lowest temperatures:");
        for (int i : lowestTemps) 
        {
            System.out.println(i);
        }

        // Print the interval with the greatest temperature difference
        System.out.println("Interval with the highest temperature difference: " + greatestRangeInterval() + "\n\n");
    }
}
