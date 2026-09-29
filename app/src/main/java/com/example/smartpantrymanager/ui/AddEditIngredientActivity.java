package com.example.smartpantrymanager.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.data.DatabaseHelper;
import com.example.smartpantrymanager.PantryItem;
import java.util.Calendar;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";

    private DatabaseHelper dbHelper;
    private EditText editName, editQuantity, editExpiryDate;
    private TextView errorName, errorQuantity;
    private Spinner spinnerUnit;

    private int editingItemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        dbHelper = new DatabaseHelper(this);
        editName = findViewById(R.id.editName); //connect java to XML
        editQuantity = findViewById(R.id.editQuantity);
        editExpiryDate = findViewById(R.id.editExpiryDate);
        errorName = findViewById(R.id.errorName);
        errorQuantity = findViewById(R.id.errorQuantity);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        Button buttonSave = findViewById(R.id.buttonSave);

        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                this, R.array.units_array, android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        editExpiryDate.setOnClickListener(v -> showDatePicker());

        editingItemId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);
        if (editingItemId != -1) {
            setTitle("Edit Ingredient");
            prefillForEdit(editingItemId);
        } else {
            setTitle("Add Ingredient");
        }

        buttonSave.setOnClickListener(v -> onSaveClicked());
    }

    private void prefillForEdit(int itemId) {
        for (PantryItem item : dbHelper.getAllPantryItems()) {
            if (item.getId() == itemId) {
                editName.setText(item.getName());
                editQuantity.setText(String.valueOf(item.getQuantity()));
                editExpiryDate.setText(item.getExpiryDate());

                @SuppressWarnings("unchecked")
                ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerUnit.getAdapter();
                int position = adapter.getPosition(item.getUnit());
                if (position >= 0) spinnerUnit.setSelection(position);
                break;
            }
        }
    }

    private void showDatePicker() {//brings a calender
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(),
                            "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    editExpiryDate.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    //save updates
    private void onSaveClicked() {
        boolean isValid = true;

        String name = editName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            errorName.setText("Name is required");
            errorName.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorName.setVisibility(View.GONE);
        }

        //validation
        String quantityText = editQuantity.getText().toString().trim();
        double quantity = 0;
        if (TextUtils.isEmpty(quantityText)) {
            errorQuantity.setText("Quantity is required");
            errorQuantity.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            try {
                quantity = Double.parseDouble(quantityText);
                if (quantity <= 0) {
                    errorQuantity.setText("Quantity must be greater than 0");
                    errorQuantity.setVisibility(View.VISIBLE);
                    isValid = false;
                } else {
                    errorQuantity.setVisibility(View.GONE);
                }
            } catch (NumberFormatException e) {
                errorQuantity.setText("Enter a valid number");
                errorQuantity.setVisibility(View.VISIBLE);
                isValid = false;
            }
        }

        if (!isValid) return;

        String unit = spinnerUnit.getSelectedItem().toString();
        String expiryDate = editExpiryDate.getText().toString().trim();
        if (expiryDate.isEmpty()) expiryDate = null;

        if (editingItemId != -1) {
            PantryItem updated = new PantryItem(editingItemId, name, quantity, unit, expiryDate);
            dbHelper.updatePantryItem(updated);
        } else {
            PantryItem newItem = new PantryItem(name, quantity, unit, expiryDate);
            dbHelper.addPantryItem(newItem);
        }

        finish(); //closes screen
    }
}