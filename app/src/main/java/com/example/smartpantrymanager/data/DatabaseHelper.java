package com.example.smartpantrymanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantrymanager.PantryItem;
import com.example.smartpantrymanager.Recipe;
import com.example.smartpantrymanager.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_PANTRY = "pantry_items";
    private static final String TABLE_RECIPES = "recipes";
    private static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static String normalizeIngredientName(String name) {
        if (name == null) return "";
        String result = name.trim().toLowerCase();
        if (result.endsWith("es")) {
            result = result.substring(0, result.length() - 2);
        } else if (result.endsWith("s") && !result.endsWith("ss")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT, " +
                "expiry_date TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "preparation_steps TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER NOT NULL, " +
                "ingredient_name TEXT NOT NULL, " +
                "required_quantity REAL NOT NULL, " +
                "unit TEXT, " +
                "FOREIGN KEY(recipe_id) REFERENCES " + TABLE_RECIPES + "(id))");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){}

    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", item.getName());
        values.put("quantity", item.getQuantity());
        values.put("unit", item.getUnit());
        values.put("expiry_date", item.getExpiryDate());
        return db.insert(TABLE_PANTRY, null, values);
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null, "name ASC");
        while (cursor.moveToNext()) {
            items.add(new PantryItem(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                    cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                    cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"))
            ));
        }
        cursor.close();
        return items;
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", item.getName());
        values.put("quantity", item.getQuantity());
        values.put("unit", item.getUnit());
        values.put("expiry_date", item.getExpiryDate());
        return db.update(TABLE_PANTRY, values, "id = ?", new String[]{String.valueOf(item.getId())});
    }

    public void deletePantryItem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PANTRY, "id = ?", new String[]{String.valueOf(id)});
    }

    private void seedRecipes(SQLiteDatabase db) {
        long r1 = insertRecipe(db, "Scrambled Eggs", "1. Whisk eggs.\n2. Melt butter in pan, cook eggs on low heat.\n3. Add salt and pepper.\n4. Serve");
        insertRecipeIngredient(db, r1, "eggs", 2, "unit");
        insertRecipeIngredient(db, r1, "bread", 2, "slice");
        insertRecipeIngredient(db, r1, "butter", 10, "g");
        insertRecipeIngredient(db, r1, "salt", 1, "teaspoon");
        insertRecipeIngredient(db, r1, "pepper", 1, "teaspoon");


        long r2 = insertRecipe(db, "Tomato Pasta", "1. Boil pasta and add salt until al dente.\n2. Heat tomato sauce with garlic.\n3. Combine and serve.");
        insertRecipeIngredient(db, r2, "pasta", 200, "g");
        insertRecipeIngredient(db, r2, "salt", 1, "teaspoon");
        insertRecipeIngredient(db, r2, "tomato", 3, "unit");
        insertRecipeIngredient(db, r2, "garlic", 1, "clove");

        //add more recipes
    }

    private long insertRecipe(SQLiteDatabase db, String name, String steps) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("preparation_steps", steps);
        return db.insert(TABLE_RECIPES, null, values);
    }

    private void insertRecipeIngredient(SQLiteDatabase db, long recipeId, String ingredientName, double quantity, String unit) {
        ContentValues values = new ContentValues();
        values.put("recipe_id", recipeId);
        values.put("ingredient_name", ingredientName);
        values.put("required_quantity", quantity);
        values.put("unit", unit);
        db.insert(TABLE_RECIPE_INGREDIENTS, null, values);
    }

    public List<Recipe> getAllRecipesWithIngredients() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor recipeCursor = db.query(TABLE_RECIPES, null, null, null, null, null, "name ASC");
        while (recipeCursor.moveToNext()) {
            int recipeId = recipeCursor.getInt(recipeCursor.getColumnIndexOrThrow("id"));
            Recipe recipe = new Recipe(
                    recipeId,
                    recipeCursor.getString(recipeCursor.getColumnIndexOrThrow("name")),
                    recipeCursor.getString(recipeCursor.getColumnIndexOrThrow("preparation_steps"))
            );

            Cursor ingredientCursor = db.query(TABLE_RECIPE_INGREDIENTS, null,
                    "recipe_id = ?", new String[]{String.valueOf(recipeId)}, null, null, null);
            while (ingredientCursor.moveToNext()) {
                recipe.addIngredient(new RecipeIngredient(
                        ingredientCursor.getInt(ingredientCursor.getColumnIndexOrThrow("id")),
                        recipeId,
                        ingredientCursor.getString(ingredientCursor.getColumnIndexOrThrow("ingredient_name")),
                        ingredientCursor.getDouble(ingredientCursor.getColumnIndexOrThrow("required_quantity")),
                        ingredientCursor.getString(ingredientCursor.getColumnIndexOrThrow("unit"))
                ));
            }
            ingredientCursor.close();
            recipes.add(recipe);
        }
        recipeCursor.close();
        return recipes;
    }

}