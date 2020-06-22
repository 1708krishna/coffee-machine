package com.example.coffeemachine.service;

import com.example.coffeemachine.exceptions.NotAllowedException;
import com.example.coffeemachine.exceptions.NotFoundException;
import com.example.coffeemachine.model.Ingredient;

import java.util.HashMap;
import java.util.Map;

public class InventoryService {

    private Map<String, Ingredient> ingredientInventoryMap;

    public InventoryService(Map<String, Ingredient> ingredientInventoryMap) {
        this.ingredientInventoryMap = ingredientInventoryMap;
    }

    public void deduct(String name, Integer qty) {
        if (ingredientInventoryMap.containsKey(name)) {
            Ingredient ingredient = ingredientInventoryMap.get(name);

            // keeping thread safe
            synchronized (ingredient) {
                Integer remainingQuantity = ingredient.getReservedQuantity() - qty;
                if (remainingQuantity < 0) {
                    throw new NotAllowedException("Required qty of ingredients is not present");
                }
                ingredient.setReservedQuantity(remainingQuantity);
            }
            return;
        }
        throw new NotFoundException("Ingredient");
    }

    public void reserve(String name, Integer qty) {
        if (ingredientInventoryMap.containsKey(name)) {
            Ingredient ingredient = ingredientInventoryMap.get(name);

            // keeping thread safe
            synchronized (ingredient) {
                Integer remainingQuantity = ingredient.getBalanceQuantity() - qty;
                if (remainingQuantity < 0) {
                    throw new NotAllowedException("Required qty of ingredients is not present");
                }
                ingredient.setReservedQuantity(ingredient.getReservedQuantity() + qty);
                ingredient.setBalanceQuantity(remainingQuantity);
            }
            return;
        }
        throw new NotFoundException("Ingredient " + name);
    }

//    /**
//     * add quantity to ingredients
//     * @param name
//     * @param qty
//     */
//    public void addIngredientQuantity(String name, Integer qty) {
//        if (ingredientInventoryMap.containsKey(name)) {
//            Ingredient ingredient = ingredientInventoryMap.get(name);
//
//            // keeping thread safe
//            synchronized (ingredient) {
//                ingredient.setBalanceQuantity(ingredient.getBalanceQuantity() + qty);
//            }
//            return;
//        }
//        throw new NotFoundException("Ingredient");
//    }

    /**
     * get all ingredients by name and qty
     * @return
     */
    public Map<String, Integer> getAllIngredientQuantity() {
        Map<String, Integer> ingredientQuantityMap = new HashMap<>();
        for (String key: this.ingredientInventoryMap.keySet()) {
            ingredientQuantityMap.put(key, this.ingredientInventoryMap.get(key).getBalanceQuantity()
                    + this.ingredientInventoryMap.get(key).getReservedQuantity());
        }
        return ingredientQuantityMap;
    }

    public Integer getAvailableQuantity(String name) {
        if (ingredientInventoryMap.containsKey(name)) {
            return ingredientInventoryMap.get(name).getBalanceQuantity();
        }
        throw new NotFoundException("Ingredient with name");
    }
}
