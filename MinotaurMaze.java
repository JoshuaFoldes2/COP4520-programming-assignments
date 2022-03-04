import java.util.*;
import java.math.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.BufferedWriter;

//This class contains all the global variables to run the functions
class StarterValues{

}

 

public class MinotaurMaze extends Thread{

    //constants and Array container for threads, for
    static AtomicInteger cupcake = new AtomicInteger(1);
    static int tGuests = 0;
    static int cGuests = 0;
    MinotaurMaze guestList[];
    static int busy = 0;
    static int runs = 0;
    int leader;
    int eaten;

    MinotaurMaze(int leader, int eaten){
        this.leader = leader;
        this.eaten = eaten;
    }
    public static void main(String[] args) throws InterruptedException{
        Scanner numGuest = new Scanner(System.in);

        //Scanner for number of guests, will continue requesting a number until a positive integer is given to avoid program crash
        while(tGuests < 1){
            System.out.println("\nHow many guests did Mr. Minotaur invite?");
            tGuests = numGuest.nextInt();
        }
        numGuest.close();

        //initializes the threads and sets the first thread in the array as the leader and makes sure the eaten variable on all the threads is 0 
        MinotaurMaze guestList[] = new MinotaurMaze[tGuests];
        for(int i = 0; i < tGuests; i++){
            guestList[i] = new MinotaurMaze(0,0);
            guestList[i].start();
            guestList[i].leader = 0;
            guestList[i].eaten = 0;
        }
        guestList[0].leader = 1;
        guestList[0].eaten = 0;
        //for(int i = 0; i < 20; i++){

        //while not the guests have eaten the cupcake, this loop pick a thread from the array randomly and has it execute run()
        Long startTime = System.nanoTime();
        while(cGuests < tGuests){
            if(busy == 0){
                busy = 1;
                runs++;
                int num = (int)(Math.random() * tGuests);
                guestList[num].run();
            }
        }
        Long endTime = System.nanoTime();
        Long runtime = (endTime - startTime)/1000000;

        //Statement of completion
        System.out.println("\nMr. Minotaur, all the guests have entered the maze and eaten a cupcake");
        System.out.println("The Minotaur picked a guest  " + runs + " times with a program runtime of " + runtime + "ms");
    }

    //The run for the threads
    public synchronized void run(){
        System.out.print("");

        //if the thread is supposed to be active it runs the logic
        if(busy == 1){

            //if the thread has already eaten a cupcake, it skips the logic, but this rule does not apply to the leader in case something went wrong and the leader is somehow counted as having eaten the cupcake so it doesn't run infinitely
            if(eaten == 0 || leader == 1){

                //leader logic
                if(leader == 1){

                    //if the cupcake is missing, replace it and count that someone ate it and print that the number of guests who ate has increased
                    if(cupcake.get() == 0){
                        cupcake.set(1);
                        cGuests++;
                        System.out.print(cGuests + " ");
                    }

                    //if everyone else has eaten a cupcake, the leader eats one and then counts it and displays it
                    if((cGuests + 1 == tGuests) && (cupcake.get() == 1) && (eaten == 0)){
                            cupcake.set(0);
                            eaten = 1;
                            cGuests++;
                            System.out.print(cGuests + " ");
                    }
                }

                //non-leader logic
                if(leader == 0){

                    //if the cupcake exists, eat it (threads that have eaten already can't enter here so if they reach this far, they haven't eaten yet and won't cause false increments)
                    if(cupcake.get() == 1){
                        cupcake.set(0);
                        eaten = 1;
                    }
                }
            }

            //tell the main thread that this thread is done so it can pick a new thread to execute run()
            busy = 0;
        }
    }
}
