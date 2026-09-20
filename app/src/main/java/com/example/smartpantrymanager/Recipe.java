package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private int id;
    private String name;
    private String preparationSteps;
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    public Recipe(int id, String name, String preparationSteps) {
        this.id = id;
        this.name = name;
        this.preparationSteps = preparationSteps;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPreparationSteps() { return preparationSteps; }
    public List<RecipeIngredient> getIngredients() { return ingredients; }
    public void addIngredient(RecipeIngredient ingredient) { ingredients.add(ingredient); }
}