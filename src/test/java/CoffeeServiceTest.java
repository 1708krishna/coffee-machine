import com.example.coffeemachine.model.Beverage;
import com.example.coffeemachine.model.Ingredient;
import com.example.coffeemachine.service.BeverageService;
import com.example.coffeemachine.service.CoffeeService;
import com.example.coffeemachine.service.InventoryService;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CoffeeServiceTest {

    private CoffeeService coffeeService;

    private InventoryService inventoryService;

    private BeverageService beverageService;

    @Before
    public void setUp() {
        List<Ingredient> ingredientList = new ArrayList<>();
        ingredientList.add(getIngredient("water", 500));
        ingredientList.add(getIngredient("milk", 500));
        ingredientList.add(getIngredient("ginger syrup", 300));
        ingredientList.add(getIngredient("lemon syrup", 300));
        ingredientList.add(getIngredient("sugar", 200));
        ingredientList.add(getIngredient("salt", 10));

        inventoryService = new InventoryService(getIngredientMap(ingredientList));

        List<Beverage> beverageList = new ArrayList<>();
        beverageList.add(getBeverage("hot water", new String[]{"water"}, new Integer[]{50}));
        beverageList.add(getBeverage("hot milk", new String[]{"milk"}, new Integer[]{50}));
        beverageList.add(getBeverage("ginger tea", new String[]{"milk","water", "ginger syrup", "sugar"},
                                     new Integer[]{20,20,10,10}));
        beverageList.add(getBeverage("lemon tea", new String[]{"water", "lemon syrup", "sugar"},
                                     new Integer[]{30,10,10}));

        beverageService = new BeverageService(getBeverageMap(beverageList));

    }

    @Test
    public void getAllIngredientsWithLowQtyTest() {
        coffeeService = CoffeeService.getInstance(inventoryService, beverageService, 10);
        Map<String, Integer> lowQtyIngredients = coffeeService.getAllIngredientsWithLowQuantity();

        System.out.println("Ingredients with remaining quantity");
        for (String name : lowQtyIngredients.keySet()) {
            System.out.println(name + " : " + lowQtyIngredients.get(name));
        }
    }

    @Test
    public void shouldServeNParallelBeveragesTest() throws InterruptedException {
        final int numberOfOutlets = 5;
        coffeeService = CoffeeService.getInstance(inventoryService, beverageService, numberOfOutlets);
        ExecutorService service = Executors.newFixedThreadPool(numberOfOutlets);
        CountDownLatch latch = new CountDownLatch(numberOfOutlets);
        for (int i = 0; i < numberOfOutlets; i++) {
            service.execute(() -> {
                try {
                    coffeeService.makeBeverage("ginger tea");
                    latch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        latch.await();

        Map<String, Integer> ingredientsWithRemainingQuantity = inventoryService.getAllIngredientQuantity();
        System.out.println("Ingredients with remaining quantity");
        for (String name : ingredientsWithRemainingQuantity.keySet()) {
            System.out.println(name + " : " + ingredientsWithRemainingQuantity.get(name));
        }
    }

    @Test
    public void handleWhenMoreThanNParallelBeveragesRequested() throws InterruptedException {
        final int numberOfOutlets = 5;
        final int threadCount = 10;
        coffeeService = CoffeeService.getInstance(inventoryService, beverageService, numberOfOutlets);
        ExecutorService service = Executors.newFixedThreadPool(numberOfOutlets);
        for (int i = 0; i < threadCount ; i++) {
            service.execute(() -> {
                try {
                    coffeeService.makeBeverage("ginger tea");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        service.shutdown();
        service.awaitTermination(60, TimeUnit.SECONDS);

        Map<String, Integer> ingredientsWithRemainingQuantity = inventoryService.getAllIngredientQuantity();
        System.out.println("Ingredients with remaining quantity");
        for (String name : ingredientsWithRemainingQuantity.keySet()) {
            System.out.println(name + " : " + ingredientsWithRemainingQuantity.get(name));
        }
    }



    /**
     * =========== BELOW THIS ALL ARE SETUP FUNCTIONS ==============
     */

    private Ingredient getIngredient(String name, Integer qty) {
        return Ingredient.builder().name(name).reservedQuantity(0).balanceQuantity(qty).build();
    }

    private Beverage getBeverage(String name, String[] ingredientNames, Integer[] qty) {
        Map<String, Integer> ingredientToQtyMap = new HashMap<>();
        for (int i = 0; i < ingredientNames.length; i++) {
            ingredientToQtyMap.put(ingredientNames[i], qty[i]);
        }
        return Beverage.builder().name(name).ingredientToQuantityMap(ingredientToQtyMap).build();
    }

    private Map<String, Ingredient> getIngredientMap(List<Ingredient> ingredients) {
        Map<String, Ingredient> ingredientMap = new HashMap<>();
        for (Ingredient ingredient : ingredients) {
            ingredientMap.put(ingredient.getName(), ingredient);
        }
        return ingredientMap;
    }

    private Map<String, Beverage> getBeverageMap(List<Beverage> beverages) {
        Map<String, Beverage> beverageMap = new HashMap<>();
        for (Beverage beverage : beverages) {
            beverageMap.put(beverage.getName(), beverage);
        }
        return beverageMap;
    }
}
