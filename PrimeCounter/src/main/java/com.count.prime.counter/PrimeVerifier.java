package com.count.prime.counter;

/**
 * Created by imran on 1/7/17.
 */
public class PrimeVerifier {
    //No state

    //checks whether an int is prime or not.
    public static boolean isPrime(int n) {
        for(int i=2;i<n;i++) {
            if(n%i==0)
                return false;
        }
        return true;
    }
}
