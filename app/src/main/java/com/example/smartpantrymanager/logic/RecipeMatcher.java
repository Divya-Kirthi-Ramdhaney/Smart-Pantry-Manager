package com.example.smartpantrymanager.logic;

import com.example.smartpantrymanager.data.DatabaseHelper;
import com.example.smartpantrymanager.PantryItem;
import com.example.smartpantrymanager.Recipe;
import com.example.smartpantrymanager.RecipeIngredient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeMatcher {
    //shows recipes that match exactly
    public static List<Recipe> getSuggestedRecipes(List<Recipe> allRecipes, List<PantryItem> pantry) {
        Map<String, Double> pantryMap = buildPantryMap(pantry);
        List<Recipe> suggested = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            if (recipeIsFullyMatched(recipe, pantryMap)) {
                suggested.add(recipe);
            }
        }
        return suggested;
    }

    //shows recipes that almost match
    public static List<Recipe> getAlmostThereRecipes(List<Recipe> allRecipes, List<PantryItem> pantry) {
        Map<String, Double> pantryMap = buildPantryMap(pantry);
        List<Recipe> almostThere = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            int missingCount = countMissingIngredients(recipe, pantryMap);
            if (missingCount == 1) {
                almostThere.add(recipe);
            }
        }
        return almostThere;
    }

    private static Map<String, Double> buildPantryMap(List<PantryItem> pantry) {
        Map<String, Double> map = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = DatabaseHelper.normalizeIngredientName(item.getName());
            map.put(key, map.getOrDefault(key, 0.0) + item.getQuantity());
        }
        return map;
    }

    private static boolean recipeIsFullyMatched(Recipe recipe, Map<String, Double> pantryMap) {
        for (RecipeIngredient required : recipe.getIngredients()) {
            String key = DatabaseHelper.normalizeIngredientName(required.getIngredientName());
            Double available = pantryMap.get(key);
            android.util.Log.d("MATCH_DEBUG", recipe.getName() + " needs '" + key + "' qty="
                    + required.getRequiredQuantity() + " " + required.getUnit()
                    + " | pantry has: " + available);
            if (available == null || available < required.getRequiredQuantity()) {
                return false;
            }
        }
        return true;
    }

    private static int countMissingIngredients(Recipe recipe, Map<String, Double> pantryMap) {
        int missing = 0;
        for (RecipeIngredient required : recipe.getIngredients()) {
            String key = DatabaseHelper.normalizeIngredientName(required.getIngredientName());
            Double available = pantryMap.get(key);
            if (available == null || available < required.getRequiredQuantity()) {
                missing++;
            }
        }
        return missing;
    }
}