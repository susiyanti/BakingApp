package com.example.susiyanti.bakingapp.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by susiyanti on 9/18/17.
 */

public class RetrofitBuilder {
    static RecipeService iRecipe;

    public static RecipeService Retrieve() {

        Gson gson = new GsonBuilder().create();

        iRecipe = new Retrofit.Builder()
                .baseUrl("https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(RecipeService.class);

        return iRecipe;
    }
}
