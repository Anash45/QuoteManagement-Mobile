package com.example.helloworld;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<Product> cartProducts;
    private TextView textViewTotalWithoutTax;
    private TextView textViewTotalWithTax;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCart);
        textViewTotalWithoutTax = view.findViewById(R.id.textViewTotalWithoutTax);
        textViewTotalWithTax = view.findViewById(R.id.textViewTotalWithTax);

        // Initialize RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartProducts = getCartProducts();
        cartAdapter = new CartAdapter(cartProducts, this::onQuantityChanged, this::onRemoveClicked);
        recyclerView.setAdapter(cartAdapter);

        // Calculate totals
        calculateTotals();

        return view;
    }

    private List<Product> getCartProducts() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CartPrefs", Context.MODE_PRIVATE);
        String cartJson = sharedPreferences.getString("cart", "[]");

        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> productIds = new Gson().fromJson(cartJson, type);

        if (productIds == null) {
            productIds = new ArrayList<>();
        }

        List<Product> products = new ArrayList<>();
        for (String productId : productIds) {
            Product product = getProductById(productId);
            if (product != null) {
                products.add(product);
            }
        }

        return products;
    }

    private Product getProductById(String id) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("ProductPrefs", Context.MODE_PRIVATE);
        String productsJson = sharedPreferences.getString("products", "[]");

        Type type = new TypeToken<List<Product>>() {}.getType();
        List<Product> products = new Gson().fromJson(productsJson, type);

        if (products == null) {
            products = new ArrayList<>();
        }

        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }

        return null;
    }

    private void onQuantityChanged(Product product, int newQuantity) {
        product.setQuantity(newQuantity);
        calculateTotals();
    }

    private void onRemoveClicked(Product product) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CartPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String cartJson = sharedPreferences.getString("cart", "[]");
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> cartItems = new Gson().fromJson(cartJson, type);

        if (cartItems != null) {
            cartItems.remove(product.getId());
            String updatedCartJson = new Gson().toJson(cartItems);
            editor.putString("cart", updatedCartJson);
            editor.apply();
        }

        cartProducts.remove(product);
        cartAdapter.updateProducts(cartProducts);
        calculateTotals();
    }

    private void calculateTotals() {
        double totalWithoutTax = 0;
        double totalWithTax = 0;

        for (Product product : cartProducts) {
            int quantity = product.getQuantity(); // Get the updated quantity
            double price = Double.parseDouble(product.getPrice());
            double itemTotal = price * quantity;

            totalWithoutTax += itemTotal;
            double tax = product.isNew() ? itemTotal * 0.15 : 0;
            totalWithTax += itemTotal + tax;

            Log.d("CartFragment", "Product: " + product.getName() +
                    ", Quantity: " + quantity +
                    ", Price: $" + price +
                    ", Tax: $" + tax +
                    ", Item Total: $" + (itemTotal + tax));
        }

        Log.d("CartFragment", "Total without tax: $" + totalWithoutTax);
        Log.d("CartFragment", "Total with tax: $" + totalWithTax);

        if (textViewTotalWithoutTax != null && textViewTotalWithTax != null) {
            textViewTotalWithoutTax.setText(String.format("Total without tax: $%.2f", totalWithoutTax));
            textViewTotalWithTax.setText(String.format("Total with tax: $%.2f", totalWithTax));
        } else {
            Log.e("CartFragment", "TextViews for totals are null");
        }
    }
}


