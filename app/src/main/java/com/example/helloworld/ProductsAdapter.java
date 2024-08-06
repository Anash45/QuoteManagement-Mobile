package com.example.helloworld;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.function.Consumer;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {

    private List<Product> products;
    private final Consumer<Product> onDeleteClicked;
    private final Consumer<Product> onEditClicked;

    public ProductsAdapter(List<Product> products, Consumer<Product> onDeleteClicked, Consumer<Product> onEditClicked) {
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
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
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
        }
    }
}
