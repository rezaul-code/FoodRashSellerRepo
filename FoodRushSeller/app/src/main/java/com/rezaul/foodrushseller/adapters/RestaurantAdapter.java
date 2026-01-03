package com.rezaul.foodrushseller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rezaul.foodrushseller.R;
import com.rezaul.foodrushseller.models.Restaurant;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    public interface OnRestaurantClickListener {
        void onClick(Restaurant restaurant);
    }

    private Context context;
    private List<Restaurant> restaurantList;
    private OnRestaurantClickListener listener;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList, OnRestaurantClickListener listener) {
        this.context = context;
        this.restaurantList = restaurantList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);

        holder.tvName.setText(restaurant.getName());
        holder.tvAddress.setText(restaurant.getAddress());
        holder.tvRating.setText(
                restaurant.getRating() == null ? "0.0 ★" : restaurant.getRating() + " ★"
        );

        holder.itemView.setOnClickListener(v -> listener.onClick(restaurant));
    }

    @Override
    public int getItemCount() {
        return restaurantList == null ? 0 : restaurantList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvAddress, tvRating;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRestaurantName);
            tvAddress = itemView.findViewById(R.id.tvRestaurantAddress);
            tvRating = itemView.findViewById(R.id.tvRestaurantRating);
        }
    }
}
