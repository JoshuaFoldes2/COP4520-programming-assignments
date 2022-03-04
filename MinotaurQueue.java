import java.util.Queue;
import java.util.LinkedList;
import java.math;

public class MInotaurQueue extends Thread{
    //has all starting values (tGuests is the total guests so for the purposes of testing, this is the value to change)
    static int tGuests = 100;
    static int sGuests = 0;
    MInotaurQueue guestList[];
    Queue<MInotaurQueue> vaseLine;
    static final int queueSize = 11;
    static int queueCapacity = 11;
    static int visitLength = 0;
    static int newQueuers = 0;
    static int i = 0;
    static int busy = 0;
    static boolean equal = false;
    int seen;
    int inQueue;
    static int runs;
    
    //thread parameters
    MInotaurQueue(int seen, int inQueue){
        this.seen = seen;
        this.inQueue = inQueue;
    }
    public static void main(String[] args) throws InterruptedException{

        //initializes array of threads and makes sure all values are set to 0
        System.out.println(" --- number of Guests at the party is " + tGuests + " --- ");
        MInotaurQueue guestList[] = new MInotaurQueue[tGuests];
        for(int i = 0; i < tGuests; i++){
            guestList[i] = new MInotaurQueue(0,0);
            guestList[i].start();
            guestList[i].seen = 0;
            guestList[i].inQueue = 0;
        }

        //Queue initialization
        Queue<MInotaurQueue> vaseLine = new LinkedList<MInotaurQueue>();

        //controller loop
        while(sGuests < tGuests  || vaseLine.peek() != null){

            //the head of the queue represents the room, so once the visitLength is 0, the head is popped representing the person leaving the room
            if(visitLength == 0){
                if(vaseLine.peek() != null){
                    vaseLine.element().inQueue = 0;
                    vaseLine.remove();
                    queueCapacity++;
                }
            }

            //this is where the number of guests to be added to the queue is chosen and added.  this part of the code stops being used once the queue closes while the main loop continues to finish emptying the queue
            if(sGuests < tGuests){

                //if the queue is empty, the number of guests is a minimum of 1
                if(queueSize == queueCapacity){
                    newQueuers = (int)((Math.random()*3)+1);
                }

                //as long as the queue isn't full, it picks a random number between -1 and either 3 or the queue capacity if that is smaller
                //the 0 guest choice is double weighted (since -1 does the same thing as 0) to try and stop the queue from filling up as fast
                else{
                    if(queueCapacity != 0){
                        if(queueCapacity<3){
                            newQueuers = (int)((Math.random()*(queueCapacity+2))-1);
                        }
                        else{
                            newQueuers = (int)((Math.random()*(5))-1);
                        }
                    }
                }

                //this picks what thread to add to the queue
                for(int j = 0;j < newQueuers;j++){
                    boolean Continue = true;

                    //this loop runs until a thread is added to the queue
                    while(Continue){
                        if(i == tGuests){
                            i = 0;
                        }

                        // a random number is selected and if the thread in question isn't already in the queue, the thread has a 50% chance to join if it hasn't seen the vase and a 25% chance to join if it has
                        int enterQueue = (int)((Math.random()*(20))+1);
                        if(((guestList[i].seen == 0 && enterQueue <11)||(guestList[i].seen == 1 && enterQueue < 6)) && (guestList[i].inQueue == 0)){
                                vaseLine.add(guestList[i]);
                                queueCapacity--;
                                guestList[i].inQueue = (i+1);
                                Continue = false;
                        }
                        i++;
                    }
                }
            }

            //if the previous thread just got popped, the visitLength is set to between 1 and 3
            if(visitLength == 0){
                visitLength = (int)((Math.random()*3)+1);
            }

            //run() is run as long as the queue isn't empty
            if(vaseLine.peek() != null){
                busy = 1;
                vaseLine.element().run();
            }
            runs++;

            //if all the guests have seen the vase, no new threads are added to the queue and allows the queue to empty and the program to terminate printing a message to terminal
            if(sGuests == tGuests && !equal){
                System.out.println("\n\n --- all guests have seen the vase so Mr. Minotaur is closing the queue --- \n");
                equal = true;
            }
        }

        //completion message
        System.out.println(" --- The queue is empty and all guests have seen the vase --- \n");
        System.out.println("The controller ran the loop " + runs + " times");
    }

    //the run command wait the thread 1 second representing visit the vase.  One thread might run this up to three times based upon visitLength
    public synchronized void run(){

        //if the thread is supposed to be active it runs the logic
        if(busy == 1){
            try{
                wait(1000);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            visitLength--;

            //once the visit is considered over, the thread documents it has seen the vase if this is the first time through the queue
            if(visitLength == 0){
                if(seen == 0){
                    seen = 1;
                    sGuests++;
                    System.out.print(sGuests + " ");
                }
            }
            busy = 0;
        }
    }   
}
