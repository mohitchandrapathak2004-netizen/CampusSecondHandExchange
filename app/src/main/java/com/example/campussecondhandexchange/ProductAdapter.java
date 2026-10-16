package com.example.campussecondhandexchange;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_listing, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvProductName.setText(product.getItemName());
        holder.tvProductPrice.setText("₹" + product.getPrice());
        holder.tvProductInfo.setText(product.getCategory() + " • " + product.getCondition());
        holder.tvSellerName.setText("Seller: " + product.getSellerName());

        // Load image using Glide with rounded corners to match your UI
        Glide.with(context)
                .load(product.getImageUrl())
                .transform(new CenterCrop(), new RoundedCorners(16))
                .placeholder(R.drawable.ic_launcher_background) // Fallback image while loading
                .into(holder.ivProductImage);

        // Handle item click to open Product Details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, product_details.class);
            // Pass the clicked product object to the details screen
            intent.putExtra("product_data", product);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateList(List<Product> newList) {
        productList = newList;
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvProductInfo, tvSellerName;
        ImageView ivProductImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductInfo = itemView.findViewById(R.id.tvProductInfo);
            tvSellerName = itemView.findViewById(R.id.tvSellerName);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
        }
    }
}
