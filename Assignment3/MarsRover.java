import java.util.*;
import java.math.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.BufferedWriter;

//This class contains all the global variables to run the functions
class StarterValues{

}



public class MarsRover extends Thread{

    //constants and Array container for threads, for
    static AtomicIntegerArray high = new AtomicIntegerArray(5);
    static AtomicIntegerArray low = new AtomicIntegerArray(5);
    static AtomicInteger hC = new AtomicInteger(0);
    static AtomicInteger lC = new AtomicInteger(0);
    static AtomicInteger time = new AtomicInteger(0);
    static int readings[][] = new int[8][60];
    static int hours = 0;
    static int timepassed = 0;
    int counter = 0;
    int identity;

    MarsRover(int identity){
        this.identity = identity;
    }

    public static void main(String[] args) throws InterruptedException{
        Scanner numHours = new Scanner(System.in);

        //Scanner for number of guests, will continue requesting a number until a positive integer is given to avoid program crash
        while(hours < 1){
            System.out.println("\nHow many hours would you like to simulate");
            hours = numHours.nextInt();
        }
        numHours.close();

        //initializes the threads and sets the first thread in the array as the leader and makes sure the eaten variable on all the threads is 0 
        MarsRover sensors[] = new MarsRover[8];
        for(int i = 0; i < 8; i++){
            sensors[i] = new MarsRover(i);
            sensors[i].identity = i;
            sensors[i].counter = 0;
        }

        //while not the guests have eaten the cupcake, this loop pick a thread from the array randomly and has it execute run()
        while(timepassed < hours){
            sensors[0].run();
            sensors[1].run();
            sensors[2].run();
            sensors[3].run();
            sensors[4].run();
            sensors[5].run();
            sensors[6].run();
            sensors[7].run();
            sensors[0].join();
            sensors[1].join();
            sensors[2].join();
            sensors[3].join();
            sensors[4].join();
            sensors[5].join();
            sensors[6].join();
            sensors[7].join();

            int timePeriod = 1, tempChange = 0;
            int startmax = 0, startmin = 0, endmax = 0, endmin = 0;
            for(int i = 0; i < 50; i++){
                startmax = readings[0][i];
                startmin = readings[0][i];
                endmax = readings[0][i+10];
                endmin = readings[0][i+10];
                store(readings[0][i]);


                for(int j = 1; j < 8; j++){
                    startmax = Math.max(startmax, readings[j][i]);
                    startmin = Math.min(startmin, readings[j][i]);
                    endmax = Math.max(endmax, readings[j][i+10]);
                    endmin = Math.min(endmin, readings[j][i+10]);
                    store(readings[j][i]);
                }
                int tempp = Math.max(Math.abs(startmax - endmin),Math.abs(startmin-endmax));
                if(tempp > tempChange){
                    timePeriod = i+1;
                    tempChange = tempp;
                }

            }
            for(int i = 50; i < 60; i++){
                for(int j = 0; j < 8; j++){
                    store(readings[j][i]);
                }
            }
            System.out.println("\n\nHigh values:  " + high.toString() + "\nLow values:  " + low.toString() + "\nThe time period with the most change is from minute " + timePeriod + " to minute " + (timePeriod + 10) + " with a change of " + tempChange);
            for(int i = 0;i<5;i++){
                high.set(i,0);
                low.set(i,0);
            }
            timepassed++;
        }

        //Statement of completion
    }

    //The run for the threads
    public synchronized void run(){
        counter = 0;
        while(counter < 60){
            readings[identity][counter] = (((int)(Math.round(Math.random() * 170))) - 100);
            counter++;
        }
    }

    public static void store(int value){
        int i = hC.get();
        int find = 0;
        while(i >= 0){
            if(value == high.get(i)){
                break;
            }
            if(value > high.get(i)){
                if(i == 0){
                    find = 1;
                    break;
                }
                else{
                    i--;
                }
            }
            if(value < high.get(i)){
                if(i < 4 && value > high.get(i+1)){
                    find = 1;
                    i++;
                    break;
                }
                else{
                    break;
                }
            }   
        }
        int temp = value;
        int temp2 = 0;
        if(find == 1){
            for(;i < 5;i++){
                if(high.get(i) == 0){
                    high.set(i,temp);
                    break;
                }
                else{
                    temp2 = high.get(i);
                    high.set(i,temp);
                    temp = temp2;

                }
            }
            if(hC.get()<4)
                hC.getAndIncrement();
        }


        i = lC.get();
        find = 0;
        while(i >= 0){
            if(value == low.get(i)){
                break;
            }
            if(value < low.get(i)){
                if(i == 0){
                    find = 1;
                    break;
                }
                else{
                    i--;
                }
            }
            if(value > low.get(i)){
                if(i < 4 && value < low.get(i+1)){
                    find = 1;
                    i++;
                    break;
                }
                else{
                    break;
                }
            }
        }
        temp = value;
        temp2 = 0;
        if(find == 1){
            for(;i < 5;i++){
                if(low.get(i) == 0){
                    low.set(i,temp);
                    break;
                }
                else{
                    temp2 = low.get(i);
                    low.set(i,temp);
                    temp = temp2;

                }
            }
            if(lC.get()<4)
                lC.getAndIncrement();
        }
    } 
}
