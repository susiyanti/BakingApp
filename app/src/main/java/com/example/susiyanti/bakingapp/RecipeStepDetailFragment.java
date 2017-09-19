package com.example.susiyanti.bakingapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.susiyanti.bakingapp.model.Recipe;
import com.example.susiyanti.bakingapp.model.Step;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.example.susiyanti.bakingapp.MainActivity.SELECTED_INDEX;
import static com.example.susiyanti.bakingapp.MainActivity.SELECTED_RECIPES;
import static com.example.susiyanti.bakingapp.MainActivity.SELECTED_STEPS;

/**
 * A fragment representing a single Step detail screen.
 * This fragment is either contained in a {@link RecipeStepListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeStepDetailActivity}
 * on handsets.
 */
public class RecipeStepDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";


    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private BandwidthMeter bandwidthMeter;
    private ArrayList<Step> steps = new ArrayList<>();
    private int selectedIndex;
    private Handler mainHandler;
    ArrayList<Recipe> recipe;
    String recipeName;

    private int resumeWindow;
    private long resumePosition;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeStepDetailFragment() {
    }

    private ListItemClickListener itemClickListener;

    public interface ListItemClickListener {
        void onListItemClick(List<Step> allSteps, int Index, String recipeName);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            if(savedInstanceState != null) {
                steps = savedInstanceState.getParcelableArrayList(SELECTED_STEPS);
                selectedIndex = savedInstanceState.getInt(SELECTED_INDEX);
                recipeName = savedInstanceState.getString("Title");
                resumePosition = savedInstanceState.getLong("resumePosition");
                resumeWindow = savedInstanceState.getInt("resumeWindow");
            }
            else {
                steps =getArguments().getParcelableArrayList(SELECTED_STEPS);
                selectedIndex=getArguments().getInt(SELECTED_INDEX);
                recipeName=getArguments().getString("Title");
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.step_detail, container, false);
        TextView textView;

        if(getActivity() instanceof  RecipeStepListActivity) {
            itemClickListener = (RecipeStepListActivity)getActivity();
        }else{
            itemClickListener = (RecipeStepDetailActivity)getActivity();
        }

        // Show the dummy content as text in a TextView.
        if (recipeName != null) {
            textView = ((TextView) rootView.findViewById(R.id.recipe_step_detail_text));
            textView.setText(steps.get(selectedIndex).getDescription());
            simpleExoPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.stepplayerView);
            simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

            String videoURL = steps.get(selectedIndex).getVideoURL();

            String imageUrl=steps.get(selectedIndex).getThumbnailURL();
            if (imageUrl!="") {
                Uri builtUri = Uri.parse(imageUrl).buildUpon().build();
                ImageView thumbImage = (ImageView) rootView.findViewById(R.id.thumbImage);
                Picasso.with(getContext()).load(builtUri).into(thumbImage);
            }

            if (!videoURL.isEmpty()) {
                initializePlayer();
                if (isInLandscapeMode(getContext())){
                    textView.setVisibility(View.GONE);
                }else{
                    simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                }
            }
            else {
                player=null;
                simpleExoPlayerView.setForeground(ContextCompat.getDrawable(getContext(), R.drawable.ic_visibility_off_white_36dp));
                simpleExoPlayerView.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
            }


            Button mPrevStep = (Button) rootView.findViewById(R.id.previousStep);
            Button mNextstep = (Button) rootView.findViewById(R.id.nextStep);

            mPrevStep.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (steps.get(selectedIndex).getId() > 0) {
                        if (player!=null){
                            player.stop();
                        }
                        itemClickListener.onListItemClick(steps,steps.get(selectedIndex).getId() - 1,recipeName);
                    }
                    else {
                        Toast.makeText(getActivity(),"You already are in the First step of the recipe", Toast.LENGTH_SHORT).show();

                    }
                }});

            mNextstep.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {

                    int lastIndex = steps.size()-1;
                    if (steps.get(selectedIndex).getId() < steps.get(lastIndex).getId()) {
                        if (player!=null){
                            player.stop();
                        }
                        itemClickListener.onListItemClick(steps,steps.get(selectedIndex).getId() + 1,recipeName);
                    }
                    else {
                        Toast.makeText(getContext(),"You already are in the Last step of the recipe", Toast.LENGTH_SHORT).show();

                    }
                }});
        }
        return rootView;
    }

    private void initializePlayer() {
        if (player == null) {
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();

            player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            simpleExoPlayerView.setPlayer(player);

            String userAgent = Util.getUserAgent(getContext(), "Baking App");
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(steps.get(selectedIndex).getVideoURL()), new DefaultDataSourceFactory(getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
            if (haveResumePosition) {
                player.seekTo(resumeWindow, resumePosition);
            }
            player.prepare(mediaSource, !haveResumePosition, false);
            player.setPlayWhenReady(true);
        }
    }

    public boolean isInLandscapeMode( Context context ) {
        return (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SELECTED_STEPS,steps);
        outState.putInt(SELECTED_INDEX,selectedIndex);
        outState.putString("Title",recipeName);
        outState.putLong("resumePosition", resumePosition);
        outState.putInt("resumeWindow", resumeWindow);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (player!=null) {
            updateResumePosition();
            player.release();
            player = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player!=null) {
            updateResumePosition();
            player.release();
            player = null;
        }
    }

    private void updateResumePosition() {
        resumeWindow = player.getCurrentWindowIndex();
        resumePosition = Math.max(0, player.getCurrentPosition());
        Log.d("EXO-up", resumePosition+"");
    }
}
