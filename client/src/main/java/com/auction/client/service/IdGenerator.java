package com.auction.client.service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class IdGenerator {
    private static HashSet<Integer> idSet = new HashSet<>();
    public static int generateId(){
    idSet.add(10000000);
    Random r = new Random();
    int id = 10000000;
    while (idSet.contains(id)){
        id = r.nextInt(10000000, 99999999);
    }
    return id;

    }
    
}
