package com.kfang.fencemelibrary;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import android.support.v4.content.LocalBroadcastManager;
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
import com.kfang.fencemelibrary.NavMenu.DrawerAdapter;
import com.kfang.fencemelibrary.NavMenu.DrawerItem;
import com.kfang.fencemelibrary.NavMenu.SimpleItem;
import com.kobakei.ratethisapp.RateThisApp;
import com.yarolegovich.slidingrootnav.SlideGravity;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    public static final int CARD_A_PLAYER = 0;
    public static final int TIEBREAKER = 1;
    public static final int RESET_BOUT = 2;
    static final int MAX_NAME_LENGTH = 20;
    public static String LOG_TAG;
    public static long mCurrentTime;
    public static Fencer mRedFencer;
    public static Fencer mGreenFencer;
    public static String[] screenTitles = {"Card a Player", "Tiebreaker", "Reset Bout"};
    static ArrayList<Fencer> fencers;
    static boolean tieBreaker = false;

    // Broadcast Receivers
    BroadcastReceiver updateTime;
    BroadcastReceiver updateToggle;
    BroadcastReceiver resetBoutTimer;
    BroadcastReceiver timerUp;
    BroadcastReceiver resetEntireBout;
    // timer views
    Button mStartTimer;
    TextView mCurrentTimer;
    // buttons in main drawable resource file
    Button addRed;
    Button subtractRed;
    Button addGreen;
    Button subtractGreen;
    Button resetTimer;
    Button doubleTouch;
    TextView greenScore;
    TextView redScore;
    TextView redNameView;
    TextView greenNameView;
    FragmentManager mFragmentManager = getSupportFragmentManager();
    AdView mAdView;
    Context mContext;
    Vibrator vibrator;
    CoordinatorLayout mCoordinatorLayout;

    // create a tiebreaker
    public static void makeTieBreaker(final Context context) {
        Random r = new Random();
        Fencer chosenFencer = fencers.get(r.nextInt(fencers.size()));
        chosenFencer.assignPriority();
        // create tiebreaker dialog that sets time to 1 minute
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Tiebreaker");
        builder.setMessage(chosenFencer.getName() + " has priority!");
        builder.setPositiveButton("Start Tiebreaker", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(context, "Starting Tiebreaker...", Toast.LENGTH_SHORT).show();
                mCurrentTime = 60000;
                TimerService.mTimerRunning = false;
                Intent setTimer = new Intent(context, TimerService.class);
                setTimer.putExtra(Utility.CHANGE_TIMER, TimerService.SET_TIMER);
                context.startService(setTimer);

                Intent startTimer = new Intent(context, TimerService.class);
                startTimer.putExtra(Utility.CHANGE_TIMER, TimerService.TOGGLE_TIMER);
                context.startService(startTimer);
                tieBreaker = true;
            }
        })
                .create()
                .show();

    }

    public static void resetPlayerCards() {
        mRedFencer.resetCards();
        mGreenFencer.resetCards();
    }

    public static void checkAndSetDoubleTouch(Activity activity) {
        if (Utility.getDoubleTouchStatus(activity)) {
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
        setupBroadcastReceivers();
    }

    @Override
    protected void onStop() {
        unregisterReceivers();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LOG_TAG = this.getPackageName();

        mContext = MainActivity.this;
        setContentView(R.layout.activity_main);

        // initialize fencers and add to fencers array
        mRedFencer = new Fencer("Red");
        mGreenFencer = new Fencer("Green");
        fencers = new ArrayList<>();
        fencers.add(mRedFencer);
        fencers.add(mGreenFencer);

        // set up ads, views, vibrations and action bars.
        vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        boolean isDebuggable = BuildConfig.DEBUG;
        setViews();
        if (getResources().getBoolean(R.bool.lite_version)) {
            setupAds(isDebuggable);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set action bar
        // set up navigation drawer and reset cards

        SlidingRootNavBuilder builder = new SlidingRootNavBuilder(this)
                .withMenuLayout(R.layout.menu_left_drawer)
                .withToolbarMenuToggle(toolbar)
                .withSavedState(savedInstanceState)
                .withGravity(SlideGravity.LEFT);

        SlidingRootNav navigationMenu = builder.inject();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(CARD_A_PLAYER),
                createItemFor(TIEBREAKER),
                createItemFor(RESET_BOUT)
        ));
        adapter.setListener(new DrawerItemClickListener(this, navigationMenu));
        resetPlayerCards();

        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        // restore game status if enabled
        if (Utility.getRestoreStatus(mContext)) {
            Utility.updateCurrentMatchPreferences(mContext);
            updateNames();
        } else { // restore default time
            mCurrentTime = Utility.updateCurrentTime(mContext) * 60000;
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(Utility.TIMER_RUNNING)) {
                setTimerButtonColor(Utility.COLOR_RED, this);
            }
        } else {
            RateThisApp.showRateDialogIfNeeded(this);
        }

        setTime();
        checkIfFirstRun();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(Utility.TIMER_RUNNING, TimerService.mTimerRunning);
        super.onSaveInstanceState(outState);
    }

    // update the views containing names of the players
    private void updateNames() {
        redNameView = (TextView) findViewById(R.id.redSide);
        greenNameView = (TextView) findViewById(R.id.greenSide);
        if (mRedFencer.getName() != null) {
            redNameView.setText(mRedFencer.getName());
        }
        if (mGreenFencer.getName() != null) {
            greenNameView.setText(mGreenFencer.getName());
        }
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenTitles[position])
                .withSelectedTextTint(R.color.colorAccent)
                .withTextTint(R.color.colorAccent);
    }

    // check if the app is being first run (since last update)
    public void checkIfFirstRun() {
        String versionName;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            String lastVersion = prefs.getString(Utility.LAST_VERSION_NUMBER, null);
            if (lastVersion == null || !lastVersion.equals(versionName)) {
                // first run of the app
                displayNewDialog(versionName);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Utility.LAST_VERSION_NUMBER, versionName);
                editor.apply();

            }
        } catch (PackageManager.NameNotFoundException e) { // should never happen.
            e.printStackTrace();
        }

    }

    // display a dialog containing what's new
    public void displayNewDialog(String versionName) {
        String[] changes = getResources().getStringArray(R.array.change_log);
        // build change log from string awways
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
        AlertDialog.Builder whatsNew = new AlertDialog.Builder(mContext);
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
        if (requestCode == DrawerItemClickListener.OPEN_CARD_ACTIVITY && resultCode == Activity.RESULT_OK) {
            if (!checkForVictories(mRedFencer)) {
                checkForVictories(mGreenFencer);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // set up navigation drawers

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceivers();
    }

    public void setTimerButtonColor(String color, Context context) {
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(ContextCompat.getColor(context, R.color.colorBrightRed), ContextCompat.getColor(context, R.color.colorBrightGreen));
        anim.setEvaluator(new ArgbEvaluator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mStartTimer.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
            }
        });
        anim.setDuration(150);

        switch (color) {
            case Utility.COLOR_GREEN:
                //mStartTimer.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBrightGreen));
                anim.setIntValues(ContextCompat.getColor(context, R.color.colorBrightRed), ContextCompat.getColor(context, R.color.colorBrightGreen));
                anim.start();
                break;
            case Utility.COLOR_RED:
                //mStartTimer.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBrightRed));
                anim.setIntValues(ContextCompat.getColor(context, R.color.colorBrightGreen), ContextCompat.getColor(context, R.color.colorBrightRed));
                anim.start();
                break;
        }
    }

    private void unregisterReceivers() {
        // unregister the receivers registered in the local broadcast manager
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.unregisterReceiver(updateTime);
        lbm.unregisterReceiver(updateToggle);
        lbm.unregisterReceiver(resetBoutTimer);
        lbm.unregisterReceiver(timerUp);
        lbm.unregisterReceiver(resetEntireBout);
    }

    public void updateScores() {
        redScore.setText(String.valueOf(mRedFencer.getPoints()));
        greenScore.setText(String.valueOf(mGreenFencer.getPoints()));
    }

    // create broadcast receivers to update time, update button, reset bout, and timer up
    private void setupBroadcastReceivers() {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        // LocalBroadcastManagers to deal with updating time
        updateTime = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // set the text in the textview to corresponding minutes and seconds
                // mCurrentTime = intent.getLongExtra(TimerService.CURRENT_TIME, 0);
                setTime();
            }
        };

        // update button toggle text to eiher "start timer" or "stop timer"
        updateToggle = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mStartTimer.setEnabled(true);
                // set text in button to corresponding value.
                String text = intent.getStringExtra(TimerService.UPDATE_BUTTON_TEXT);
                String color = intent.getStringExtra(TimerService.UPDATE_BUTTON_COLOR);
                if (Utility.getVibrateTimerStatus(mContext))
                    if (TimerService.mTimerRunning) {
                        vibrator.vibrate(50);
                    } else {
                        vibrator.vibrate(new long[]{0, 50, 70, 50}, -1);
                    }
                mStartTimer.setText(text);
                setTimerButtonColor(color, getApplicationContext());
            }
        };


        resetBoutTimer = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mStartTimer.setEnabled(true);
                // set the text in the text view to corresponding minutes and seconds
                int minutes = Utility.updateCurrentTime(getApplicationContext());
                mCurrentTime = minutes * 60000;
                setTime();
            }
        };

        timerUp = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // vibrate phone in 500 ms increments
                Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                TimerService.mAlarmTone = RingtoneManager.getRingtone(getApplicationContext(), alarm);

                mStartTimer.setEnabled(false);
                // play alarm in background thread
                final Thread alarms = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // create alarm
                        TimerService.mAlarmTone.play();

                        long[] pattern = {0, 500, 500};
                        // create vibration
                        if (Utility.getVibrateStatus(getApplicationContext())) {
                            vibrator.vibrate(pattern, 0);
                        }
                    }

                });
                // disable keep screen on
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                final Handler alarmHandler = new Handler();
                alarmHandler.post(alarms);

                // check for victories

                Fencer winnerFencer;
                if (mRedFencer.getPoints() > mGreenFencer.getPoints()) {
                    winnerFencer = mRedFencer;
                } else if (mGreenFencer.getPoints() > mRedFencer.getPoints()) {
                    winnerFencer = mGreenFencer;
                } else {
                    winnerFencer = null;
                }

                if (winnerFencer != null) {
                    disableChangingScore();
                    AlertDialog.Builder winnerDialogBuilder = new AlertDialog.Builder(mContext);
                    winnerDialogBuilder.setTitle(winnerFencer.getName() + " wins!")
                            .setMessage(winnerFencer.getName() + " has won the bout!")
                            .setPositiveButton("Reset Bout", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    TimerService.mTimerRunning = false;
                                    alarmHandler.removeCallbacks(alarms);
                                    TimerService.mAlarmTone.stop();
                                    vibrator.cancel();
                                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(TimerService.RESET_BOUT_INTENT));
                                    enableChangingScore();
                                }
                            })
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    TimerService.mTimerRunning = false;
                                    alarmHandler.removeCallbacks(alarms);
                                    TimerService.mAlarmTone.stop();
                                    vibrator.cancel();
                                    enableChangingScore();
                                }
                            })
                            .create()
                            .show();
                } else if (tieBreaker) {
                    if (mGreenFencer.hasPriority()) {
                        winnerFencer = mGreenFencer;
                    } else {
                        winnerFencer = mRedFencer;
                    }
                    AlertDialog.Builder winnerDialogBuilder = new AlertDialog.Builder(mContext);
                    winnerDialogBuilder.setTitle(winnerFencer.getName() + " wins!")
                            .setMessage(winnerFencer.getName() + " has won the bout!")
                            .setPositiveButton("Reset Bout", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    TimerService.mTimerRunning = false;
                                    alarmHandler.removeCallbacks(alarms);
                                    TimerService.mAlarmTone.stop();
                                    vibrator.cancel();
                                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(TimerService.RESET_BOUT_INTENT));
                                    enableChangingScore();
                                }
                            })
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    TimerService.mTimerRunning = false;
                                    alarmHandler.removeCallbacks(alarms);
                                    TimerService.mAlarmTone.stop();
                                    vibrator.cancel();
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
                                    TimerService.mTimerRunning = false;
                                    alarmHandler.removeCallbacks(alarms);
                                    TimerService.mAlarmTone.stop();
                                    vibrator.cancel();
                                    enableChangingScore();
                                    makeTieBreaker(mContext);
                                }
                            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            TimerService.mTimerRunning = false;
                            alarmHandler.removeCallbacks(alarms);
                            TimerService.mAlarmTone.stop();
                            vibrator.cancel();
                            enableChangingScore();
                        }
                    })
                            .create()
                            .show();
                }


            }
        };

        resetEntireBout = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                resetBout(null);
            }
        };

        // timer up
        lbm.registerReceiver(timerUp, new IntentFilter(TimerService.TIMER_UP_INTENT));
        // update time
        lbm.registerReceiver(updateTime, new IntentFilter(TimerService.UPDATE_TIME_INTENT)
        );
        // update text on toggle button
        lbm.registerReceiver(
                updateToggle, new IntentFilter(TimerService.UPDATE_TOGGLE_BUTTON_INTENT)
        );
        // reset timer
        lbm.registerReceiver(resetBoutTimer, new IntentFilter(TimerService.RESET_TIMER_INTENT)
        );

        // holy grail reset entire bout - change
        // cards, timer, player scores
        lbm.registerReceiver(resetEntireBout, new IntentFilter(TimerService.RESET_BOUT_INTENT));
    }

    private void disableChangingScore() {
        addRed.setEnabled(false);
        subtractRed.setEnabled(false);
        addGreen.setEnabled(false);
        subtractGreen.setEnabled(false);
    }

    private void enableChangingScore() {
        addRed.setEnabled(true);
        subtractRed.setEnabled(true);
        addGreen.setEnabled(true);
        subtractGreen.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateScores();
        checkAndSetDoubleTouch(this);
        /* if (!checkForVictories(mRedFencer)) {
            checkForVictories(mGreenFencer);
        } */
    }

    private void setTime() {
        int minutes = (int) mCurrentTime / 1000 / 60;
        int seconds = (int) mCurrentTime / 1000 % 60;
        mCurrentTimer.setText("" + minutes + String.format(Locale.getDefault(), ":%02d", seconds));
    }

    private void setViews() {
        // find name views and set correspondingly
        redNameView = (TextView) findViewById(R.id.redSide);
        greenNameView = (TextView) findViewById(R.id.greenSide);

        if (mRedFencer.getName() != null) {
            redNameView.setText(mRedFencer.getName());
        }
        if (mGreenFencer.getName() != null) {
            greenNameView.setText(mGreenFencer.getName());
        }

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);

        // set text views and buttons for score keeping
        redScore = (TextView) findViewById(R.id.red_score);
        greenScore = (TextView) findViewById(R.id.green_score);
        addRed = (Button) findViewById(R.id.plus_red);
        subtractRed = (Button) findViewById(R.id.minus_red);
        addGreen = (Button) findViewById(R.id.plus_green);
        subtractGreen = (Button) findViewById(R.id.minus_green);
        doubleTouch = (Button) findViewById(R.id.double_touch);

        // set values to redScore and greenScore
        redScore.setText(String.valueOf(mRedFencer.getPoints()));
        greenScore.setText(String.valueOf(mGreenFencer.getPoints()));

        // set onclickListeners for buttons
        addRed.setOnClickListener(createScoreChanger(redScore, Utility.TO_ADD, mRedFencer));
        subtractRed.setOnClickListener(createScoreChanger(redScore, Utility.TO_SUBTRACT, mRedFencer));
        addGreen.setOnClickListener(createScoreChanger(greenScore, Utility.TO_ADD, mGreenFencer));
        subtractGreen.setOnClickListener(createScoreChanger(greenScore, Utility.TO_SUBTRACT, mGreenFencer));
        doubleTouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.getPauseStatus(mContext)) {
                    if (TimerService.mTimerRunning) {
                        Intent stopTimer = new Intent(getApplicationContext(), TimerService.class);
                        stopTimer.putExtra(Utility.CHANGE_TIMER, TimerService.TOGGLE_TIMER);
                        startService(stopTimer);
                    }
                }
                for (Fencer fencer : fencers) {
                    fencer.incrementNumPoints();
                }
                updateScores();
                if (mRedFencer.getPoints() >= 5) {
                    if (!checkForVictories(mRedFencer)) {
                        checkForVictories(mGreenFencer);
                    }
                }

                if (Utility.getPopupPreference(mContext)) {
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
        mStartTimer = (Button) findViewById(R.id.start_timer);
        if (TimerService.mTimerRunning) {
            mStartTimer.setText(getResources().getString(R.string.button_stop_timer));
        }
        resetTimer = (Button) findViewById(R.id.reset_timer);
        mCurrentTimer = (TextView) findViewById(R.id.timer);

        // set onClickListener for start and reset
        mStartTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Utility.getAwakeStatus(mContext)) {
                    if (!TimerService.mTimerRunning) { // if timer isn't running, keep screen on because we want to start the timer
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    } else { // else, turn screen off
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                }
                // create toggle timer intent and fire
                Intent startTimer = new Intent(getApplicationContext(), TimerService.class);
                startTimer.putExtra(Utility.CHANGE_TIMER, TimerService.TOGGLE_TIMER);
                startService(startTimer);
            }
        });
        resetTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopTimer = new Intent(getApplicationContext(), TimerService.class);
                stopTimer.putExtra(Utility.CHANGE_TIMER, TimerService.RESET_TIMER);
                startService(stopTimer);
            }
        });

        checkAndSetDoubleTouch(this);

    }

    // set up ads with automatic debug detection
    private void setupAds(boolean test) {
        // set up ads and in app purchases
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-6647745358935231~7845605907");

        mAdView = (AdView) findViewById(R.id.adView);
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

        Utility.saveCurrentMatchPreferences(mContext);
        unregisterReceivers();

        super.onDestroy();
    }

    public void resetBout(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        resetPlayerCards();
                        int minutes = Utility.updateCurrentTime(getApplicationContext());
                        mCurrentTime = minutes * 60000;
                        mStartTimer.setText(getString(R.string.button_start_timer));
                        setTime();
                        resetScores(null);
                        vibrator.cancel();
                        Toast.makeText(mContext, "Bout reset!", Toast.LENGTH_SHORT).show();
                        Intent stopTimer = new Intent(getApplicationContext(), TimerService.class);
                        stopTimer.putExtra(Utility.CHANGE_TIMER, TimerService.RESET_TIMER);
                        startService(stopTimer);
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

    // reset scores
    public void resetScores(View v) {
        enableChangingScore();
        tieBreaker = false;
        mRedFencer.setPoints(0);
        mGreenFencer.setPoints(0);
        updateScores();
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
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            String lastVersion = prefs.getString(Utility.LAST_VERSION_NUMBER, null);
            displayNewDialog(lastVersion);
            return true;
        } else if (id == R.id.rate_this_app) {
            RateThisApp.showRateDialog(this);
            return true;
        } else if (id == R.id.settings) {
            if (TimerService.mTimerRunning) {
                Intent stopTimer = new Intent(getApplicationContext(), TimerService.class);
                stopTimer.putExtra(Utility.CHANGE_TIMER, TimerService.TOGGLE_TIMER);
                startService(stopTimer);
                Toast.makeText(this, "Paused bout", Toast.LENGTH_SHORT).show();
            }
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTimer(View v) {
        if (TimerService.mTimerRunning) {
            /* Toast.makeText(getApplicationContext(), "Pause timer before changing time", Toast.LENGTH_SHORT).show();*/
            final Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Pause before changing time", Snackbar.LENGTH_SHORT);
            snackbar.setAction("Pause Timer", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TimerService.mTimerRunning) {
                        Intent stopTimer = new Intent(getApplicationContext(), TimerService.class);
                        stopTimer.putExtra(Utility.CHANGE_TIMER, TimerService.TOGGLE_TIMER);
                        startService(stopTimer);
                    }
                }
            }).show();
            return;
        }
        DialogFragment newFragment = TimePickerFragment.newInstance(R.string.button_set_timer);
        newFragment.show(mFragmentManager, "dialog");
    }

    // create an on click listener for each of the plus/minus buttons.
    private View.OnClickListener createScoreChanger(final TextView score, final int toAdd, final Fencer fencer) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toAdd == Utility.TO_ADD && (fencer.getPoints() < Utility.getPointsPreference(mContext) || Utility.equalPoints())) {
                    fencer.incrementNumPoints();
                } else if (toAdd == Utility.TO_SUBTRACT && fencer.getPoints() > 0) {
                    fencer.decrementNumPoints();
                }
                score.setText(String.format("%s", fencer.getPoints()));

                if (Utility.getPauseStatus(mContext)) {
                    if (TimerService.mTimerRunning) {
                        Intent stopTimer = new Intent(getApplicationContext(), TimerService.class);
                        stopTimer.putExtra(Utility.CHANGE_TIMER, TimerService.TOGGLE_TIMER);
                        startService(stopTimer);
                    }
                }
                if (!checkForVictories(fencer) && toAdd == Utility.TO_ADD && Utility.getPopupPreference(mContext)) {
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

    public boolean checkForVictories(Fencer fencer) {
        // check if the points are not equal and there is a fencer with enough points to win or there is a tiebreaker and the points aren't equal
        if (!Utility.equalPoints() && fencer.getPoints() >= Utility.getPointsPreference(mContext) || (tieBreaker && !Utility.equalPoints())) {
            if (TimerService.mTimerRunning) {
                Intent stopTimer = new Intent(getApplicationContext(), TimerService.class);
                stopTimer.putExtra(Utility.CHANGE_TIMER, TimerService.TOGGLE_TIMER);
                startService(stopTimer);
            }

            disableChangingScore();
            AlertDialog.Builder winnerDialogBuilder = new AlertDialog.Builder(mContext);
            winnerDialogBuilder.setTitle(fencer.getName() + " wins!")
                    .setMessage(fencer.getName() + " has won the bout!")
                    .setPositiveButton("Reset Bout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TimerService.mTimerRunning = false;
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(TimerService.RESET_BOUT_INTENT));
                            enableChangingScore();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            TimerService.mTimerRunning = false;
                            enableChangingScore();
                        }
                    })
                    .create()
                    .show();
            return true;
        }
        return false;
    }

    public void changeRedName(View v) {
        getNewName(v, "Red");
    }

    public void changeGreenName(View v) {
        getNewName(v, "Green");
    }

    // create dialog and request for a new name
    private void getNewName(View v, final String defaultName) {
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
                    name = defaultName;
                }
                view.setText(name);
                if (defaultName.equals("Green")) {
                    mGreenFencer.setName(name);
                } else if (defaultName.equals("Red")) {
                    mRedFencer.setName(name);
                }
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
}
