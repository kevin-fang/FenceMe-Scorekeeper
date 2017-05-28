package com.kfang.fencemelibrary.main;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kfang.fencemelibrary.AboutActivity;
import com.kfang.fencemelibrary.BuildConfig;
import com.kfang.fencemelibrary.CardPlayerActivity;
import com.kfang.fencemelibrary.NavMenu.DrawerAdapter;
import com.kfang.fencemelibrary.NavMenu.DrawerItem;
import com.kfang.fencemelibrary.NavMenu.SimpleItem;
import com.kfang.fencemelibrary.R;
import com.kfang.fencemelibrary.R2;
import com.kfang.fencemelibrary.SettingsActivity;
import com.kfang.fencemelibrary.TimePickerFragment;
import com.kfang.fencemelibrary.Utility;
import com.kfang.fencemelibrary.databinding.ActivityMainBinding;
import com.kobakei.ratethisapp.RateThisApp;
import com.yarolegovich.slidingrootnav.SlideGravity;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kfang.fencemelibrary.CardPlayerActivity.GREEN_FENCER;
import static com.kfang.fencemelibrary.CardPlayerActivity.RED_FENCER;
import static com.kfang.fencemelibrary.Constants.COLOR_GREEN;
import static com.kfang.fencemelibrary.Constants.COLOR_RED;
import static com.kfang.fencemelibrary.Constants.LAST_VERSION_NUMBER;
import static com.kfang.fencemelibrary.Constants.OPEN_CARD_ACTIVITY;
import static com.kfang.fencemelibrary.Constants.TIMER_RUNNING;
import static com.kfang.fencemelibrary.Constants.TO_CARD_PLAYER;


public class MainActivity extends AppCompatActivity implements MainContract.MainView, DrawerAdapter.OnItemSelectedListener {

    public static final int CARD_A_PLAYER = 0;
    public static final int TIEBREAKER = 1;
    public static final int RESET_BOUT = 2;
    static final int MAX_NAME_LENGTH = 20;
    public static String LOG_TAG;
    public static String[] screenTitles = {"Card a Player", "Tiebreaker", "Reset Bout"};
    static boolean tieBreaker = false;

    public Ringtone alarmTone;
    public MainContract.MainPresenter presenter;

    // timer views
    @BindView(R2.id.start_timer)
    Button startTimerButton;
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

    FragmentManager mFragmentManager = getSupportFragmentManager();
    Context mContext;
    Vibrator vibrator;
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
        builder.setPositiveButton("Start Tiebreaker", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(context, "Starting Tiebreaker...", Toast.LENGTH_SHORT).show();
                presenter.setTimer(60);
                presenter.startTimer();
                tieBreaker = true;
                enableTimerButton();
            }
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

        // initialize fencers and add to fencers array


        vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        presenter = new MainPresenterImpl(this, PreferenceManager.getDefaultSharedPreferences(this), vibrator);

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
            presenter.setTimer(presenter.getBoutLength() * 60);
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(TIMER_RUNNING)) {
                setTimerButtonColor(COLOR_RED);
            }
        } else {
            RateThisApp.showRateDialogIfNeeded(this);
        }

        checkIfFirstRun();
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
                .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Rate app", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        launchRateApp();
                        dialog.dismiss();
                    }
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
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(ContextCompat.getColor(this, R.color.colorBrightRed), ContextCompat.getColor(this, R.color.colorBrightGreen));
        anim.setEvaluator(new ArgbEvaluator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                startTimerButton.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
            }
        });
        anim.setDuration(150);

        switch (color) {
            case COLOR_GREEN:
                anim.setIntValues(ContextCompat.getColor(this, R.color.colorBrightRed), ContextCompat.getColor(this, R.color.colorBrightGreen));
                anim.start();
                break;
            case COLOR_RED:
                anim.setIntValues(ContextCompat.getColor(this, R.color.colorBrightGreen), ContextCompat.getColor(this, R.color.colorBrightRed));
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
        //startTimerButton.setEnabled(true);
        // set text in button to corresponding value.
        startTimerButton.setText(text);
        setTimerButtonColor(colorTo);
    }

    public void stopRingTone() {
        if (alarmTone != null && alarmTone.isPlaying()) { // stop the alarm if it is currently playing.
            alarmTone.stop();
        }
    }

    @Override
    public void enableTimerButton() {
        startTimerButton.setEnabled(true);
    }

    @Override
    public void disableTimerButton() {
        startTimerButton.setEnabled(false);
    }

    @Override
    public void displayWinnerDialog(Fencer winner) {

        AlertDialog.Builder winnerDialogBuilder = new AlertDialog.Builder(this);
        winnerDialogBuilder.setTitle(winner.getName() + " wins!")
                .setMessage(winner.getName() + " has won the bout!")
                .setPositiveButton("Reset Bout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.resetBout();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        presenter.stopTimer();
                        enableChangingScore();
                    }
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
        final Thread alarms = new Thread(new Runnable() {
            @Override
            public void run() {
                // create alarm
                alarmTone.play();
                long[] pattern = {0, 500, 500};
                // create vibration
                if (presenter.vibrateOnTimerFinish()) {
                    vibrator.vibrate(pattern, 0);
                }
            }

        });
        // disable keep screen on
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final Handler alarmHandler = new Handler();
        alarmHandler.post(alarms);

        // check for victories

        Fencer winnerFencer = presenter.higherPoints();

        if (winnerFencer != null) {
            disableChangingScore();
            AlertDialog.Builder winnerDialogBuilder = new AlertDialog.Builder(mContext);
            winnerDialogBuilder.setTitle(winnerFencer.getName() + " wins!")
                    .setMessage(winnerFencer.getName() + " has won the bout!")
                    .setPositiveButton("Reset Bout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alarmHandler.removeCallbacks(alarms);
                            alarmTone.stop();
                            vibrator.cancel();
                            Toast.makeText(getApplicationContext(), "Bout reset!", Toast.LENGTH_SHORT).show();
                            presenter.resetBout();
                            enableChangingScore();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            alarmHandler.removeCallbacks(alarms);
                            alarmTone.stop();
                            vibrator.cancel();
                            presenter.stopTimer();
                            enableChangingScore();
                        }
                    })
                    .create()
                    .show();
        } else if (tieBreaker) {
            if (presenter.getGreenFencer().hasPriority()) {
                winnerFencer = presenter.getGreenFencer();
            } else {
                winnerFencer = presenter.getRedFencer();
            }
            AlertDialog.Builder winnerDialogBuilder = new AlertDialog.Builder(mContext);
            winnerDialogBuilder.setTitle(winnerFencer.getName() + " wins!")
                    .setMessage(winnerFencer.getName() + " has won the bout!")
                    .setPositiveButton("Reset Bout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alarmHandler.removeCallbacks(alarms);
                            vibrator.cancel();
                            Toast.makeText(getApplicationContext(), "Bout reset!", Toast.LENGTH_SHORT).show();
                            presenter.resetBout();
                            enableChangingScore();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            alarmHandler.removeCallbacks(alarms);
                            vibrator.cancel();
                            presenter.stopTimer();
                            enableChangingScore();
                        }
                    })
                    .create()
                    .show();
        } else {
            AlertDialog.Builder tiebreakerBuilder = new AlertDialog.Builder(mContext);
            tiebreakerBuilder.setTitle("Tie")
                    .setMessage("Score is tied!")
                    .setPositiveButton("Start Tiebreaker", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alarmHandler.removeCallbacks(alarms);
                            alarmTone.stop();
                            vibrator.cancel();
                            enableChangingScore();
                            makeTieBreaker();
                        }
                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    alarmHandler.removeCallbacks(alarms);
                    alarmTone.stop();
                    vibrator.cancel();
                    enableChangingScore();
                }
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
            setupAds(BuildConfig.DEBUG);
        } else {
            currentTimerView.setTextSize(148);
        }
        setSupportActionBar(toolbar);

        // set values to redScore and greenScore
        // set onclickListeners for buttons
        addRed.setOnClickListener(createScoreChanger(redScore, Utility.TO_ADD, presenter.getRedFencer()));
        subtractRed.setOnClickListener(createScoreChanger(redScore, Utility.TO_SUBTRACT, presenter.getRedFencer()));
        addGreen.setOnClickListener(createScoreChanger(greenScore, Utility.TO_ADD, presenter.getGreenFencer()));
        subtractGreen.setOnClickListener(createScoreChanger(greenScore, Utility.TO_SUBTRACT, presenter.getGreenFencer()));
        doubleTouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presenter.pauseOnScoreChange()) {
                    if (presenter.timerRunning()) {
                        presenter.stopTimer();
                    }
                }
                presenter.incrementBothPoints();
                presenter.checkForVictories();
                if (presenter.popupOnScoreChange()) {
                    Snackbar.make(mCoordinatorLayout, "Gave double touch", Snackbar.LENGTH_SHORT)
                            .setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            })
                            .show();
                }
            }
        });

        // set textViews and buttons for timekeeping
        if (presenter.timerRunning()) {
            startTimerButton.setText(getResources().getString(R.string.button_stop_timer));
        }

        // set onClickListener for start and reset
        startTimerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (presenter.stayAwakeDuringTimer()) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
                presenter.toggleTimer();
            }
        });
        resetTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.resetTimer();
            }
        });

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
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startTimerButton.setText(getString(R.string.button_start_timer));
                        presenter.resetBout();
                        vibrator.cancel();
                        Toast.makeText(mContext, "Bout reset!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
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
            snackbar.setAction("Pause Timer", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (presenter.timerRunning()) {
                        presenter.stopTimer();
                    }
                }
            }).show();
            return;
        }
        DialogFragment newFragment = TimePickerFragment.newInstance(R.string.button_set_timer, presenter);
        newFragment.show(mFragmentManager, "dialog");
    }
    // create an on click listener for each of the plus/minus buttons.
    private View.OnClickListener createScoreChanger(final TextView score, final int toAdd, final Fencer fencer) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            .setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })
                            .show();
                }
            }

        };
    }

    public void changeRedName(View v) {
        getNewName(v, presenter.getRedFencer());
    }

    public void changeGreenName(View v) {
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
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name;
                name = inputName.getText().toString();
                if (name.equals("")) {
                    name = fencer.getDefaultName();
                }
                //view.setText(name);
                fencer.setName(name);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

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
                        .setItems(playerArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                                    b.putString(TO_CARD_PLAYER, player);
                                    cardPlayer.putExtras(b);
                                    startActivityForResult(cardPlayer, OPEN_CARD_ACTIVITY);
                                }
                            }
                        })
                        .create()
                        .show();
                navigationMenu.closeMenu(true);
                break;
            case MainActivity.TIEBREAKER:
                presenter.resetScores();
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
