package com.example.coffeemachine.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data @Builder
public class Beverage {
    private String name;
    private Map<String, Integer> ingredientToQuantityMap;
}
