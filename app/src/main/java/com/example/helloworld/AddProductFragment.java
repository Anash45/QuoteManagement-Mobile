package com.example.helloworld;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
public class AddProductFragment extends Fragment {

    private static final String TAG = "AddProductFragment";

    private EditText editTextProductName;
    private EditText editTextDescription;
    private EditText editTextPrice;
    private Spinner spinnerCategory;
    private Button buttonDatePicker;
    private RadioGroup radioGroupCondition;
    private RadioButton radioButtonNew;
    private RadioButton radioButtonUsed;
    private Button buttonSubmit;
    private String selectedDate = "";
    private int day, month, year;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        editTextProductName = view.findViewById(R.id.editTextProductName);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        buttonDatePicker = view.findViewById(R.id.buttonDatePicker);
        radioGroupCondition = view.findViewById(R.id.radioGroupCondition);
        radioButtonNew = view.findViewById(R.id.radioButtonNew);
        radioButtonUsed = view.findViewById(R.id.radioButtonUsed);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);

        // Set up Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.product_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Set up DatePicker button
        buttonDatePicker.setOnClickListener(v -> showDatePickerDialog());

        buttonSubmit.setOnClickListener(v -> validateAndSubmit());
    }



    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = String.format("%d-%d-%d", selectedDay, selectedMonth + 1, selectedYear);
                    buttonDatePicker.setText(getString(R.string.date_selected, selectedDate));
                }, year, month, day);

        datePickerDialog.show();
    }

    private void validateAndSubmit() {
        String productName = editTextProductName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        int categoryPosition = spinnerCategory.getSelectedItemPosition();
        int selectedConditionId = radioGroupCondition.getCheckedRadioButtonId();

        if (productName.isEmpty() || description.isEmpty() || priceStr.isEmpty() || selectedDate.isEmpty()) {
            Toast.makeText(getContext(), R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), R.string.invalid_price_format, Toast.LENGTH_SHORT).show();
            return;
        }

        if (categoryPosition == 0) {
            Toast.makeText(getContext(), R.string.select_category, Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedConditionId == -1) {
            Toast.makeText(getContext(), R.string.select_condition, Toast.LENGTH_SHORT).show();
            return;
        }

        // Tax calculation
        double taxRate = (radioButtonNew.isChecked()) ? 0.15 : 0.0;
        double finalPrice = price + (price * taxRate);

        // Get condition text
        String conditionText = radioButtonNew.isChecked() ? getString(R.string.newStr) : getString(R.string.used);

        // Generate a unique ID for the product
        String productId = UUID.randomUUID().toString();

        int quantity = 1;
        // Create and save the product
        Product product = new Product(productId, productName, description, priceStr, spinnerCategory.getSelectedItem().toString(), selectedDate, conditionText, quantity);
        saveProductToSharedPreferences(product);

        // Show result message
        String message = String.format(getString(R.string.product_added),
                productName, description, price, spinnerCategory.getSelectedItem().toString(),
                day, month + 1, year, conditionText, finalPrice);

        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

        // Clear input fields
        clearInputFields();
    }

    private void saveProductToSharedPreferences(Product product) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("ProductPrefs", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        List<Product> products = getProductsFromSharedPreferences();

        // Ensure products list is not null
        if (products == null) {
            products = new ArrayList<>();
        }

        // Debugging: Print current products and the new product ID
        Log.d("AddProductFragment", "Saving product with ID: " + product.getId());
        for (Product p : products) {
            Log.d("AddProductFragment", "Existing product ID: " + p.getId());
        }

        // Remove existing product with the same ID
        if (product.getId() != null) {
            products.removeIf(p -> p.getId() != null && p.getId().equals(product.getId()));
        }

        // Add the new product
        products.add(product);
        editor.putString("products", serializeProducts(products));
        editor.apply();
    }

    private String serializeProducts(List<Product> products) {
        Gson gson = new Gson();
        return gson.toJson(products);
    }

    private List<Product> deserializeProducts(String serializedProducts) {
        Gson gson = new Gson();
        Type productListType = new TypeToken<List<Product>>() {}.getType();
        return gson.fromJson(serializedProducts, productListType);
    }

    private List<Product> getProductsFromSharedPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("ProductPrefs", getContext().MODE_PRIVATE);
        String serializedProducts = sharedPreferences.getString("products", "");
        return deserializeProducts(serializedProducts);
    }


    private void clearInputFields() {
        editTextProductName.setText("");
        editTextDescription.setText("");
        editTextPrice.setText("");
        spinnerCategory.setSelection(0); // Set to default category
        buttonDatePicker.setText(R.string.select_date);
        radioGroupCondition.clearCheck();
    }
}
