/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.user.smssecure.backend.ecceg;

import java.math.BigInteger;
import java.util.Random;

/**
 *
 * @author atia
 */
public class Gen {
    
    public static BigInteger generateK(BigInteger prime) {
        boolean found = false;
        BigInteger key = null;
        
        while (!found) {
            Random rand = new Random();
            key = new BigInteger(rand.nextInt(prime.bitLength()), new Random());
            if (key.compareTo(prime) == -1 && key.compareTo(BigInteger.ZERO) == 1)
                found = true;
        }
        return key;
    }
    
    public static void main (String [] args) {
        Gen gen = new Gen();
        System.out.println(gen.generateK(new BigInteger("2147483647")));
    }
}
