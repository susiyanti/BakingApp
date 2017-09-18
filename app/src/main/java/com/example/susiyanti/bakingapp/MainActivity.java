package com.example.susiyanti.bakingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.susiyanti.bakingapp.model.Recipe;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.ListItemClickListener {

    static String ALL_RECIPES="All_Recipes";
    static String SELECTED_RECIPES="Selected_Recipes";
    static String SELECTED_STEPS="Selected_Steps";
    static String SELECTED_INDEX="Selected_Index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onListItemClick(Recipe clickedItemIndex) {
        Bundle selectedRecipeBundle = new Bundle();
        ArrayList<Recipe> selectedRecipe = new ArrayList<>();
        selectedRecipe.add(clickedItemIndex);
        selectedRecipeBundle.putParcelableArrayList(SELECTED_RECIPES,selectedRecipe);

        final Intent intent = new Intent(this, RecipeStepListActivity.class);
        intent.putExtras(selectedRecipeBundle);
        startActivity(intent);
    }
}
