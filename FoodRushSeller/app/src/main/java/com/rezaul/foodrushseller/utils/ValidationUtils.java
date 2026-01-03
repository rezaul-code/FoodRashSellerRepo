package com.rezaul.foodrushseller.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class ValidationUtils {

    public static boolean isValidPhone(String phone) {
        return !TextUtils.isEmpty(phone)
                && phone.length() == 10
                && TextUtils.isDigitsOnly(phone);
    }

    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 3;
    }

    public static boolean isNotEmpty(String value) {
        return !TextUtils.isEmpty(value);
    }

    public static boolean isValidPrice(String price) {
        if (TextUtils.isEmpty(price)) return false;
        try {
            Double.parseDouble(price);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
