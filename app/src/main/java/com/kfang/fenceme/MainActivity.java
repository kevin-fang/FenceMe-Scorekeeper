package com.kfang.fenceme;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.Random;

import static android.R.id.list;
import static com.kfang.fenceme.TimerService.SET_TIMER_INTENT;
import static com.kfang.fenceme.Utility.greenName;
import static com.kfang.fenceme.Utility.redName;


public class MainActivity extends AppCompatActivity {

    public static final int TO_ADD = 1;
    public static final int TO_SUBTRACT = 0;
    public static long mCurrentTime;
    static String[] mPreferenceTitles;
    public boolean noAds;
    protected Bundle skuDetails;
    Button mStartTimer;
    TextView mCurrentTimer;
    // buttons in main drawable resource file
    Button addRed;
    Button subtractRed;
    Button addGreen;
    Button subtractGreen;
    Button resetTimer;
    TextView greenScore;
    TextView redScore;
    FragmentManager mFragmentManager = getSupportFragmentManager();
    AdView mAdView;
    int maxNameLength = 20;
    Context mContext;
    IInAppBillingService mService;
    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    public static void makeTieBreaker(final Context context) {
        Random r = new Random();
        ArrayList<String> names = new ArrayList<>();
        names.add(redName);
        names.add(greenName);
        String chosenName = names.get(r.nextInt(names.size()));
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Tiebreaker");
        builder.setMessage(chosenName + " has priority!");
        builder.setPositiveButton("Start Tiebreaker", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Starting priority session", Toast.LENGTH_SHORT).show();
                mCurrentTime = 60000;
                Intent setTimer = new Intent(context, TimerService.class);
                setTimer.putExtra("TOGGLE", TimerService.SET_TIMER);
                context.startService(setTimer);
            }
        });
        builder.create().show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = MainActivity.this;
        setContentView(R.layout.activity_main);
        mCurrentTime = Utility.updateCurrentTime(mContext) * 60000;

        // set up ads, views, and BroadcastManagers
        boolean isDebuggable = BuildConfig.DEBUG;
        setupAds(isDebuggable);
        setViews();
        setUpBroadcastManagers();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeButtonEnabled(true);

        setupNavigation();

        /*
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        ArrayList<String> skuList = new ArrayList<String>();
        skuList.add("noAds");
        final Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

        Thread getInAppPurchases = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    skuDetails = mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
                } catch (RemoteException e) {
                    Toast.makeText(getApplicationContext(), "Cannot verify in-app purchases: Internet unavailable", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getInAppPurchases.start(); */
    }

    public void setupNavigation() {
        mPreferenceTitles = getResources().getStringArray(R.array.main_menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (NavigationView) findViewById(R.id.left_drawer);

        /* mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, mPreferenceTitles)); */

        mDrawerList.setNavigationItemSelectedListener(new DrawerItemClickListener(this, mDrawerLayout));

        Menu menu = mDrawerList.getMenu();
        for (String preferenceTitle : mPreferenceTitles) {
            menu.add(preferenceTitle);
        }

        for (int i = 0, count = mDrawerList.getChildCount(); i < count; i++) {
            final View child = mDrawerList.getChildAt(i);
            if (child != null && child instanceof ListView) {
                final ListView menuView = (ListView) child;
                final HeaderViewListAdapter adapter = (HeaderViewListAdapter) menuView.getAdapter();
                final BaseAdapter wrapped = (BaseAdapter) adapter.getWrappedAdapter();
                wrapped.notifyDataSetChanged();
            }
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                mDrawerList.bringToFront();
                mDrawerLayout.requestLayout();
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(mDrawerToggle.getDrawerArrowDrawable());
        }
    }

    public void launchSettings(View v) {
        mDrawerLayout.closeDrawers();
        PreferenceFragment fragment = new SettingsActivity.MyPreferenceFragment();

        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction()
                .add(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void setUpBroadcastManagers() {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        // LocalBroadcastManagers to deal with updating time and toggle button text intents.
        lbm.registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // set the text in the textview to corresponding minutes and seconds
                        int minutes = intent.getIntExtra(TimerService.MINUTES, 0);
                        int seconds = intent.getIntExtra(TimerService.SECONDS, 0);
                        if (seconds < 10 && seconds >= 0) {
                            mCurrentTimer.setText("" + minutes + ":0" + seconds);
                        } else {
                            mCurrentTimer.setText("" + minutes + ":" + seconds);
                        }
                    }
                }, new IntentFilter(TimerService.UPDATE_TIME_INTENT)
        );

        lbm.registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // set text in button to corresponding value.
                        String text = intent.getStringExtra(TimerService.UPDATE_BUTTON_TEXT);
                        mStartTimer.setText(text);
                    }
                }, new IntentFilter(TimerService.UPDATE_TOGGLE_BUTTON_INTENT)
        );

        lbm.registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // set the text in the textview to corresponding minutes and seconds
                        int minutes = Utility.updateCurrentTime(getApplicationContext());
                        mCurrentTime = minutes * 60000;
                        mCurrentTimer.setText("" + minutes + ":00");
                    }
                }, new IntentFilter(TimerService.RESET_TIMER_INTENT)
        );

        lbm.registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        // set the text in the textview to corresponding minutes and seconds
                        int minutes = (int) mCurrentTime / 1000 / 60;
                        int seconds = (int) mCurrentTime / 1000 % 60;

                        //Log.d("MCURRENTTIME", "In Intent: " + mCurrentTime);
                        if (seconds < 10) {
                            mCurrentTimer.setText("" + minutes + ":0" + seconds);
                        } else {
                            mCurrentTimer.setText("" + minutes + ":" + seconds);
                        }
                    }
                }, new IntentFilter(SET_TIMER_INTENT)
        );
    }

    private void setViews() {
        // find name views and set correspondignly
        TextView redNameView = (TextView) findViewById(R.id.redSide);
        TextView greenNameView = (TextView) findViewById(R.id.greenSide);

        if (redName != null) {
            redNameView.setText(redName);
        }
        if (greenName != null) {
            greenNameView.setText(greenName);
        }

        // set textviews and buttons for scorekeeping
        redScore = (TextView) findViewById(R.id.red_score);
        greenScore = (TextView) findViewById(R.id.green_score);
        addRed = (Button) findViewById(R.id.plus_red);
        subtractRed = (Button) findViewById(R.id.minus_red);
        addGreen = (Button) findViewById(R.id.plus_green);
        subtractGreen = (Button) findViewById(R.id.minus_green);

        // set values to redScore and greenScore
        redScore.setText(String.valueOf(Utility.redScore));
        greenScore.setText(String.valueOf(Utility.greenScore));

        // set onclickListeners for buttons
        addRed.setOnClickListener(createOnClickListener(redScore, TO_ADD, Utility.RED_PLAYER));
        subtractRed.setOnClickListener(createOnClickListener(redScore, TO_SUBTRACT, Utility.RED_PLAYER));
        addGreen.setOnClickListener(createOnClickListener(greenScore, TO_ADD, Utility.GREEN_PLAYER));
        subtractGreen.setOnClickListener(createOnClickListener(greenScore, TO_SUBTRACT, Utility.GREEN_PLAYER));

        // set textviews and buttons for timekeeping
        mStartTimer = (Button) findViewById(R.id.start_timer);
        if (TimerService.mTimerRunning) {
            mStartTimer.setText(getResources().getString(R.string.stop_timer));
        }
        resetTimer = (Button) findViewById(R.id.reset_timer);
        mCurrentTimer = (TextView) findViewById(R.id.timer);


        int seconds = (int) (mCurrentTime / 1000) % 60;
        //Toast.makeText(this, "seconds: " + mCurrentTime, Toast.LENGTH_SHORT).show();
        if (seconds < 10) {
            mCurrentTimer.setText("" + mCurrentTime / 1000 / 60 + ":0" + seconds);
        } else {
            mCurrentTimer.setText("" + mCurrentTime / 1000 / 60 + ":" + seconds);
        }

        // set onClickListener for start and reset
        mStartTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startTimer = new Intent(getApplicationContext(), TimerService.class);
                startTimer.putExtra("TOGGLE", TimerService.TOGGLE_TIMER);
                startService(startTimer);
            }
        });
        resetTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopTimer = new Intent(getApplicationContext(), TimerService.class);
                stopTimer.putExtra("TOGGLE", TimerService.RESET_TIMER);
                startService(stopTimer);
            }
        });

    }

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
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    // reset scores
    public void resetScores(View v) {
        redScore.setText(String.format("%s", 0));
        greenScore.setText(String.format("%s", 0));
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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.card:
                startActivity(new Intent(this, CardPlayerActivity.class));
                return true;
            /* case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true; */
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTimer(View v) {
        if (TimerService.mTimerRunning) {
            Toast.makeText(getApplicationContext(), "Pause timer before changing time", Toast.LENGTH_SHORT).show();
            return;
        }
        DialogFragment newFragment = TimePickerFragment.newInstance(R.string.set_timer);
        newFragment.show(mFragmentManager, "dialog");
    }

    // create an on click listener for each of the plus/minus buttons.
    private View.OnClickListener createOnClickListener(final TextView score, final int toAdd, final String player) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valueStr = score.getText().toString();
                int value = Integer.parseInt(valueStr);
                if (toAdd == TO_ADD && value < Utility.getPointsPreference(mContext)) {
                    value += 1;
                } else if (toAdd == TO_SUBTRACT && value > 0) {
                    value -= 1;
                }
                score.setText(String.format("%s", value));
                if (player.equals(Utility.GREEN_PLAYER)) {
                    Utility.greenScore = value;
                } else {
                    Utility.redScore = value;
                }
            }

        };
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
        inputName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxNameLength)});
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
                    greenName = name;
                } else if (defaultName.equals("Red")) {
                    redName = name;
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
