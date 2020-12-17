package fr.high4technology.high4resto.bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;

public class Concurrency {
    public static Map<String, Integer> map = new ConcurrentHashMap<>();
}
