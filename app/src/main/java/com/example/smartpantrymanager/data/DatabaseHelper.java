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
        //create pantry table
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT, " +
                "expiry_date TEXT)");

        //create recipe table
        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "preparation_steps TEXT)");

        //create ingredient table
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

    //adding item to table
    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", item.getName());
        values.put("quantity", item.getQuantity());
        values.put("unit", item.getUnit());
        values.put("expiry_date", item.getExpiryDate());
        return db.insert(TABLE_PANTRY, null, values);
    }

    //showing items in pantry
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

    //updating pantry
    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", item.getName());
        values.put("quantity", item.getQuantity());
        values.put("unit", item.getUnit());
        values.put("expiry_date", item.getExpiryDate());
        return db.update(TABLE_PANTRY, values, "id = ?", new String[]{String.valueOf(item.getId())});
    }

    //deleting titem from pantry
    public void deletePantryItem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PANTRY, "id = ?", new String[]{String.valueOf(id)});
    }

    //example recipes
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

        long r3 = insertRecipe(db, "Grilled Lamb Chops", "1. Mix olive oil, lemon juice, salt, and garlic.\n2. Coat chops in marinade.\n3. Grill chops and serve.");
        insertRecipeIngredient(db, r3, "chops", 200, "g");
        insertRecipeIngredient(db, r3, "olive oil", 1, "teaspoon");
        insertRecipeIngredient(db, r3, "lemon juice", 50, "ml");
        insertRecipeIngredient(db, r3, "garlic", 1, "clove");
        insertRecipeIngredient(db, r3, "salt", 1, "teaspoon");


        long r4 = insertRecipe(db, "Air fryer steak", "1. Rub steak with oil\n2. Season with spices.\n3. Fry and serve.");
        insertRecipeIngredient(db, r4, "steak", 200, "g");
        insertRecipeIngredient(db, r4, "oil", 1, "teaspoon");
        insertRecipeIngredient(db, r4, "mixed spices", 50, "g");
        insertRecipeIngredient(db, r4, "garlic", 1, "clove");

        long r5 = insertRecipe(db, "Vegan pudding", "1. Add milk, sugar, and cornstarch to pot.\n2. Simmer until smooth.\n3. Mix vanilla extract and serve.");
        insertRecipeIngredient(db, r5, "almond milk", 200, "ml");
        insertRecipeIngredient(db, r5, "sugar", 5, "tablespoon");
        insertRecipeIngredient(db, r5, "cornstarch", 2, "teaspoon");
        insertRecipeIngredient(db, r5, "vanilla extract", 1, "teaspoon");

        long r6 = insertRecipe(db, "Roasted chickpeas", "1. Mix chickpeas with olive oil and mixed spices.\n2. Roast chickpeas.\n3. Cool chickpeas and serve.");
        insertRecipeIngredient(db, r6, "chickpeas", 200, "g");
        insertRecipeIngredient(db, r6, "olive oil", 1, "teaspoon");
        insertRecipeIngredient(db, r6, "mixed spices", 1, "teaspoon");

        long r7 = insertRecipe(db, "Cheese rolls", "1. Fill cheese into wrappers.\n2. Roll the wrappers.\n3. Fry and serve.");
        insertRecipeIngredient(db, r7, "cheese", 200, "g");
        insertRecipeIngredient(db, r7, "pastry wrappers", 10, "units");

        long r8 = insertRecipe(db, "Cauliflower Tots", "1. Mix egg, cheese, mixed spices with minced cauliflower.\n2. Shape and coat with breadcrumbs.\n3. Fry and serve.");
        insertRecipeIngredient(db, r8, "cauliflower", 1, "units");
        insertRecipeIngredient(db, r8, "cheese", 200, "g");
        insertRecipeIngredient(db, r8, "egg", 1, "unit");
        insertRecipeIngredient(db, r8, "breadcrumbs", 200, "g");
        insertRecipeIngredient(db, r8, "mixed spices", 1, "tablespoon");

        long r9 = insertRecipe(db, "Mac and cheese", "1. Boil pasta with salt and olive oil.\n2. Drain water from pasta.\n3. Serve with greated cheese.");
        insertRecipeIngredient(db, r9, "macaroni", 200, "g");
        insertRecipeIngredient(db, r9, "olive oil", 1, "teaspoon");
        insertRecipeIngredient(db, r9, "cheese", 50, "g");
        insertRecipeIngredient(db, r9, "salt", 1, "teaspoon");

        long r10 = insertRecipe(db, "Movie style popcorn", "1. Add oil to pot and pop popcorn.\n2. Add salt once popped.");
        insertRecipeIngredient(db, r10, "popcorn seeds", 50, "g");
        insertRecipeIngredient(db, r10, "olive oil", 1, "teaspoon");
        insertRecipeIngredient(db, r10, "salt", 1, "teaspoon");

        long r11 = insertRecipe(db, "Mashed potatoes", "1. Boil potatoes.\n2. Add butter and spices.\n3. Mash until soft and serve.");
        insertRecipeIngredient(db, r11, "potatoes", 200, "g");
        insertRecipeIngredient(db, r11, "butter", 100, "g");
        insertRecipeIngredient(db, r11, "Mixed spices", 50, "g");

        long r12 = insertRecipe(db, "Sausage rolls", "1. Fry sausages.\n2. Spread tomato sauce on roll.\n3. Add sausage inside roll and serve.");
        insertRecipeIngredient(db, r12, "sausages", 200, "g");
        insertRecipeIngredient(db, r12, "olive oil", 1, "teaspoon");
        insertRecipeIngredient(db, r12, "tomato sauce", 50, "ml");
        insertRecipeIngredient(db, r12, "roll", 1, "unit");

        long r13 = insertRecipe(db, "Chocolate covered strawberries", "1. Melt chocolate.\n2. Coat strawberries in melted chocolate.\n3. Serve.");
        insertRecipeIngredient(db, r13, "chocolate", 200, "g");
        insertRecipeIngredient(db, r13, "strawberries ", 10, "unit");

        long r14 = insertRecipe(db, "Roti", "1. Mix olive oil, water, salt, and flour.\n2. Roll roti's.\n3. Toast and serve.");
        insertRecipeIngredient(db, r14, "flour", 200, "g");
        insertRecipeIngredient(db, r14, "olive oil", 1, "teaspoon");
        insertRecipeIngredient(db, r14, "water", 50, "ml");
        insertRecipeIngredient(db, r14, "salt", 1, "teaspoon");

        long r15 = insertRecipe(db, "Chips", "1. Cut potato's into wedges.\n2. Fry potato wedges.\n3. Add salt and serve.");
        insertRecipeIngredient(db, r15, "potato", 200, "g");
        insertRecipeIngredient(db, r15, "olive oil", 1, "l");
        insertRecipeIngredient(db, r15, "salt", 1, "teaspoon");

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