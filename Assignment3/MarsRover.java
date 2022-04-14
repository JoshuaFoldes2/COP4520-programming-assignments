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
    static int readings1[][] = new int[8][60];
    static int readings2[][] = new int[8][60];
    static int switcher[] = {0,0,0,0,0,0,0,0};
    static int arraySource = 0;
    static AtomicInteger finished = new AtomicInteger(0);
    static int hours = 0;
    static int timepassed = 0;
    int counter = 0;
    int identity;

    MarsRover(int identity){
        this.identity = identity;
    }

    static void reportGenerator(){
        finished.set(0);
        int timePeriod = 1, tempChange = 0;
        int startmax = 0, startmin = 0, endmax = 0, endmin = 0;
        //finds the min and max for each column and compares each min with the other column's max to find the biggest difference as well as checking for max and min values
        if(arraySource == 0){
            for(int i = 0; i < 50; i++){
                startmax = readings1[0][i];
                startmin = readings1[0][i];
                endmax = readings1[0][i+10];
                endmin = readings1[0][i+10];
                store(readings1[0][i]);


                for(int j = 1; j < 8; j++){
                    startmax = Math.max(startmax, readings1[j][i]);
                    startmin = Math.min(startmin, readings1[j][i]);
                    endmax = Math.max(endmax, readings1[j][i+10]);
                    endmin = Math.min(endmin, readings1[j][i+10]);
                    store(readings1[j][i]);
                }
                int tempp = Math.max(Math.abs(startmax - endmin),Math.abs(startmin-endmax));
                if(tempp > tempChange){
                    timePeriod = i+1;
                    tempChange = tempp;
                }

            }
            for(int i = 50; i < 60; i++){
                for(int j = 0; j < 8; j++){
                    store(readings1[j][i]);
                }
            }
        }
        else{
            for(int i = 0; i < 50; i++){
                startmax = readings2[0][i];
                startmin = readings2[0][i];
                endmax = readings2[0][i+10];
                endmin = readings2[0][i+10];
                store(readings2[0][i]);


                for(int j = 1; j < 8; j++){
                    startmax = Math.max(startmax, readings2[j][i]);
                    startmin = Math.min(startmin, readings2[j][i]);
                    endmax = Math.max(endmax, readings2[j][i+10]);
                    endmin = Math.min(endmin, readings2[j][i+10]);
                    store(readings2[j][i]);
                }
                int tempp = Math.max(Math.abs(startmax - endmin),Math.abs(startmin-endmax));
                if(tempp > tempChange){
                    timePeriod = i+1;
                    tempChange = tempp;
                }

            }
            for(int i = 50; i < 60; i++){
                for(int j = 0; j < 8; j++){
                    store(readings2[j][i]);
                }
            }

        }

        //prints report and resets the min and max values
        System.out.println("Report " + (timepassed+1) + " of " + hours + "\nHigh values:  " + high.toString() + "\nLow values:  " + low.toString() + "\nThe time period with the most change is from minute " + timePeriod + " to minute " + (timePeriod + 10) + " with a change of " + tempChange + "\n\n");
        for(int i = 0;i<5;i++){
            high.set(i,0);
            low.set(i,0);
        }
        timepassed++;
        if(arraySource == 0){
            arraySource = 1;
        }
        else{
            arraySource = 0;
        }

    }
    public static void main(String[] args) throws InterruptedException{
        Scanner numHours = new Scanner(System.in);

        //Scanner for number of hours, will continue requesting a number until a positive integer is given to avoid program crash
        while(hours < 1){
            System.out.println("\nHow many hours would you like to simulate");
            hours = numHours.nextInt();
        }
        numHours.close();

        //initializes the threads 
        MarsRover sensors[] = new MarsRover[8];
        for(int i = 0; i < 8; i++){
            sensors[i] = new MarsRover(i);
            sensors[i].identity = i;
            sensors[i].counter = 0;
        }

            sensors[0].start();
            sensors[1].start();
            sensors[2].start();
            sensors[3].start();
            sensors[4].start();
            sensors[5].start();
            sensors[6].start();
            sensors[7].start();
        //while the set number of hours have not transpired, runs the threads and then creates the report
        while(timepassed < hours){
            if(finished.get() == 8){
                reportGenerator();
            }

        }
    }

    //The run for the threads
    public synchronized void run(){
        int hourCounter = 0;
        while(hourCounter < hours){
            counter = 0;
            if(switcher[identity] == 0){
                while(counter < 60){
                    readings1[identity][counter] = (((int)(Math.round(Math.random() * 170))) - 100);
                    counter++;
                    try{
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }

                }
                finished.getAndIncrement();
                switcher[identity] = 1;
            }
            else{            
                while(counter < 60){
                    readings2[identity][counter] = (((int)(Math.round(Math.random() * 170))) - 100);
                    counter++;
                    try{
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                finished.getAndIncrement();
                switcher[identity] = 0;
            }
            hourCounter++;
        }
    }

    //This algorithm adds the top 5 values to this list
    public static void store(int value){
        int i = hC.get();
        int find = 0;

        //This determines if the number is a new unique number that belongs in the top 5 and where it belongs
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

        //this inserts and shifts everything back
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
 
        //This determines if the number is a new unique number that belongs in the top 5 and where it belongs
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

        //this inserts and shifts everything back
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
