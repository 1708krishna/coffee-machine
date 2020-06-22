package com.example.coffeemachine.service;


import com.example.coffeemachine.exceptions.NotAllowedException;
import com.example.coffeemachine.model.Beverage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CoffeeService {

    private InventoryService inventoryService;
    private OutletService outletService;
    private BeverageService beverageService;

    private Random random;

    private Object checkRequiredQuantityAndReserveLock = new Object();
    public static final Integer LOW_QTY_MARK = 50;
    private static CoffeeService INSTANCE;

    public static CoffeeService getInstance(InventoryService inventoryService, BeverageService beverageService, Integer numberOfOutlets) {
        if (INSTANCE == null) {
            synchronized (CoffeeService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CoffeeService(inventoryService, beverageService, numberOfOutlets);
                }
            }
        }
        return INSTANCE;
    }

    private CoffeeService(InventoryService inventoryService, BeverageService beverageService, Integer numberOfOutlets) {
        this.inventoryService = inventoryService;
        this.beverageService = beverageService;
        this.outletService = new OutletService(numberOfOutlets);
        this.random = new Random();
    }

    public void makeBeverage(String name) throws InterruptedException {
        outletService.lockOutlet();
        Beverage beverage = beverageService.getBeverage(name);
        Map<String, Integer> ingredientToQuantityMap = beverage.getIngredientToQuantityMap();
        synchronized (checkRequiredQuantityAndReserveLock) {

            System.out.println(Thread.currentThread().getName() + ": Checking the ingredients quantity");

            for (String ingredientName : ingredientToQuantityMap.keySet()) {
                if (inventoryService.getAvailableQuantity(ingredientName) < ingredientToQuantityMap.get(ingredientName)) {
                    throw new NotAllowedException("Required ingredient qty is not available");
                }
            }

            System.out.println(Thread.currentThread().getName() + ": Finished checking the ingredients quantity");

            /**
             * reaching to this step means, required qty is available
             * now, reserve the qty for making the coffee
             */
            for (String ingredientName : ingredientToQuantityMap.keySet()) {
                inventoryService.reserve(ingredientName, ingredientToQuantityMap.get(ingredientName));
            }

            System.out.println(Thread.currentThread().getName() + ": Reserved the ingredients quantity");
        }

        /**
         * as preparing the coffee is time taking process, it can be done separately without taking the lock
         * as it has already reserved the qty
         * Generated random multiplier to have separate preparation time for each beverage
          */
        System.out.println(Thread.currentThread().getName() + ": Preparing the beverage");
        for (String ingredientName : ingredientToQuantityMap.keySet()) {
            Thread.sleep(100 * random.nextInt(10));
            inventoryService.deduct(ingredientName, ingredientToQuantityMap.get(ingredientName));
        }

        outletService.UnlockOutlet();
        System.out.println(Thread.currentThread().getName() + ": Beverage is served successfully!!!");
    }

    public Map<String, Integer> getAllIngredientsWithLowQuantity() {
        Map<String, Integer> ingredientQuantityMap = inventoryService.getAllIngredientQuantity();
        Map<String, Integer> ingredientsWithLowQuantity = new HashMap<>();
        for (String ingredientName : ingredientQuantityMap.keySet()) {
            if (ingredientQuantityMap.get(ingredientName) < LOW_QTY_MARK) {
                ingredientsWithLowQuantity.put(ingredientName, ingredientQuantityMap.get(ingredientName));
            }
        }
        return ingredientsWithLowQuantity;
    }
}
