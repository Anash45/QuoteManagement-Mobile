package com.example.helloworld;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Product> cartProducts;
    private final BiConsumer<Product, Integer> onQuantityChanged;
    private final Consumer<Product> onRemoveClicked;

    public CartAdapter(List<Product> cartProducts, BiConsumer<Product, Integer> onQuantityChanged, Consumer<Product> onRemoveClicked) {
        this.cartProducts = cartProducts;
        this.onQuantityChanged = onQuantityChanged;
        this.onRemoveClicked = onRemoveClicked;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartProducts.get(position);

        // Set item details
        holder.textViewName.setText(product.getName());
        holder.textViewPrice.setText(String.format("$%.2f", Double.parseDouble(product.getPrice())));

        // Initialize quantity to 1 and calculate initial values
        holder.editTextQuantity.setText(String.valueOf(product.getQuantity()));

        double price = Double.parseDouble(product.getPrice());
        int quantity = product.getQuantity();
        double tax = product.isNew() ? price * 0.15 : 0;
        double itemTotal = (price + tax) * quantity;

        holder.textViewTax.setText(String.format("Tax: $%.2f", tax));
        holder.textViewItemTotal.setText(String.format("Total: $%.2f", itemTotal));

        // Add TextWatcher to handle quantity changes
        holder.editTextQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String quantityText = s.toString();
                int quantity;
                try {
                    quantity = Integer.parseInt(quantityText);
                } catch (NumberFormatException e) {
                    quantity = 1; // Default to 1 if invalid input
                    holder.editTextQuantity.setText("1");
                }

                double newItemTotal = (price + tax) * quantity;
                holder.textViewItemTotal.setText(String.format("Total: $%.2f", newItemTotal));
                onQuantityChanged.accept(product, quantity);
            }
        });

        // Remove button click listener
        holder.buttonRemove.setOnClickListener(v -> onRemoveClicked.accept(product));
    }

    @Override
    public int getItemCount() {
        return cartProducts.size();
    }

    public void updateProducts(List<Product> newProducts) {
        this.cartProducts = newProducts;
        notifyDataSetChanged();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewPrice;
        TextView textViewCondition;
        TextView textViewTax;
        TextView textViewItemTotal;
        EditText editTextQuantity;
        ImageButton buttonRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewCondition = itemView.findViewById(R.id.textViewCondition);
            textViewTax = itemView.findViewById(R.id.textViewTax);
            textViewItemTotal = itemView.findViewById(R.id.textViewItemTotal);
            editTextQuantity = itemView.findViewById(R.id.editTextQuantity);
            buttonRemove = itemView.findViewById(R.id.buttonRemove);
        }
    }
}
