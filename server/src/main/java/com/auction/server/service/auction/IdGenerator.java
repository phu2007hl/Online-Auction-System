package com.auction.server.service.auction;

import java.util.HashSet;
import java.util.Random;

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
