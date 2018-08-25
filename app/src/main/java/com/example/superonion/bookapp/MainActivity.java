package com.example.superonion.bookapp;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    // Two final static variables to pass into the enableDisableAll method
    private static final int STATE_ALL_DISABLED = 0;
    private static final int STATE_ALL_ENABLED = 1;

    private static final String TAG = "MainActivity";
    Animation shake;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    private int SETTINGS_ACTION = 1;
    View titleView;
    View authorView;
    View generalView;
    Button searchButton;
    Button advanceButton;
    boolean advancedSearch = false;
    LinearLayout bottomSheet;

    // Text Fields //
    EditText searchText;
    EditText searchTextTitle;
    EditText searchTextAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        themeChooser(pref.getString("theme","1"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Animations Shake the textView if empty
        shake = AnimationUtils.loadAnimation(this, R.anim.shakeanimation);
        // Animations Blink the imageView arrow
        ImageView arrowView = (ImageView) findViewById(R.id.bottom_arrow);
        animationBlink(arrowView);

        // Text Fields //
        searchText = (EditText) findViewById(R.id.search_field);
        searchTextTitle = (EditText) findViewById(R.id.search_field_title);
        searchTextAuthor = (EditText) findViewById(R.id.search_field_author);

        searchButton = (Button) findViewById(R.id.search_button);
        advanceButton = (Button) findViewById(R.id.advance_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userInput = searchText.getText().toString();
                String userInput1 = searchTextTitle.getText().toString();
                String userInput2 = searchTextAuthor.getText().toString();
                if (!advancedSearch) {
                    if (TextUtils.isEmpty(userInput)) {
                        searchText.startAnimation(shake);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), BookActivity.class);
                        intent.putExtra("intitle", "");
                        intent.putExtra("inauthor", "");
                        intent.putExtra("category", "");
                        intent.putExtra("general", userInput);
                        startActivity(intent);
                    }
                } else {
                    if ((TextUtils.isEmpty(userInput)) && (TextUtils.isEmpty(userInput1)) && (TextUtils.isEmpty(userInput2))) {
                        searchText.startAnimation(shake);
                        searchTextTitle.startAnimation(shake);
                        searchTextAuthor.startAnimation(shake);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), BookActivity.class);
                        intent.putExtra("intitle", userInput1);
                        intent.putExtra("inauthor", userInput2);
                        intent.putExtra("category", userInput);
                        intent.putExtra("general", "");
                        startActivity(intent);
                    }
                }
            }
        });

        titleView = findViewById(R.id.TitleLayout);
        authorView = findViewById(R.id.AuthorLayout);
        generalView = findViewById(R.id.GeneralLayout);
        bottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet) ;

        /**
         *
         * init the bottom sheet behavior
         * @see externalBottomSheetCallback
         *
         */
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new externalBottomSheetCallback());


        advanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                advanceButton.setClickable(false);
                searchButton.setClickable(false);
                // if AdvanceSearch is false; turn it on and do the following >>
                if (!advancedSearch) {
                    TextView generalTextView = (TextView) findViewById(R.id.general_search);
                    generalTextView.setText("By: Subject");
                    advanceButton.setText("<< QUICK SEARCH");
                    searchText.setText("");
                    searchTextTitle.setText("");
                    searchTextAuthor.setText("");
                    slideRight(titleView);
                    slideLeft(authorView);
                    slideDown(generalView);
                    advancedSearch = true;
                } else { // case if AdvanceSearch is on and needs to be turned off.
                    TextView generalTextView = (TextView) findViewById(R.id.general_search);
                    generalTextView.setText("General Search");
                    advanceButton.setText("ADVANCE >>");
                    searchText.setText("");
                    searchTextTitle.setText("");
                    searchTextAuthor.setText("");
                    viewHide(titleView);
                    viewHide(authorView);
                    slideUp(generalView);
                    advancedSearch = false;
                }
            }
        });
    }
    public void viewHide (View view) {
        AlphaAnimation animate = new AlphaAnimation(1.0f, 0.0f);
        animate.setDuration(300);
        view.startAnimation(animate);
        view.setVisibility(View.INVISIBLE);
    }
    // slide the view from Left of itself to the current position
    public void slideRight(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                -(view.getWidth()),                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }
    public void slideLeft(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                view.getWidth(),                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }
    public void slideDown(final View view){

//        Log.d(TAG, "slideDown: Author View");
//        Log.d("WIDTH        :", String.valueOf(rectf.width()));
//        Log.d("HEIGHT       :", String.valueOf(rectf.height()));
//        Log.d("left         :", String.valueOf(rectf.left));
//        Log.d("right        :", String.valueOf(rectf.right));
//        Log.d("top          :", String.valueOf(rectf.top));
//        Log.d("bottom       :", String.valueOf(rectf.bottom));
//        Log.d("Centerx       :", String.valueOf(rectf.centerX()));
//        Log.d("CenterY       :", String.valueOf(rectf.centerY()));
        final Rect rectf = new Rect();
        //For coordinates location relative to the screen/display
        authorView.getGlobalVisibleRect(rectf);
        ObjectAnimator viewAnimator = ObjectAnimator.ofFloat(view, "translationY",0f,rectf.height());
        viewAnimator.setDuration(500);
        viewAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        viewAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                advanceButton.setClickable(true);
                searchButton.setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        viewAnimator.start();
    }
    public void slideUp(View view){
        final Rect rectf = new Rect();
        //For coordinates location relative to the screen/display
        authorView.getGlobalVisibleRect(rectf);
        ObjectAnimator viewAnimator = ObjectAnimator.ofFloat(view, "translationY",rectf.height(),0f);
        viewAnimator.setDuration(500);
        viewAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        viewAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                advanceButton.setClickable(true);
                searchButton.setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        viewAnimator.start();
    }
    public void animationBlink(View view) {
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(animation);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: called");
        Log.d(TAG, "onActivityResult: RESULT CODE" +resultCode);
        if (requestCode == SETTINGS_ACTION) {
            if (resultCode == SettingsActivity.RESULT_CODE_THEME_UPDATED) {
                finish();
                startActivity(getIntent());
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void themeChooser(String theme) {
        switch (theme) {
            case "1":
                setTheme(R.style.AppTheme);
                break;
            case "2":
                setTheme(R.style.AppTheme_Earth);
                break;
            case "3":
                setTheme(R.style.AppTheme_3);
                break;

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.settings:

                startActivityForResult(new Intent(this,
                        SettingsActivity.class), SETTINGS_ACTION);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     *
     * To simplify the onCreate, moved BottomSheetCallback elsewhere
     * This will check if the bottom sheet has been fully expanded
     * If so, disable all bottons and lose focus to all editables.
     *
     */
    private class externalBottomSheetCallback extends BottomSheetBehavior.BottomSheetCallback {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            // Checks for state changes.
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                // The bottomSheet has been expanded so we disable all functionality
                enableDisableAll(STATE_ALL_DISABLED);
            }
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                // The bottomSheet has been collapsed so we enable back all functionality
                Log.e(TAG, "onStateChanged: Sheet collapses" );
                enableDisableAll(STATE_ALL_ENABLED);
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            // Nothing right now
        }
    }
    /**
     *
     * Simple method to help enable/disable all the buttons and text fields
     * within the activity. Accepts a state int which we defined in the
     * scope. STATE_ALL_DISABLED and STATE_ALL_ENABLED
     *
     */
    private void enableDisableAll (int state) {
        if (state == STATE_ALL_DISABLED) {
            // Disables all the buttons
            advanceButton.setClickable(false);
            searchButton.setClickable(false);
            // Disables all the TextFields. Focusable instead of clickable.
            searchText.setFocusable(false);
            searchTextTitle.setFocusable(false);
            searchTextAuthor.setFocusable(false);
        } else if (state == STATE_ALL_ENABLED) {
            // Enable the buttons
            advanceButton.setClickable(true);
            searchButton.setClickable(true);
            // Enable all the TextFields. Focusable instead of clickable
            searchText.setFocusableInTouchMode(true);
            searchTextTitle.setFocusableInTouchMode(true);
            searchTextAuthor.setFocusableInTouchMode(true);
        }
    }
}
