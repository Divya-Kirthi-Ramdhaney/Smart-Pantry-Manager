package com.example.smartpantrymanager.ui;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.data.DatabaseHelper;
import com.example.smartpantrymanager.Recipe;
import com.example.smartpantrymanager.RecipeIngredient;

public class RecipeDetailActivity extends AppCompatActivity {
    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        Recipe found = null;
        for (Recipe r : dbHelper.getAllRecipesWithIngredients()) {
            if (r.getId() == recipeId) {
                found = r;
                break;
            }
        }

        TextView textName = findViewById(R.id.textDetailName);
        TextView textIngredients = findViewById(R.id.textDetailIngredients);
        TextView textSteps = findViewById(R.id.textDetailSteps);

        if (found != null) {
            setTitle(found.getName());
            textName.setText(found.getName());

            StringBuilder ingredientsText = new StringBuilder();
            for (RecipeIngredient ing : found.getIngredients()) {
                ingredientsText.append("• ")
                        .append(ing.getRequiredQuantity()).append(" ")
                        .append(ing.getUnit()).append(" ")
                        .append(ing.getIngredientName())
                        .append("\n");
            }
            textIngredients.setText(ingredientsText.toString());
            textSteps.setText(found.getPreparationSteps());
        } else {
            textName.setText("Recipe not found");
        }
    }
}