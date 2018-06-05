package com.example.android.bakingtime.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.android.bakingtime.data.remote.RecipeService;
import com.example.android.bakingtime.data.remote.RetrofitClient;

public class ApiUtils {
    private static final String API_BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";

    public static RecipeService getRecipeService() {
        return RetrofitClient.getClient(API_BASE_URL).create(RecipeService.class);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
