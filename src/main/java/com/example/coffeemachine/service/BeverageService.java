package com.example.coffeemachine.service;

import com.example.coffeemachine.exceptions.NotFoundException;
import com.example.coffeemachine.model.Beverage;

import java.util.Map;

public class BeverageService {
    private Map<String, Beverage> beverageMap;

    public BeverageService(Map<String, Beverage> beverageMap) {
        this.beverageMap = beverageMap;
    }

    public Beverage getBeverage(String name) {
        if (beverageMap.containsKey(name)) {
            return beverageMap.get(name);
        }
        throw new NotFoundException("Beverage in data");
    }
}
