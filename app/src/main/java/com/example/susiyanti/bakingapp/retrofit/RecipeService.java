package com.example.susiyanti.bakingapp.retrofit;

import com.example.susiyanti.bakingapp.model.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by susiyanti on 9/18/17.
 */

public interface RecipeService {
    @GET("baking.json")
    Call<ArrayList<Recipe>> getRecipe();
}
