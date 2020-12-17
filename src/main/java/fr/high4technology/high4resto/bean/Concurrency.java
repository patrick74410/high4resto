package fr.high4technology.high4resto.bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;

@Getter
public class Concurrency {
    private Map<String, Integer> map = new ConcurrentHashMap<>();
}
