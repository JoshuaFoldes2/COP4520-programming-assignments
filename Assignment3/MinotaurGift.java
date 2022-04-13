import java.util.*;
import java.math.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.BufferedWriter;

//This class contains all the global variables to run the functions
class StarterValues{

}
class Node{
    AtomicMarkableReference<Node> next;
    int key;

    public Node(int key){
        this.key = key;
        this.next = new AtomicMarkableReference<Node>(null, false);
    }
}
class Window{
    public Node pred, curr;
    Window(Node pred, Node curr){
        this.pred = pred;
        this.curr = curr;
    }
} 
class MinotaurList{
    Node head;
    public MinotaurList(){
        head = new Node(Integer.MIN_VALUE);
        head.next = new AtomicMarkableReference<Node>(new Node(Integer.MAX_VALUE),false);
    }
    public Window find(Node head, int key){
        Node pred = null, curr = null, succ = null;
        boolean[] marked = {false};
        boolean snip;
        retry: while(true){
            pred = head;
            curr = pred.next.getReference();
            while(true){
                succ = curr.next.get(marked);
                while (marked[0]){
                    snip = pred.next.compareAndSet(curr, succ, false, false);
                    if(!snip) continue retry;
                    curr = succ;
                    succ = curr.next.get(marked);
                }
                if(curr.key >= key)
                    return new Window(pred, curr);
                pred = curr;
                curr = succ;
            }
        } 
    }

    public boolean add(int key){
        while(true){
            Window window = find(head, key);
            Node pred = window.pred, curr = window.curr;
            if(curr.key == key){
                return false;
            }
            else{
                Node node = new Node(key);
                node.next = new AtomicMarkableReference<Node>(curr, false);
                if (pred.next.compareAndSet(curr, node, false, false)){
                    return true;
                }
            }
        }
    }

    public boolean remove(int key){
        boolean snip;
        while(true){
            Window window = find(head,key);
            Node pred = window.pred, curr = window.curr;
            if(curr.key != key){
                return false;
            }
            else{
                Node succ = curr.next.getReference();
                snip = curr.next.attemptMark(succ, true);
                if(!snip)
                    continue;
                pred.next.compareAndSet(curr, succ, false, false);
                return true;
            }
        }
    }
        public boolean removeHeadNext(){
            Node curr = head;
            boolean[] marked = {false};
            while(true){
                curr = curr.next.get(marked);
                if(marked[0] == false){
                    if(curr.key != Integer.MAX_VALUE)
                        return remove(curr.key);
                    else   
                        return false;
                }
            }
        }

    public boolean contains(int key){
        boolean[] marked = {false};
        Node curr = head;
        while(curr.key < key){
            curr = curr.next.getReference();
            Node succ = curr.next.get(marked);
        }
        return(curr.key == key && !marked[0]);
    }

}

public class MinotaurGift extends Thread{

    //constants and Array container for threads, for
    static int bagsize = 500000;
    static int bagPercent = bagsize/100;
    static AtomicInteger bagPlace = new AtomicInteger(0);
    static AtomicInteger letters = new AtomicInteger(0);
    static int[] bag = new int[bagsize];
    static MinotaurList list = new MinotaurList();
    int choice;
    int minotaurRequest;

    MinotaurGift(){
        this.choice = 0;
        this.minotaurRequest = 0;
    }

    public static void main(String[] args) throws InterruptedException{

        for(int i = 0; i < bagsize;i++){
            bag[i] = i+1;
        }
        for(int i = 0; i < bagsize;i++){
            int temp = (((int)((Math.random() * (bagsize)))));
            int temp2 = bag[i];
            bag[i] = bag[temp];
            bag[temp] = temp2;
        }

        //initializes the threads and sets the first thread in the array as the leader and makes sure the eaten variable on all the threads is 0 
        MinotaurGift servants[] = new MinotaurGift[4];
        for(int i = 0; i < 4; i++){
            servants[i] = new MinotaurGift();
        }

        //while not the guests have eaten the cupcake, this loop pick a thread from the array randomly and has it execute run()
        System.out.print("0%-");
        servants[0].run();
        servants[1].run();
        servants[2].run();
        servants[3].run();
        servants[0].join();
        servants[1].join();
        servants[2].join();
        servants[3].join();

        System.out.println("\nThe servants have finished with writing the thank you notes" + bagPlace.get() + letters.get());
    }


    //The run for the threads
    public synchronized void run(){
        while(letters.get() < bagsize){
            minotaurRequest = (((int)(Math.round(Math.random() * 99))));
            if(minotaurRequest > 0){
                choice = (((int)(Math.round(Math.random()))));
                if(choice == 0){
                    if(bagPlace.get() < bagsize){
                        if(list.add(bag[bagPlace.get()]))
                            bagPlace.getAndIncrement();
                    }
                }
                else{
                    if(list.removeHeadNext()){
                        letters.getAndIncrement();
                        if(letters.get()%bagsize == 0){
                            System.out.print("-100%");
                        }
                        else if(letters.get()%(bagPercent*10) == 0){
                            System.out.print("-" + (letters.get()/bagPercent) + "%-");
                        }
                        else if(letters.get()%bagPercent == 0){
                            System.out.print("|");
                        }
                    }
                }
            }
            else{
                list.contains(((int)((Math.random() * bagsize)+1)));
            }
        }
    }
}
