package fr.high4technology.high4resto.bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Concurrency {
    public static Map<String, Integer> mapStock = new ConcurrentHashMap<>();
    public static Map<Long, Integer> mapCountCommande = new ConcurrentHashMap<>();
}
