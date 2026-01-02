# FoodRashSellerRepo
# FoodRush Seller - Complete Android App Structure

## Project Structure
```
FoodRushSeller/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/rezaul/foodrushseller/
│   │   │   │   ├── activities/
│   │   │   │   │   ├── MainActivity.java
│   │   │   │   │   ├── SplashActivity.java
│   │   │   │   │   ├── LoginActivity.java
│   │   │   │   │   ├── RegisterActivity.java
│   │   │   │   │   ├── DashboardActivity.java
│   │   │   │   │   ├── AddRestaurantActivity.java
│   │   │   │   │   ├── RestaurantDetailsActivity.java
│   │   │   │   │   ├── AddMenuItemActivity.java
│   │   │   │   │   └── OrdersActivity.java
│   │   │   │   │
│   │   │   │   ├── adapters/
│   │   │   │   │   ├── RestaurantAdapter.java
│   │   │   │   │   ├── MenuItemAdapter.java
│   │   │   │   │   └── OrderAdapter.java
│   │   │   │   │
│   │   │   │   ├── models/
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Restaurant.java
│   │   │   │   │   ├── MenuItem.java
│   │   │   │   │   ├── Order.java
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   ├── LoginResponse.java
│   │   │   │   │   └── ApiResponse.java
│   │   │   │   │
│   │   │   │   ├── network/
│   │   │   │   │   ├── ApiClient.java
│   │   │   │   │   ├── ApiService.java
│   │   │   │   │   └── AuthInterceptor.java
│   │   │   │   │
│   │   │   │   └── utils/
│   │   │   │       ├── Constants.java
│   │   │   │       ├── PreferenceManager.java
│   │   │   │       └── ValidationUtils.java
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_splash.xml
│   │   │   │   │   ├── activity_login.xml
│   │   │   │   │   ├── activity_register.xml
│   │   │   │   │   ├── activity_dashboard.xml
│   │   │   │   │   ├── activity_add_restaurant.xml
│   │   │   │   │   ├── activity_restaurant_details.xml
│   │   │   │   │   ├── activity_add_menu_item.xml
│   │   │   │   │   ├── activity_orders.xml
│   │   │   │   │   ├── item_restaurant.xml
│   │   │   │   │   ├── item_menu.xml
│   │   │   │   │   └── item_order.xml
│   │   │   │   │
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── bg_button.xml
│   │   │   │   │   ├── bg_edittext.xml
│   │   │   │   │   ├── bg_card.xml
│   │   │   │   │   └── ic_launcher_background.xml
│   │   │   │   │
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── styles.xml
│   │   │   │   │   └── dimens.xml
│   │   │   │   │
│   │   │   │   └── AndroidManifest.xml
│   │   │   │
│   │   │   └── build.gradle (Module: app)
│   │   │
│   │   └── build.gradle (Project)
│   │
│   └── gradle files...
```

## Dependencies Required (build.gradle - Module: app)

```gradle
dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.cardview:cardview:1.0.0'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    
    // Retrofit for API calls
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'
    
    // Glide for image loading
    implementation 'com.github.bumptech.glide:glide:4.16.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.16.0'
    
    // Material Design
    implementation 'com.google.android.material:material:1.11.0'
}
```

## Key Files Overview

### 1. Network Layer
- **ApiClient.java**: Retrofit singleton instance
- **ApiService.java**: API endpoints interface
- **AuthInterceptor.java**: Add token to requests

### 2. Activities
- **SplashActivity**: Initial screen with logo
- **LoginActivity**: Seller login
- **RegisterActivity**: Seller registration
- **DashboardActivity**: Main screen showing restaurants
- **AddRestaurantActivity**: Add new restaurant
- **RestaurantDetailsActivity**: View/edit restaurant & menu items
- **AddMenuItemActivity**: Add menu items
- **OrdersActivity**: View all orders

### 3. Adapters
- **RestaurantAdapter**: Display restaurant list
- **MenuItemAdapter**: Display menu items
- **OrderAdapter**: Display orders

### 4. Models
- **User**: Seller user model
- **Restaurant**: Restaurant data model
- **MenuItem**: Menu item model
- **Order**: Order model
- Request/Response models for API

### 5. Utils
- **Constants**: API base URL, keys
- **PreferenceManager**: Store token, user data
- **ValidationUtils**: Input validation

Now I'll provide the complete code for all files...
