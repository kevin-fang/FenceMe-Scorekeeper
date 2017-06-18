package com.kfang.fencemelibrary.main;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kfang.fencemelibrary.BuildConfig;
import com.kfang.fencemelibrary.R;
import com.kfang.fencemelibrary.R2;
import com.kfang.fencemelibrary.databinding.ActivityMainBinding;
import com.kfang.fencemelibrary.misc.TimePickerFragment;
import com.kfang.fencemelibrary.misc.Utility;
import com.kfang.fencemelibrary.misc.navmenu.DrawerAdapter;
import com.kfang.fencemelibrary.misc.navmenu.DrawerItem;
import com.kfang.fencemelibrary.misc.navmenu.SimpleItem;
import com.kfang.fencemelibrary.model.Fencer;
import com.kfang.fencemelibrary.presentation.MainContract;
import com.kfang.fencemelibrary.presentation.MainPresenterImpl;
import com.kobakei.ratethisapp.RateThisApp;
import com.yarolegovich.slidingrootnav.SlideGravity;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kfang.fencemelibrary.main.CardPlayerActivity.FENCER_TO_CARD;
import static com.kfang.fencemelibrary.main.CardPlayerActivity.GREEN_FENCER;
import static com.kfang.fencemelibrary.main.CardPlayerActivity.RED_FENCER;
import static com.kfang.fencemelibrary.main.CardPlayerActivity.RETURN_CARD;
import static com.kfang.fencemelibrary.misc.Constants.COLOR_GREEN;
import static com.kfang.fencemelibrary.misc.Constants.COLOR_RED;
import static com.kfang.fencemelibrary.misc.Constants.CURRENT_TIME;
import static com.kfang.fencemelibrary.misc.Constants.LAST_VERSION_NUMBER;
import static com.kfang.fencemelibrary.misc.Constants.OPEN_CARD_ACTIVITY;
import static com.kfang.fencemelibrary.misc.Constants.TIMER_RUNNING;


public class MainActivity extends AppCompatActivity implements MainContract.MainView, DrawerAdapter.OnItemSelectedListener {

    public static final int CARD_A_PLAYER = 0;
    public static final int TIEBREAKER = 1;
    public static final int RESET_BOUT = 2;
    static final int MAX_NAME_LENGTH = 20;
    public static String LOG_TAG;
    public static String[] screenTitles = {"Card a Player", "Tiebreaker", "Reset Bout"};
    final Handler alarmHandler = new Handler();
    public Ringtone alarmTone;
    public MainContract.MainPresenter presenter;

    float greenY1;
    float redY1;
    float greenY2;
    float redY2;

    // timer views
    @BindView(R2.id.change_timer)
    Button changeTimerButton;
    @BindView(R2.id.timer)
    TextView currentTimerView;

