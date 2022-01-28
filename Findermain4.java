import java.util.*;
import java.math.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.BufferedWriter;

class StarterValues{
    static int starter = ((int)(Math.pow(10, 8)));
    static int size = (starter/10*4);
    static AtomicLongArray sieve = new AtomicLongArray(size);
    static long topTen[] = new long[10];
    static long primeCounter = 2;
    static long primeSum = 7;
}
class primeThread extends Thread{
    int start;
    int end;
    int index = 0;
    primeThread(long start, long end){
        this.start = (int)(start);
        this.end = (int)(end);
    }
    public synchronized void run(){
        while(index < StarterValues.size){
            if(StarterValues.sieve.get(index) != 0)
                if(start < index)
                    sieveMaker(StarterValues.sieve.get(index),index+1,end);
                else
                    sieveMaker(StarterValues.sieve.get(index),start,end);
            index++;
        }
    }
    public synchronized void sieveMaker(long prime,int start,int end){
        for(int i = start; i <= end; i++){
            if(prime < StarterValues.sieve.get(i))
                if(StarterValues.sieve.get(i)%prime == 0)
                    StarterValues.sieve.set(i,0);
        }
    }
}
public class Findermain4 extends Thread{
    public static void primeRecorder(){
        int top = 9;
        for(int i = StarterValues.size - 1; i >= 0; i--){
            if(StarterValues.sieve.get(i)>0){
                StarterValues.primeCounter++;
                StarterValues.primeSum += StarterValues.sieve.get(i);
                if(top >= 0){
                    StarterValues.topTen[top] = StarterValues.sieve.get(i);
                    top--;
                }
            }
        }
    }
    public static void main(String[] args) throws IOException{
        int j = 0;
        int k = StarterValues.size;
        for(int i = 0; i < k;i+=4){
            StarterValues.sieve.set(i, (j * 10) + 1);
            StarterValues.sieve.set(i+1, (j * 10) + 3);
            StarterValues.sieve.set(i+2, (j * 10) + 7);
            StarterValues.sieve.set(i+3, (j * 10) + 9);
            j++;
        }
        StarterValues.sieve.set(0,0);
        Long startTime = System.nanoTime();
        int eights = StarterValues.size / 8;

        primeThread thread1 = new primeThread(0,eights-1);
        primeThread thread2 = new primeThread(eights,(2*eights)-1);
        primeThread thread3 = new primeThread(2*eights,(3*eights)-1);
        primeThread thread4 = new primeThread(3*eights,(4*eights)-1);
        primeThread thread5 = new primeThread(4*eights,(5*eights)-1);
        primeThread thread6 = new primeThread(5*eights,(6*eights)-1);
        primeThread thread7 = new primeThread(6*eights,(7*eights)-1);
        primeThread thread8 = new primeThread(7*eights,StarterValues.size-1);
        try{
            thread1.start();
            thread1.join();
            thread2.start();
            thread2.join(100);
            thread3.start();
            thread4.start();
            thread5.start();
            thread6.start();
            thread7.start();
            thread8.start();
            thread3.join();
            thread4.join();
            thread5.join();
            thread6.join();
            thread7.join();
            thread8.join();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        Long endTime = System.nanoTime();
        Long runtime = (endTime - startTime)/1000000;
        primeRecorder();
        System.out.print("\n");
        System.out.print("Number of primes is:  " + StarterValues.primeCounter + "\nSum of primes is:  " + StarterValues.primeSum + "\nTop ten primes were:  " + "\n" + StarterValues.topTen[0] + "\n" + StarterValues.topTen[1] + "\n" + StarterValues.topTen[2] + "\n" + StarterValues.topTen[3] + "\n" + StarterValues.topTen[4] + "\n" + StarterValues.topTen[5] + "\n" + StarterValues.topTen[6] + "\n" + StarterValues.topTen[7] + "\n" + StarterValues.topTen[8] + "\n" + StarterValues.topTen[9] + "\nExecution Time for Prime Finding Algorithm is: " + runtime + " ms");

        File file = new File("primes.txt");
        FileWriter fw = new FileWriter(file);
        fw.write("Number of primes is:  " + StarterValues.primeCounter + "\nSum of primes is:  " + StarterValues.primeSum + "\nTop ten primes were:  " + "\n" + StarterValues.topTen[0] + "\n" + StarterValues.topTen[1] + "\n" + StarterValues.topTen[2] + "\n" + StarterValues.topTen[3] + "\n" + StarterValues.topTen[4] + "\n" + StarterValues.topTen[5] + "\n" + StarterValues.topTen[6] + "\n" + StarterValues.topTen[7] + "\n" + StarterValues.topTen[8] + "\n" + StarterValues.topTen[9] + "\nExecution Time for Prime Finding Algorithm is: " + runtime + " ms");
        fw.close();
    }

    
}
