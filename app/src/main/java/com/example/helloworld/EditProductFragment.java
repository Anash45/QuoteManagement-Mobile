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

public class EditProductFragment extends Fragment {

    private static final String TAG = "EditProductFragment";

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
    private Product productToEdit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_product, container, false);
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

        // Get product from arguments and prefill fields
        if (getArguments() != null && getArguments().getString("productJson") != null) {
            String productJson = getArguments().getString("productJson");
            productToEdit = new Gson().fromJson(productJson, Product.class);
            prefillProductDetails(productToEdit);
        }

        // Set up DatePicker button
        buttonDatePicker.setOnClickListener(v -> showDatePickerDialog());

        buttonSubmit.setOnClickListener(v -> validateAndSubmit());
    }

    private void prefillProductDetails(Product product) {
        editTextProductName.setText(product.getName());
        editTextDescription.setText(product.getDescription());
        editTextPrice.setText(product.getPrice());

        // Set Spinner selection
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerCategory.getAdapter();
        int categoryPosition = adapter.getPosition(product.getCategory());
        spinnerCategory.setSelection(categoryPosition);

        // Set Date
        selectedDate = product.getDate();
        buttonDatePicker.setText(getString(R.string.date_selected, selectedDate));

        // Set Condition
        if ("New".equals(product.getCondition())) {
            radioButtonNew.setChecked(true);
        } else {
            radioButtonUsed.setChecked(true);
        }
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

        // Get condition text as a string
        String conditionText = (selectedConditionId == R.id.radioButtonNew) ? "New" : "Used";

        // Create new Product object with updated details
        String category = (String) spinnerCategory.getItemAtPosition(categoryPosition);

        Product updatedProduct = new Product(
                productToEdit.getId(), // keep the original ID
                productName,
                description,
                String.format("%.2f", price),
                category,
                selectedDate,
                conditionText // use string "New" or "Used"
        );

        updateProductInSharedPreferences(updatedProduct);

        // Navigate back to ProductsFragment
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void updateProductInSharedPreferences(Product updatedProduct) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("ProductPrefs", getContext().MODE_PRIVATE);
        String serializedProducts = sharedPreferences.getString("products", "[]");
        Type productListType = new TypeToken<List<Product>>() {}.getType();
        List<Product> products = new Gson().fromJson(serializedProducts, productListType);

        if (products == null) {
            products = new ArrayList<>();
        }

        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(updatedProduct.getId())) {
                products.set(i, updatedProduct);
                break;
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        String updatedProductsJson = new Gson().toJson(products);
        editor.putString("products", updatedProductsJson);
        editor.apply();
    }
}
