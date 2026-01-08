package com.rezaul.foodrushseller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rezaul.foodrushseller.R;
import com.rezaul.foodrushseller.models.MenuItem;
import com.rezaul.foodrushseller.utils.Constants;

import java.util.List;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ViewHolder> {

    public interface OnMenuItemClickListener {
        void onItemClick(MenuItem item);
        void onEditImageClick(MenuItem item);
    }

    private Context context;
    private List<MenuItem> menuItemList;
    private OnMenuItemClickListener listener;

    public MenuItemAdapter(Context context, List<MenuItem> menuItemList,
                           OnMenuItemClickListener listener) {
        this.context = context;
        this.menuItemList = menuItemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem item = menuItemList.get(position);

        holder.tvName.setText(item.getName());
        holder.tvPrice.setText("₹ " + item.getPrice());
        holder.tvStatus.setText(item.getAvailable() ? "Available" : "Unavailable");

        // Show veg/non-veg indicator
        if (item.getVeg() != null) {
            if (item.getVeg()) {
                holder.tvVegIndicator.setText("🥬 Veg");
                holder.tvVegIndicator.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            } else {
                holder.tvVegIndicator.setText("🍗 Non-Veg");
                holder.tvVegIndicator.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            }
            holder.tvVegIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.tvVegIndicator.setVisibility(View.GONE);
        }

        // Load menu item image
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            String imageUrl = Constants.BASE_URL + item.getImagePath();

            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.colorImagePlaceholder)
                    .error(R.color.colorImagePlaceholder)
                    .into(holder.ivMenuImage);

            holder.tvNoImage.setVisibility(View.GONE);
        } else {
            // Show placeholder when no image
            holder.ivMenuImage.setImageResource(R.color.colorImagePlaceholder);
            holder.tvNoImage.setVisibility(View.VISIBLE);
        }

        // Click on card
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));

        // Click on edit image button
        holder.btnEditImage.setOnClickListener(v -> listener.onEditImageClick(item));
    }

    @Override
    public int getItemCount() {
        return menuItemList == null ? 0 : menuItemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPrice, tvStatus, tvVegIndicator, tvNoImage;
        ImageView ivMenuImage;
        ImageButton btnEditImage;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMenuName);
            tvPrice = itemView.findViewById(R.id.tvMenuPrice);
            tvStatus = itemView.findViewById(R.id.tvMenuStatus);
            tvVegIndicator = itemView.findViewById(R.id.tvVegIndicator);
            ivMenuImage = itemView.findViewById(R.id.ivMenuImage);
            tvNoImage = itemView.findViewById(R.id.tvNoImage);
            btnEditImage = itemView.findViewById(R.id.btnEditImage);
        }
    }
}