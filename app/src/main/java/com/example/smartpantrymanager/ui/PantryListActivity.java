package com.example.smartpantrymanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.data.DatabaseHelper;
import com.example.smartpantrymanager.PantryItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class PantryListActivity extends AppCompatActivity implements PantryAdapter.OnItemActionListener {

    private DatabaseHelper dbHelper;
    private PantryAdapter adapter;
    private TextView textEmptyPantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        dbHelper = new DatabaseHelper(this);
        textEmptyPantry = findViewById(R.id.textEmptyPantry);

        //know how to arrage rows
        RecyclerView recyclerView = findViewById(R.id.recyclerPantry);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PantryAdapter(dbHelper.getAllPantryItems(), this);
        recyclerView.setAdapter(adapter);

        //goes to next pahe to ass ingredient
        FloatingActionButton fab = findViewById(R.id.fabAddItem);
        fab.setOnClickListener(v -> {Intent intent = new Intent(PantryListActivity.this, AddEditIngredientActivity.class);
            startActivity(intent);});

        refreshList();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_pantry);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                return true; // already on this screen, do nothing
            } else if (id == R.id.nav_suggested) {
                startActivity(new Intent(this, SuggestedRecipesActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();    }

    private void refreshList() { //updates the list
        List<PantryItem> items = dbHelper.getAllPantryItems();
        adapter.updateItems(items);
        textEmptyPantry.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override //edit items
    public void onEdit(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override //delete items
    public void onDelete(PantryItem item) {
        dbHelper.deletePantryItem(item.getId());
        refreshList();
    }
}