    // buttons in main drawable resource file
    @BindView(R2.id.plus_red)
    Button addRed;
    @BindView(R2.id.minus_red)
    Button subtractRed;
    @BindView(R2.id.plus_green)
    Button addGreen;
    @BindView(R2.id.minus_green)
    Button subtractGreen;
    @BindView(R2.id.reset_timer)
    Button resetTimer;
    @BindView(R2.id.double_touch)
    Button doubleTouch;
    @BindView(R2.id.green_score)
    TextView greenScore;
    @BindView(R2.id.red_score)
    TextView redScore;
    @BindView(R2.id.redSide)
    TextView redNameView;
    @BindView(R2.id.greenSide)
    TextView greenNameView;
    @BindView(R2.id.adView)
    AdView mAdView;
    @BindView(R2.id.coordinator)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R2.id.red_body)
    View redBody;
    @BindView(R2.id.green_body)
    View greenBody;

    FragmentManager mFragmentManager = getSupportFragmentManager();
    Context mContext;
    Vibrator vibrator;
    final Thread alarms = new Thread(() -> {
        // create alarm
        alarmTone.play();
        long[] pattern = {0, 500, 500};
        // create vibration
        if (presenter.vibrateOnTimerFinish()) {
            vibrator.vibrate(pattern, 0);
        }
    });
    @BindView(R2.id.toolbar)
    Toolbar toolbar;

    SlidingRootNav navigationMenu;

    public static boolean isPro(Context context) {
        return !context.getResources().getBoolean(R.bool.lite_version);
    }

    // create a tiebreaker
    public void makeTieBreaker() {
        Fencer chosenFencer = presenter.randomFencer();
        // create tiebreaker dialog that sets time to 1 minute
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tiebreaker");
        builder.setMessage(chosenFencer.getName() + " has priority!");
        builder.setPositiveButton("Start Tiebreaker", (dialog, which) -> {
                //Toast.makeText(context, "Starting Tiebreaker...", Toast.LENGTH_SHORT).show();
                presenter.resetScores();
            presenter.setTimer(60 * 1000);
                presenter.startTimer();
                presenter.setTieBreaker(true);
                enableTimerButton();
        })
                .create()
                .show();

    }

    public void checkAndSetDoubleTouch(Activity activity) {
        if (presenter.enableDoubleTouch()) {
            activity.findViewById(R.id.double_touch).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.double_touch_divider).setVisibility(View.VISIBLE);
        } else {
            activity.findViewById(R.id.double_touch).setVisibility(View.GONE);
            activity.findViewById(R.id.double_touch_divider).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        RateThisApp.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LOG_TAG = this.getPackageName();

        mContext = MainActivity.this;
        setContentView(R.layout.activity_main);

        // set up MVP
        vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        presenter = new MainPresenterImpl(this, PreferenceManager.getDefaultSharedPreferences(this), vibrator);

        // set up data binding for scores and names
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setGreenFencer(presenter.getGreenFencer());
        binding.setRedFencer(presenter.getRedFencer());

        ButterKnife.bind(this);
        // set up ads, views, vibrations and action bars.
        setViews(savedInstanceState);

        // restore game status if enabled
        if (presenter.restoreOnAppReset()) {
            Utility.updateCurrentMatchPreferences(this, presenter);
        } else { // restore default time
            presenter.setTimer(presenter.getBoutLengthMinutes() * 60 * 1000);
        }

        if (savedInstanceState != null) {
            presenter.setTimer(savedInstanceState.getInt(CURRENT_TIME));
            if (savedInstanceState.getBoolean(TIMER_RUNNING)) {
                setTimerButtonColor(COLOR_RED);
                presenter.startTimer();
            }
        } else {
            RateThisApp.showRateDialogIfNeeded(this);
        }
        setupSwipeDetectors();
        checkIfFirstRun();

    }

    void setupSwipeDetectors() {
        View.OnTouchListener greenOnTouchListener = (v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    greenY1 = event.getY();
                    return true;
                case MotionEvent.ACTION_UP:
                    greenY2 = event.getY();
                    if (greenY1 > greenY2) {
                        // swipe up
                        Log.d(LOG_TAG, "Swipe up green, y1: " + greenY1 + ", y2: " + greenY2);
                        changeScoreAndCheckForVictories(presenter.getGreenFencer(), Utility.TO_ADD);
                    } else if (greenY2 > greenY1) {
                        // swipe down
                        Log.d(LOG_TAG, "Swipe down green, y1: " + greenY1 + ", y2: " + greenY2);
                        changeScoreAndCheckForVictories(presenter.getGreenFencer(), Utility.TO_SUBTRACT);
                    } else {
                        getNewName(greenNameView, presenter.getGreenFencer());
                    }
                    return true;
            }
            return false;
        };

        // set up gestures
        greenBody.setOnTouchListener(greenOnTouchListener);
        greenNameView.setOnTouchListener(greenOnTouchListener);

        View.OnTouchListener redOnTouchListener = (v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    redY1 = event.getY();
                    return true;
                case MotionEvent.ACTION_UP:
                    redY2 = event.getY();
                    if (redY1 > redY2) {
                        // swipe up
                        Log.d(LOG_TAG, "Swipe up red, y1: " + redY1 + ", y2: " + redY2);
                        changeScoreAndCheckForVictories(presenter.getRedFencer(), Utility.TO_ADD);
                    } else if (redY2 > redY1) {
                        // swipe down
                        Log.d(LOG_TAG, "Swipe down red, y1: " + redY1 + ", y2: " + redY2);
                        changeScoreAndCheckForVictories(presenter.getRedFencer(), Utility.TO_SUBTRACT);
                    } else {
                        getNewName(redNameView, presenter.getRedFencer());
                    }
                    return true;
            }
            return false;
        };

        redBody.setOnTouchListener(redOnTouchListener);
        redNameView.setOnTouchListener(redOnTouchListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // save current time and whether the timer is running
        outState.putInt(CURRENT_TIME, presenter.getCurrentSeconds());
        outState.putBoolean(TIMER_RUNNING, presenter.timerRunning());
        super.onSaveInstanceState(outState);
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenTitles[position])
                .withSelectedTextTint(R.color.colorAccent)
                .withTextTint(R.color.colorAccent);
    }

    // check if the app is being first run (since last update)
    public void checkIfFirstRun() {
        String versionName = BuildConfig.VERSION_NAME;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String lastVersion = prefs.getString(LAST_VERSION_NUMBER, null);
        if (lastVersion == null || !lastVersion.equals(versionName)) {
            // first run of the app
            displayNewDialog(versionName);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(LAST_VERSION_NUMBER, versionName);
            editor.apply();

        }
    }

    // display a dialog containing what's new
    public void displayNewDialog(String versionName) {
        String[] changes = getResources().getStringArray(R.array.change_log);
        // build change log from string arrays
        StringBuilder changelogBuilder = new StringBuilder();
        for (String change : changes) {
            changelogBuilder.append("\u2022 "); // bullet point
            changelogBuilder.append(change);
            changelogBuilder.append("\n");
        }
        String changeLog = changelogBuilder.toString();

        String proVersion = "";
        if (!getResources().getBoolean(R.bool.lite_version)) {
            proVersion = "Pro ";
        }
        // build alert dialog with changelog
        AlertDialog.Builder whatsNew = new AlertDialog.Builder(this);
        whatsNew.setTitle("What's new in FenceMe! " + proVersion + versionName + ":")
                .setMessage(changeLog)
                .setPositiveButton("Dismiss", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Rate app", (dialog, which) -> {
                    launchRateApp();
                    dialog.dismiss();
                        })
                .create()
                .show();
    }

    // set up navigation drawers

    // launch intent to rate app
    public void launchRateApp() {
        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String cardingPlayer = data.getStringExtra(FENCER_TO_CARD); // string, not a fencer
        String cardToGive = data.getStringExtra(RETURN_CARD);
        presenter.handleCarding(cardingPlayer, cardToGive);
        if (requestCode == OPEN_CARD_ACTIVITY && resultCode == Activity.RESULT_OK) {
            if (!presenter.checkForVictories(presenter.getRedFencer())) {
                presenter.checkForVictories(presenter.getGreenFencer());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        //presenter.stopTimer();
        super.onPause();
    }

    public void setTimerButtonColor(String color) {
        // change the timer button color using transitions.
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(ContextCompat.getColor(this, R.color.colorTimerStop), ContextCompat.getColor(this, R.color.colorTimerStart));
        anim.setEvaluator(new ArgbEvaluator());
        anim.addUpdateListener((valueAnim) ->
                mCoordinatorLayout.setBackgroundColor((Integer) valueAnim.getAnimatedValue()));
        anim.addUpdateListener((valueAnimator) ->
                mCoordinatorLayout.setBackgroundColor((Integer) valueAnimator.getAnimatedValue()));
        anim.setDuration(150);

        switch (color) {
            case COLOR_GREEN:
                anim.setIntValues(ContextCompat.getColor(this, R.color.colorTimerStop), ContextCompat.getColor(this, R.color.colorTimerStart));
                anim.start();
                break;
            case COLOR_RED:
                anim.setIntValues(ContextCompat.getColor(this, R.color.colorTimerStart), ContextCompat.getColor(this, R.color.colorTimerStop));
                anim.start();
                break;
        }
    }

    @Override
    public void vibrateTimer() {
        if (presenter.vibrateOnTimerToggle()) {
            if (!presenter.timerRunning()) {
                vibrator.vibrate(50);
            } else {
                vibrator.vibrate(new long[]{0, 50, 70, 50}, -1);
            }
        }
    }

    @Override
    public void updateToggle(String colorTo, int text) {
        // set text in button to corresponding value.
        setTimerButtonColor(colorTo);
    }

    public void stopRingTone() {
        if (alarmTone != null && alarmTone.isPlaying()) { // stop the alarm if it is currently playing.
            alarmHandler.removeCallbacks(alarms);
            alarmTone.stop();
        }
    }

    @Override
    public void enableTimerButton() {
        //changeTimerButton.setEnabled(true);
    }

    @Override
    public void disableTimerButton() {
        //changeTimerButton.setEnabled(false);
    }

    @Override
    public void displayWinnerDialog(Fencer winner) {
        AlertDialog.Builder winnerDialogBuilder = new AlertDialog.Builder(this);
        winnerDialogBuilder.setTitle(winner.getName() + " wins!")
                .setMessage(winner.getName() + " has won the bout!")
                .setPositiveButton("Reset Bout", (dialog, which) -> {
                    presenter.resetBout();
                    stopRingTone();
                })
                .setOnCancelListener((dialog) -> {
                    presenter.stopTimer();
                    enableChangingScore();
                })
                .create()
                .show();
    }

    @Override
    public void timerUp() {
        // vibrate phone in 500 ms increments
        Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        alarmTone = RingtoneManager.getRingtone(getApplicationContext(), alarm);

        disableTimerButton();
        // play alarm in background thread

        // disable keep screen on
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        alarmHandler.post(alarms);

        // check for victories

        Fencer winnerFencer = presenter.higherPoints();

        if (winnerFencer != null) {
            disableChangingScore();
            AlertDialog.Builder winnerDialogBuilder = new AlertDialog.Builder(mContext);
            winnerDialogBuilder.setTitle(winnerFencer.getName() + " wins!")
                    .setMessage(winnerFencer.getName() + " has won the bout!")
                    .setPositiveButton("Reset Bout", (dialog, which) -> {
                            stopRingTone();
                            vibrator.cancel();
                            Toast.makeText(getApplicationContext(), "Bout reset!", Toast.LENGTH_SHORT).show();
                            presenter.resetBout();
                            enableChangingScore();
                    })
                    .setOnCancelListener((dialog) -> {
                            stopRingTone();
                            vibrator.cancel();
                            presenter.stopTimer();
                            enableChangingScore();
                    })
                    .create()
                    .show();
        } else if (presenter.getTiebreaker()) {
            if (presenter.getGreenFencer().hasPriority()) {
                winnerFencer = presenter.getGreenFencer();
            } else {
                winnerFencer = presenter.getRedFencer();
            }
            AlertDialog.Builder winnerDialogBuilder = new AlertDialog.Builder(mContext);
            winnerDialogBuilder.setTitle(winnerFencer.getName() + " wins!")
                    .setMessage(winnerFencer.getName() + " has won the bout!")
                    .setPositiveButton("Reset Bout", (dialog, which) -> {
                        stopRingTone();
                        vibrator.cancel();
                        Toast.makeText(getApplicationContext(), "Bout reset!", Toast.LENGTH_SHORT).show();
                        presenter.resetBout();
                        enableChangingScore();
                    })
                    .setOnCancelListener(dialog -> {
                        vibrator.cancel();
                        presenter.stopTimer();
                        enableChangingScore();
                    })
                    .create()
                    .show();
        } else {
            AlertDialog.Builder tiebreakerBuilder = new AlertDialog.Builder(mContext);
            tiebreakerBuilder.setTitle("Tie")
                    .setMessage("Score is tied!")
                    .setPositiveButton("Start Tiebreaker", (dialog, which) -> {
                        stopRingTone();
                        vibrator.cancel();
                        enableChangingScore();
                        makeTieBreaker();
                    }).setOnCancelListener(dialog -> {
                        stopRingTone();
                        vibrator.cancel();
                        enableChangingScore();
                    })
                    .create()
                    .show();
        }
    }

    @Override
    public void disableChangingScore() {
        addRed.setEnabled(false);
        subtractRed.setEnabled(false);
        addGreen.setEnabled(false);
        subtractGreen.setEnabled(false);
    }

    @Override
    public void enableChangingScore() {
        addRed.setEnabled(true);
        subtractRed.setEnabled(true);
        addGreen.setEnabled(true);
        subtractGreen.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAndSetDoubleTouch(this);
    }


    @Override
    public void updateTime(String time) {
        currentTimerView.setText(time);
    }

    private void setViews(Bundle savedInstanceState) {

        if (!isPro(this)) {
            //setupAds(BuildConfig.DEBUG);
        } else {
            currentTimerView.setTextSize(148);
        }
        setSupportActionBar(toolbar);

        // set values to redScore and greenScore
        // set onclickListeners for buttons
        addRed.setOnClickListener(createScoreChanger(Utility.TO_ADD, presenter.getRedFencer()));
        subtractRed.setOnClickListener(createScoreChanger(Utility.TO_SUBTRACT, presenter.getRedFencer()));
        addGreen.setOnClickListener(createScoreChanger(Utility.TO_ADD, presenter.getGreenFencer()));
        subtractGreen.setOnClickListener(createScoreChanger(Utility.TO_SUBTRACT, presenter.getGreenFencer()));
        doubleTouch.setOnClickListener(v -> {
            if (presenter.pauseOnScoreChange()) {
                if (presenter.timerRunning()) {
                    presenter.stopTimer();
                }
            }
            presenter.incrementBothPoints();
            presenter.checkForVictories();
            if (presenter.popupOnScoreChange()) {
                Snackbar.make(mCoordinatorLayout, "Gave double touch", Snackbar.LENGTH_SHORT)
                        .setAction("Dismiss", v1 -> {
                        })
                        .show();
            }
        });

        // set onClickListener for start and reset
        mCoordinatorLayout.setOnClickListener(v -> {
            if (presenter.timerRunning()) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else if (presenter.stayAwakeDuringTimer()) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            presenter.toggleTimer();
        });

        currentTimerView.setOnClickListener(v -> {
            if (presenter.timerRunning()) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else if (presenter.stayAwakeDuringTimer()) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            presenter.toggleTimer();
        });
        resetTimer.setOnClickListener(v -> presenter.resetTimer());

        checkAndSetDoubleTouch(this);

        // set action bar
        // set up navigation drawer and reset cards

        SlidingRootNavBuilder builder = new SlidingRootNavBuilder(this)
                .withMenuLayout(R.layout.menu_left_drawer)
                .withToolbarMenuToggle(toolbar)
                .withSavedState(savedInstanceState)
                .withGravity(SlideGravity.LEFT);

        navigationMenu = builder.inject();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(CARD_A_PLAYER),
                createItemFor(TIEBREAKER),
                createItemFor(RESET_BOUT)
        ));
        adapter.setListener(this);
        presenter.resetCards();

        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
    }

    // set up ads with automatic debug detection
    private void setupAds(boolean test) {
        // set up ads and in app purchases
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-6647745358935231~7845605907");

        AdRequest adRequest;
        if (test) {
            adRequest = new AdRequest.Builder().addTestDevice("1E4125EDAE1F61B3A38F14662D5C93C7").build();
        } else {
            adRequest = new AdRequest.Builder().build();
        }
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onDestroy() {
        presenter.stopTimer();
        Utility.saveCurrentMatchPreferences(this, presenter);
        super.onDestroy();
    }

    public void resetBout(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setPositiveButton("Reset", (dialog, which) -> {
                    //changeTimerButton.setText(getString(R.string.button_start_timer));
                    presenter.resetBout();
                    vibrator.cancel();
                    Toast.makeText(mContext, "Bout reset!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setMessage("Resetting will reset all points, the timer, and all cards awarded.")
                .create()
                .show();
    }

    // create options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    // create activities for options menu selections
    public boolean onOptionsItemSelected(MenuItem item) {
        // Activate the navigation drawer toggle
        int id = item.getItemId();
        if (id == R.id.about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else if (id == R.id.whats_new) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String lastVersion = prefs.getString(LAST_VERSION_NUMBER, null);
            displayNewDialog(lastVersion);
            return true;
        } else if (id == R.id.rate_this_app) {
            RateThisApp.showRateDialog(this);
            return true;
        } else if (id == R.id.go_pro) {
            final String appPackageName = "com.helionlabs.fencemepro"; // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        } else if (id == R.id.settings) {
            if (presenter.timerRunning()) {
                presenter.stopTimer();
                Toast.makeText(this, "Paused bout", Toast.LENGTH_SHORT).show();
            }
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void setTimer(View v) {
        if (presenter.timerRunning()) {
            /* Toast.makeText(getApplicationContext(), "Pause timer before changing time", Toast.LENGTH_SHORT).show();*/
            final Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Pause before changing time", Snackbar.LENGTH_SHORT);
            snackbar.setAction("Pause Timer", v1 -> {
                if (presenter.timerRunning()) {
                    presenter.stopTimer();
                }
            }).show();
            return;
        }
        DialogFragment newFragment = TimePickerFragment.newInstance(R.string.button_set_timer, presenter);
        newFragment.show(mFragmentManager, "dialog");
    }

    void changeScoreAndCheckForVictories(Fencer fencer, int toAdd) {
        if (toAdd == Utility.TO_ADD && (fencer.getPoints() < presenter.getPointsToWin() || presenter.equalPoints())) {
            fencer.incrementNumPoints();
        } else if (toAdd == Utility.TO_SUBTRACT && fencer.getPoints() > 0) {
            fencer.decrementNumPoints();
        }

        if (presenter.pauseOnScoreChange()) {
            if (presenter.timerRunning()) {
                presenter.toggleTimer();
            }
        }
        if (!presenter.checkForVictories(fencer) && toAdd == Utility.TO_ADD && presenter.popupOnScoreChange()) {
            Snackbar.make(mCoordinatorLayout, "Gave touch to " + fencer.getName(), Snackbar.LENGTH_SHORT)
                    .setAction("Dismiss", v1 -> {
                    })
                    .show();
        }
    }

    // create an on click listener for each of the plus/minus buttons.
    private View.OnClickListener createScoreChanger(final int toAdd, final Fencer fencer) {
        return v -> changeScoreAndCheckForVictories(fencer, toAdd);
    }

    public void changeRedName(View v) {
        presenter.stopTimer();
        getNewName(v, presenter.getRedFencer());
    }

    public void changeGreenName(View v) {
        presenter.stopTimer();
        getNewName(v, presenter.getGreenFencer());
    }

    // create dialog and request for a new name
    private void getNewName(View v, final Fencer fencer) {
        final TextView view = (TextView) v;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Player Name");
        final EditText inputName = new EditText(this);
        inputName.setText(view.getText());
        inputName.setSelectAllOnFocus(true);
        inputName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        inputName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_NAME_LENGTH)});
        builder.setView(inputName);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String name;
            name = inputName.getText().toString();
            if (name.equals("")) {
                name = fencer.getDefaultName();
            }
            //view.setText(name);
            fencer.setName(name);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog alertToShow = builder.create();
        if (alertToShow.getWindow() != null) {
            alertToShow.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        alertToShow.show();
    }

    @Override
    public void onItemSelected(int position) {
        //Toast.makeText(activity, "selected: " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();

        final Context context = this;
        switch (position) {
            case MainActivity.CARD_A_PLAYER:
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                final String[] playerArray = {presenter.getRedFencer().getName(), presenter.getGreenFencer().getName(), "Reset Cards"};
                builder.setTitle("Card a player")
                        .setItems(playerArray, (dialog, which) -> {
                            String player = playerArray[which];
                            if (player.equals("Reset Cards")) {
                                presenter.resetCards();
                                Toast.makeText(context, "Cards have been reset!", Toast.LENGTH_SHORT).show();
                            } else {
                                // create intent to card player and pause timer
                                if (presenter.timerRunning()) {
                                    presenter.stopTimer();
                                }
                                Intent cardPlayer = new Intent(context, CardPlayerActivity.class);
                                Bundle b = new Bundle();
                                b.putSerializable(RED_FENCER, presenter.getRedFencer());
                                b.putSerializable(GREEN_FENCER, presenter.getGreenFencer());
                                b.putString(FENCER_TO_CARD, player);
                                cardPlayer.putExtras(b);
                                startActivityForResult(cardPlayer, OPEN_CARD_ACTIVITY);
                            }
                        })
                        .create()
                        .show();
                navigationMenu.closeMenu(true);
                break;
            case MainActivity.TIEBREAKER:
                navigationMenu.closeMenu(true);
                makeTieBreaker();
                break;
            case MainActivity.RESET_BOUT:
                navigationMenu.closeMenu(true);
                presenter.resetBout();
                break;
        }
    }
}
