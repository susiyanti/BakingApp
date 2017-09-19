package com.example.susiyanti.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.susiyanti.bakingapp.model.Ingredient;
import com.example.susiyanti.bakingapp.model.Recipe;
import com.example.susiyanti.bakingapp.model.Step;

import java.util.ArrayList;
import java.util.List;

import static com.example.susiyanti.bakingapp.MainActivity.SELECTED_INDEX;
import static com.example.susiyanti.bakingapp.MainActivity.SELECTED_RECIPES;
import static com.example.susiyanti.bakingapp.MainActivity.SELECTED_STEPS;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeStepDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeStepListActivity extends AppCompatActivity implements RecipeStepDetailFragment.ListItemClickListener{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private ArrayList<Recipe> recipe;
    String recipeName;

    private TextView ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_list);

        if (savedInstanceState == null) {

            Bundle selectedRecipeBundle = getIntent().getExtras();

            recipe = new ArrayList<>();
            recipe = selectedRecipeBundle.getParcelableArrayList(SELECTED_RECIPES);
            recipeName = recipe.get(0).getName();
        } else {
            recipeName= savedInstanceState.getString("Title");
            recipe = savedInstanceState.getParcelableArrayList(SELECTED_RECIPES);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(recipeName);

        View recyclerView = findViewById(R.id.step_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        ArrayList<String> recipeIngredientsForWidgets= new ArrayList<>();
        ingredients = (TextView) findViewById(R.id.recipe_ingredients);
        for (Ingredient in : recipe.get(0).getIngredients()){
            ingredients.append(in.getQuantity()+" "+in.getMeasure() +" "+in.getIngredient()+"\n");
            recipeIngredientsForWidgets.add(in.getIngredient()+"\n"+
                    "Quantity: "+in.getQuantity().toString()+"\n"+
                    "Measure: "+in.getMeasure()+"\n");
        }
        UpdateBakingService.startBakingService(this,recipeIngredientsForWidgets);

        if (findViewById(R.id.step_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(recipe.get(0).getSteps()));
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("Title",recipeName);
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

        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelableArrayList(SELECTED_STEPS, (ArrayList)allSteps);
            arguments.putInt(SELECTED_INDEX,Index);
            arguments.putString("Title",recipeName);

            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, RecipeStepDetailActivity.class);
            intent.putParcelableArrayListExtra(SELECTED_STEPS, (ArrayList<? extends Parcelable>) allSteps);
            intent.putParcelableArrayListExtra(SELECTED_RECIPES, recipe);
            intent.putExtra(SELECTED_INDEX,Index);
            intent.putExtra("Title",recipeName);
            startActivity(intent);
        }

    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Step> mValues;

        public SimpleItemRecyclerViewAdapter(List<Step> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.step_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).getShortDescription());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putInt(RecipeStepDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
                        arguments.putParcelableArrayList(SELECTED_STEPS, (ArrayList)mValues);
                        arguments.putInt(SELECTED_INDEX,position);
                        arguments.putString("Title",recipeName);
                        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.step_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, RecipeStepDetailActivity.class);
                        intent.putExtra(RecipeStepDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
                        intent.putParcelableArrayListExtra(SELECTED_STEPS, (ArrayList)mValues);
                        intent.putParcelableArrayListExtra(SELECTED_RECIPES, recipe);
                        intent.putExtra(SELECTED_INDEX,position);
                        intent.putExtra("Title",recipeName);
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mContentView;
            public Step mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Title",recipeName);
        outState.putParcelableArrayList(SELECTED_RECIPES, recipe);
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

            Intent up = new Intent(this, MainActivity.class);
            NavUtils.navigateUpTo(this, up);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
