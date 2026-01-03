package com.rezaul.foodrushseller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rezaul.foodrushseller.R;
import com.rezaul.foodrushseller.models.MenuItem;

import java.util.List;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ViewHolder> {

    public interface OnMenuItemClickListener {
        void onImageClick(MenuItem item);
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

        holder.ivImage.setOnClickListener(v -> listener.onImageClick(item));
    }

    @Override
    public int getItemCount() {
        return menuItemList == null ? 0 : menuItemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPrice, tvStatus;
        ImageView ivImage;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMenuName);
            tvPrice = itemView.findViewById(R.id.tvMenuPrice);
            tvStatus = itemView.findViewById(R.id.tvMenuStatus);
            ivImage = itemView.findViewById(R.id.ivMenuImage);
        }
    }
}
