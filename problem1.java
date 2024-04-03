import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.atomic.AtomicInteger;

public class problem1 
{
    // public static final int PRESENTS = 50;
    // public static final int PRESENTS = 500;
    // public static final int PRESENTS = 5000;
    // public static final int PRESENTS = 50000;
    public static final int PRESENTS = 500000;
    public static final int SERVANTS = 4;

    // An array of threads for each servant
    servants[] servantThreads;

    // The number of presents that have been sorted
    AtomicInteger addToChain = new AtomicInteger(0);

    // To remove a present from the chain to write a thank you card
    AtomicInteger removeFromChain = new AtomicInteger(0);

    // Constructor for problem 1
    problem1(int addToChain, int removeFromChain) 
    {
        this.addToChain = new AtomicInteger(addToChain);
        this.removeFromChain = new AtomicInteger(removeFromChain);
    }

    public static void main(String[] args) 
    {
        problem1 party = new problem1(0, 0);

        party.servantThreads = new servants[SERVANTS];

        // The ordered chain of presents
        LockFreeList chain = new LockFreeList();

        // The unordered bag
        ArrayList<Integer> gifts = new ArrayList<Integer>(PRESENTS);

        for (int i = 1; i < PRESENTS; i++) 
        {
            gifts.add(i);
        }

        Collections.shuffle(gifts);

        // Variables to find out simulation duration from start to finish
        long start = 0;
        long end = 0;
        long duration = 0;

        // Variable to number the threads
        int threadCounter = 1;

        // Create and start each thread
        for (int i = 0; i < SERVANTS; i++) 
        {
            party.servantThreads[i] = new servants(chain, gifts, 0, 0, PRESENTS, threadCounter, party);
            threadCounter++;
        }

        // Beginning of the simulation
        start = System.currentTimeMillis();

        for (int i = 0; i < SERVANTS; i++) 
        {
            // Start each thread
            party.servantThreads[i].start();
        }

        // Stop the threads
        for (int i = 0; i < SERVANTS; i++) 
        {
            try {
                party.servantThreads[i].join();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        // End of the simulation
        end = System.currentTimeMillis();

        // Calculate the duration of the simulation
        duration = end - start;

        // Print output
        System.out.println(PRESENTS + " presents have been sorted.");
        System.out.println("This program took " + duration + " ms.");
    }
}

// Class to represent the servants with one thread per servant
class servants extends Thread 
{
    AtomicInteger addToChain = new AtomicInteger();
    AtomicInteger removeFromChain = new AtomicInteger();
    LockFreeList chain;
    ArrayList<Integer> gifts;
    public int presents;
    int curpresent;
    int threadno;

    problem1 main = new problem1(addToChain.get(), removeFromChain.get());

    // Constructor for servants class
    servants(LockFreeList chain, ArrayList<Integer> gifts, int addToChain, int removeFromChain, int presents, int threadno, problem1 main) 
    {
        this.chain = chain;
        this.gifts = gifts;
        this.addToChain = new AtomicInteger(addToChain);
        this.removeFromChain = new AtomicInteger(removeFromChain);
        this.presents = presents;
        this.threadno = threadno;
        this.main = main;
    }

    // Run the thread if there are less sorted presents than the total
    public void run() 
    {
        if (addToChain.get() < presents || removeFromChain.get() < presents) 
        {
            try 
            {
                int tracker;
                tracker = addToChain.getAndIncrement();

                while (tracker < presents) 
                {
                    chain.add(tracker);
                    tracker = addToChain.getAndIncrement();
                }

            }
            catch (Exception e) 
            {
                e.printStackTrace();
            }

            try 
            {
                // Servant writes a thank you card and removes the next present from the chain
                int tracker = removeFromChain.getAndIncrement();

                if (tracker < presents) 
                {
                    chain.remove(tracker);
                    tracker = removeFromChain.getAndIncrement();
                }
            }
            catch (Exception e) 
            {
                e.printStackTrace();
            }

            // The minotaur asks the servants to scan for a present
            if (Math.random() <= 0.000025) 
            {
                Random rand = new Random();
                int currentPresent = rand.nextInt(presents);
                if (chain.contains(currentPresent)) 
                {
                    // The present was found
                }
            }
        }
    }
}

// Lock free list implementation
class LockFreeList 
{
    final Node head;

    public LockFreeList() 
    {
        head = new Node(Integer.MIN_VALUE);
        Node sentinal = new Node(Integer.MAX_VALUE);
        Node tail = new Node(Integer.MAX_VALUE);
        head.next = new AtomicMarkableReference<Node>(tail, false);
        tail.next = new AtomicMarkableReference<Node>(sentinal, false);
    }

    private class Node 
    {
        int giftTag;
        public AtomicMarkableReference<Node> next;

        public Node(int giftTag) 
        {
            this.giftTag = giftTag;
        }
    }

    public boolean add(int giftTag) 
    {
        while (true) 
        {
            Window window = Window.find(head, giftTag);
            Node pred = window.pred, curr = window.curr;

            if (curr.giftTag == giftTag) return false;
            else 
            {
                Node node = new Node(giftTag);
                node.next = new AtomicMarkableReference<>(curr, false);

                if (pred.next.compareAndSet(curr, node, false, false)) 
                {
                    return true;
                }
            }
        }
    }

    public boolean remove(int giftTag) 
    {
        boolean snip;

        while (true) 
        {
            Window window = Window.find(head, giftTag);
            Node pred = window.pred, curr = window.curr;

            if (curr.giftTag != giftTag) return false;
            else 
            {
                Node succ = curr.next.getReference();
                snip = curr.next.compareAndSet(succ, succ, false, true);

                if (!snip) continue;

                pred.next.compareAndSet(curr, succ, false, false);
                return true;
            }
        }
    }

    public boolean contains(int giftTag) 
    {
        Node curr = head;

        while (curr.giftTag < giftTag) 
        {
            curr = curr.next.getReference();
        }

        return (curr.giftTag == giftTag && !curr.next.isMarked());
    }

    private static class Window 
    {
        public Node pred, curr;

        Window(Node myPred, Node myCurr) 
        {
            pred = myPred;
            curr = myCurr;
        }

        public static Window find(Node head, int giftgiftTag) 
        {
            Node pred = null, curr = null, succ = null;
            boolean[] marked = { false };
            boolean snip;

            retry: while (true) 
            {
                pred = head;
                curr = pred.next.getReference();

                while (true) 
                {
                    pred = head;
                    curr = pred.next.getReference();

                    while (true) {
                        succ = curr.next.get(marked);

                        while (marked[0]) 
                        {

                            snip = pred.next.compareAndSet(curr, succ, false, false);

                            if (!snip) continue retry;
                            curr = succ;
                            succ = curr.next.get(marked);
                        }

                        if (curr.giftTag >= giftgiftTag) return new Window(pred, curr);

                        pred = curr;
                        curr = succ;
                    }
                }
            }
        }
    }
}
