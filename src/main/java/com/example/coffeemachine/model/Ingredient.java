package com.example.coffeemachine.model;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class Ingredient {
    private String name;
    private Integer reservedQuantity;
    private Integer balanceQuantity;
}
