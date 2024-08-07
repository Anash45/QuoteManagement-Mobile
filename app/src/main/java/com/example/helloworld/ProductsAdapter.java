package com.example.helloworld;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {

    private List<Product> products;
    private final Consumer<Product> onDeleteClicked;
    private final Consumer<Product> onEditClicked;
    private final Context context; // Add Context

    public ProductsAdapter(Context context, List<Product> products, Consumer<Product> onDeleteClicked, Consumer<Product> onEditClicked) {
        this.context = context; // Initialize Context
        this.products = products;
        this.onDeleteClicked = onDeleteClicked;
        this.onEditClicked = onEditClicked;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);

        holder.textViewName.setText(product.getName());
        holder.textViewDescription.setText(product.getDescription());

        // Convert price to double for formatting
        double price = Double.parseDouble(product.getPrice());
        holder.textViewPrice.setText(String.format("$%.2f", price));

        holder.textViewCategory.setText(product.getCategory());
        holder.textViewDate.setText(product.getDate());
        holder.textViewCondition.setText(product.getCondition());

        if (product.isNew()) {
            holder.textViewTax.setText(String.format("Tax (15%%): $%.2f", price * 0.15));
        } else {
            holder.textViewTax.setText("");
        }

        holder.buttonEdit.setOnClickListener(v -> onEditClicked.accept(product));
        holder.buttonDelete.setOnClickListener(v -> onDeleteClicked.accept(product));
        holder.buttonAddToCart.setOnClickListener(v -> addToCart(product));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    private void addToCart(Product product) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Retrieve existing cart items
        String existingCart = sharedPreferences.getString("cart", "[]");
        Type listType = new TypeToken<List<String>>() {}.getType();
        List<String> cartItems = new Gson().fromJson(existingCart, listType);

        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }

        // Check if the product ID is already in the cart
        if (cartItems.contains(product.getId())) {
            // Notify user if the product is already in the cart
            Toast.makeText(context, "Product already in cart", Toast.LENGTH_SHORT).show();
        } else {
            // Add product ID to cart
            cartItems.add(product.getId());

            // Save updated cart back to SharedPreferences
            String updatedCart = new Gson().toJson(cartItems);
            editor.putString("cart", updatedCart);
            editor.apply();

            // Notify user that the product has been added
            Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT).show();
        }

    }


    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewDescription;
        TextView textViewPrice;
        TextView textViewCategory;
        TextView textViewDate;
        TextView textViewCondition;
        TextView textViewTax;
        ImageButton buttonEdit;
        ImageButton buttonDelete;
        ImageButton buttonAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewCondition = itemView.findViewById(R.id.textViewCondition);
            textViewTax = itemView.findViewById(R.id.textViewTax);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonAddToCart = itemView.findViewById(R.id.buttonAddToCart);
        }
    }
}
