package com.rezaul.foodrushseller.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rezaul.foodrushseller.R;
import com.rezaul.foodrushseller.models.Restaurant;
import com.rezaul.foodrushseller.utils.Constants;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private Context context;
    private List<Restaurant> restaurants;
    private OnRestaurantClickListener listener;
    private static final String TAG = "RestaurantAdapter";

    public interface OnRestaurantClickListener {
        void onClick(Restaurant restaurant);
        void onEditClick(Restaurant restaurant);
    }

    public RestaurantAdapter(Context context, List<Restaurant> restaurants,
                             OnRestaurantClickListener listener) {
        this.context = context;
        this.restaurants = restaurants;
        this.listener = listener;
    }

    @Override
    public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);

        // Set text
        holder.tvName.setText(restaurant.getName());
        holder.tvAddress.setText(restaurant.getAddress() != null ? restaurant.getAddress() : "No address");
        holder.tvRating.setText(String.format("⭐ %.1f", restaurant.getRating() != null ? restaurant.getRating() : 0.0));

        // ===== IMAGE LOADING WITH DEBUG =====
        String bannerUrl = restaurant.getBannerImageUrl();

        Log.d(TAG, "=== BINDING RESTAURANT ===");
        Log.d(TAG, "Name: " + restaurant.getName());
        Log.d(TAG, "Banner URL from API: " + (bannerUrl != null ? bannerUrl : "NULL"));
        Log.d(TAG, "Base URL: " + Constants.BASE_URL);

        if (bannerUrl != null && !bannerUrl.isEmpty() && !bannerUrl.equals("null")) {
            // Construct full URL
            String fullImageUrl;

            if (bannerUrl.startsWith("http")) {
                // Already a full URL
                fullImageUrl = bannerUrl;
            } else {
                // Relative path - prepend base URL
                fullImageUrl = Constants.BASE_URL + bannerUrl;
                           }

            Log.d(TAG, "Attempting to load: " + fullImageUrl);

            // Load image with Glide - NO caching to force reload
            Glide.with(context)
                    .load(fullImageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)  // ← Changed to NONE
                    .skipMemoryCache(true)  // ← Skip memory cache
                    .placeholder(R.color.colorImagePlaceholder)
                    .error(R.color.colorImagePlaceholder)  // ← Use color instead
                    .timeout(10000)  // ← 10 second timeout
                    .into(holder.ivBanner);

            Log.d(TAG, "Glide.load() called with URL: " + fullImageUrl);
        } else {
            Log.d(TAG, "No banner URL - bannerUrl is: " + bannerUrl);
            holder.ivBanner.setImageResource(R.color.colorImagePlaceholder);
        }

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(restaurant);
            }
        });

        holder.btnEditRestaurant.setOnClickListener(v -> {
            if (listener != null) {
                Log.d(TAG, "Edit button clicked for: " + restaurant.getName());
                listener.onEditClick(restaurant);
            }
        });

        Log.d(TAG, "=== END BINDING ===\n");
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBanner;
        TextView tvName;
        TextView tvAddress;
        TextView tvRating;
        View btnEditRestaurant;

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.ivRestaurantBanner);
            tvName = itemView.findViewById(R.id.tvRestaurantName);
            tvAddress = itemView.findViewById(R.id.tvRestaurantAddress);
            tvRating = itemView.findViewById(R.id.tvRestaurantRating);
            btnEditRestaurant = itemView.findViewById(R.id.btnEditRestaurant);
        }
    }
}