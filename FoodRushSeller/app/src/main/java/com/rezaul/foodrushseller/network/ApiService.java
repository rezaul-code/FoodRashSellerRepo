package com.rezaul.foodrushseller.network;

import com.rezaul.foodrushseller.models.ApiResponse;
import com.rezaul.foodrushseller.models.LoginRequest;
import com.rezaul.foodrushseller.models.LoginResponse;
import com.rezaul.foodrushseller.models.RegisterRequest;
import com.rezaul.foodrushseller.models.Restaurant;
import com.rezaul.foodrushseller.utils.Constants;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // ===================== AUTH =====================

    @POST(Constants.BASE_URL + "/api/auth/seller/signup")
    Call<ApiResponse> registerSeller(@Body RegisterRequest request);

    @POST(Constants.BASE_URL + "/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // ===================== RESTAURANT =====================

    @POST(Constants.BASE_URL + "/api/seller/restaurant")
    Call<ApiResponse> addRestaurant(@Body Restaurant restaurant);

    @POST(Constants.BASE_URL + "/api/seller/restaurant")
    Call<ApiResponse> addRestaurantJson(@Body RequestBody body);

    @GET(Constants.BASE_URL + "/api/seller/restaurant")
    Call<List<Restaurant>> getRestaurants();

    @GET(Constants.BASE_URL + "/api/seller/restaurant/{restaurantId}")
    Call<Restaurant> getRestaurantById(@Path("restaurantId") Long restaurantId);

    @Multipart
    @PUT("/api/seller/restaurant/{restaurantId}/banner")
    Call<ApiResponse> uploadRestaurantBanner(
            @Path("restaurantId") Long restaurantId,
            @Part MultipartBody.Part image
    );

    @DELETE("/api/seller/restaurant/{restaurantId}/banner")
    Call<ApiResponse> deleteRestaurantBanner(
            @Path("restaurantId") Long restaurantId
    );

    // ===================== MENU =====================

    @Multipart
    @POST(Constants.BASE_URL + "/api/seller/restaurant/{restaurantId}/menu")
    Call<ApiResponse> addMenuItem(
            @Path("restaurantId") Long restaurantId,
            @Part("data") RequestBody data,
            @Part MultipartBody.Part image
    );

    @POST(Constants.BASE_URL + "/api/seller/restaurant/{restaurantId}/menu")
    Call<ApiResponse> addMenuItemJson(
            @Path("restaurantId") Long restaurantId,
            @Body RequestBody data
    );

    @Multipart
    @PUT(Constants.BASE_URL + "/api/seller/menu/{menuItemId}/image")
    Call<ApiResponse> updateMenuImage(
            @Path("menuItemId") Long menuItemId,
            @Part MultipartBody.Part image
    );

    @DELETE(Constants.BASE_URL + "/api/seller/menu/{menuItemId}/image")
    Call<ApiResponse> deleteMenuImage(
            @Path("menuItemId") Long menuItemId
    );
}