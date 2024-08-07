package com.example.helloworld;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment {

    private RecyclerView recyclerViewProducts;
    private ProductsAdapter productsAdapter; // Class-level field
    private LinearLayout noProductsLayout;
    private TextView noProductsText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        noProductsLayout = view.findViewById(R.id.noProductsLayout);
        noProductsText = view.findViewById(R.id.noProductsText);

        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Product> products = getProductsFromSharedPreferences();
        if (products == null || products.isEmpty()) {
            noProductsLayout.setVisibility(View.VISIBLE);
            recyclerViewProducts.setVisibility(View.GONE);
        } else {
            noProductsLayout.setVisibility(View.GONE);
            recyclerViewProducts.setVisibility(View.VISIBLE);

            // Initialize the class-level productsAdapter
            productsAdapter = new ProductsAdapter(
                    requireContext(), // Pass context
                    products,
                    this::showDeleteConfirmationDialog,
                    this::openEditProductFragment
            );
            recyclerViewProducts.setAdapter(productsAdapter);
        }
    }

    private List<Product> getProductsFromSharedPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("ProductPrefs", getContext().MODE_PRIVATE);
        String serializedProducts = sharedPreferences.getString("products", "[]");
        Type productListType = new TypeToken<List<Product>>() {}.getType();
        return new Gson().fromJson(serializedProducts, productListType);
    }

    private void saveProductsToSharedPreferences(List<Product> products) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("ProductPrefs", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String serializedProducts = new Gson().toJson(products);
        editor.putString("products", serializedProducts);
        editor.apply();
    }

    private void showDeleteConfirmationDialog(Product product) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteProduct(product);
                    List<Product> updatedProducts = getProductsFromSharedPreferences();

                    // Check if productsAdapter is not null before updating
                    if (productsAdapter != null) {
                        productsAdapter.updateProducts(updatedProducts);
                    } else {
                        Log.d("ProductsFragment", "ProductsAdapter is null");
                    }

                    if (updatedProducts.isEmpty()) {
                        noProductsLayout.setVisibility(View.VISIBLE);
                        recyclerViewProducts.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void openEditProductFragment(Product product) {
        // Serialize the Product object to a JSON string
        String serializedProduct = new Gson().toJson(product);

        // Create a Bundle and put the serialized product string
        Bundle bundle = new Bundle();
        bundle.putString("productJson", serializedProduct); // Use putString instead of putSerializable

        // Create and set up the EditProductFragment
        EditProductFragment editProductFragment = new EditProductFragment();
        editProductFragment.setArguments(bundle);

        // Replace or add the fragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editProductFragment) // Adjust the container ID as needed
                .addToBackStack(null)
                .commit();
    }

    private void deleteProduct(Product product) {
        List<Product> products = getProductsFromSharedPreferences();
        if (products != null) {
            products.removeIf(p -> p.getId() != null && p.getId().equals(product.getId())); // Check if product ID is not null
            saveProductsToSharedPreferences(products);
        }
    }
}
