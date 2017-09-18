package com.example.susiyanti.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.example.susiyanti.bakingapp.model.Recipe;
import com.example.susiyanti.bakingapp.model.Step;

import java.util.ArrayList;
import java.util.List;

import static com.example.susiyanti.bakingapp.MainActivity.SELECTED_INDEX;
import static com.example.susiyanti.bakingapp.MainActivity.SELECTED_RECIPES;
import static com.example.susiyanti.bakingapp.MainActivity.SELECTED_STEPS;

/**
 * An activity representing a single Step detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeStepListActivity}.
 */
public class RecipeStepDetailActivity extends AppCompatActivity implements RecipeStepDetailFragment.ListItemClickListener{

    ArrayList<Parcelable> recipe;
    String recipeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle selectedRecipeBundle = getIntent().getExtras();
            recipe = selectedRecipeBundle.getParcelableArrayList(SELECTED_RECIPES);
            recipeName = selectedRecipeBundle.getString("Title");

            Bundle arguments = new Bundle();
            arguments.putInt(RecipeStepDetailFragment.ARG_ITEM_ID, getIntent().getIntExtra(RecipeStepDetailFragment.ARG_ITEM_ID,0));
            arguments.putParcelableArrayList(SELECTED_STEPS, getIntent().getParcelableArrayListExtra(MainActivity.SELECTED_STEPS));
            arguments.putInt(SELECTED_INDEX,getIntent().getIntExtra(MainActivity.SELECTED_INDEX,0));
            arguments.putString("Title",recipeName);
            RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.step_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //

            Intent up = new Intent(this, RecipeStepListActivity.class);
            up.putParcelableArrayListExtra(MainActivity.SELECTED_RECIPES, recipe);
            NavUtils.navigateUpTo(this, up);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(List<Step> allSteps, int Index, String recipeName) {
        final RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();

        getSupportActionBar().setTitle(recipeName);

        Bundle stepBundle = new Bundle();
        stepBundle.putParcelableArrayList(SELECTED_STEPS,(ArrayList<Step>) allSteps);
        stepBundle.putInt(SELECTED_INDEX,Index);
        stepBundle.putString("Title",recipeName);
        fragment.setArguments(stepBundle);

            Intent intent = new Intent(this, RecipeStepDetailActivity.class);
            intent.putParcelableArrayListExtra(SELECTED_STEPS, (ArrayList<? extends Parcelable>) allSteps);
            intent.putParcelableArrayListExtra(SELECTED_RECIPES, recipe);
            intent.putExtra(SELECTED_INDEX,Index);
            intent.putExtra("Title",recipeName);
            startActivity(intent);

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("Title",recipeName);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recipeName = savedInstanceState.getString("Title");
    }
}
