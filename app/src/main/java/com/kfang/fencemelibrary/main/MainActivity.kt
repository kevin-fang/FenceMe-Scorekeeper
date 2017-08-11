package com.kfang.fencemelibrary.main

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputFilter
import android.text.InputType
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.kfang.fencemelibrary.BuildConfig
import com.kfang.fencemelibrary.R
import com.kfang.fencemelibrary.databinding.ActivityMainBinding
import com.kfang.fencemelibrary.misc.Constants
import com.kfang.fencemelibrary.misc.TimePickerFragment
import com.kfang.fencemelibrary.misc.Utility
import com.kfang.fencemelibrary.misc.navmenu.DrawerAdapter
import com.kfang.fencemelibrary.misc.navmenu.DrawerItem
import com.kfang.fencemelibrary.misc.navmenu.SimpleItem
import com.kfang.fencemelibrary.model.Fencer
import com.kfang.fencemelibrary.presentation.MainContract
import com.kfang.fencemelibrary.presentation.MainPresenterImpl
import com.kobakei.ratethisapp.RateThisApp
import com.yarolegovich.slidingrootnav.SlideGravity
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), MainContract.MainView, DrawerAdapter.OnItemSelectedListener {
    // alarm handling
    val alarmHandler: Handler = Handler()
    var alarmTone: Ringtone? = null
    lateinit var presenter: MainContract.MainPresenter

    // set menu items
    var screenTitles: Array<String> = kotlin.arrayOf("Card a Player", "Tiebreaker", "Reset Bout")

    // set values used for gesture tracking
    var greenY1: Float = 0F
    var redY1: Float = 0F
    var greenY2: Float = 0F
    var redY2: Float = 0F

    // set vibrator
    lateinit var vibrator: Vibrator

    // set up alarm thread
    val alarms: Thread
    init {
        alarms = Thread {
            // create alarm
            alarmTone?.play()
            val pattern = longArrayOf(0, 500, 500)
            // create vibration
            if (presenter.vibrateOnTimerFinish()) {
                vibrator.vibrate(pattern, 0)
            }
        }

    }

    lateinit var navigationMenu: SlidingRootNav

    // create a tiebreaker
    fun makeTieBreaker() {
        // choose a random fencer and assign priority to that fencer
        val chosenFencer = presenter.randomFencer()
        // create tiebreaker dialog that sets time to 1 minute
        AlertDialog.Builder(this).apply {
            setTitle("Tiebreaker")
            setMessage(chosenFencer.name + " has priority!")
            setPositiveButton("Start Tiebreaker") { _, _ ->
                presenter.resetScores()
                presenter.setTimer(60 * 1000)
                presenter.startTimer()
                presenter.tiebreaker = true
            }
            create()
            show()
        }

    }

    override fun hideTimer() {
        timer.visibility = View.INVISIBLE
    }

    override fun showTimer() {
        timer.visibility = View.VISIBLE
    }

    // check and set whether the double touch button is visible
    fun checkAndSetDoubleTouch() {
        val viewState: Int
        if (presenter.enableDoubleTouch()) {
            viewState = View.VISIBLE
        } else {
            viewState = View.GONE
        }
        double_touch.visibility = viewState
        double_touch_divider.visibility = viewState
    }

    override fun onStart() {
        super.onStart()
        RateThisApp.onStart(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LOG_TAG = this.packageName // used for debugging

        // set alarms
        val alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        alarmTone = RingtoneManager.getRingtone(applicationContext, alarm)

        setContentView(R.layout.activity_main)

        // set up MVP
        vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        presenter = MainPresenterImpl(this, PreferenceManager.getDefaultSharedPreferences(this), vibrator)

        // set up data binding for scores and names
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
                .apply {
                    greenFencer = presenter.greenFencer
                    redFencer = presenter.redFencer
                }

        // set up ads, views, vibrations and action bars.
        setViews(savedInstanceState)

        // restore game status if enabled
        if (presenter.restoreOnAppReset()) {
            Utility.updateCurrentMatchPreferences(this, presenter)
        } else { // restore default time
            presenter.setTimer(presenter.boutLengthMinutes * 60 * 1000)
        }

        sabre_mode.setOnClickListener {
            presenter.toggleSabreMode() // hide timer
        }

        if (savedInstanceState != null) {
            presenter.setTimer(savedInstanceState.getInt(Constants.CURRENT_TIME))
            if (savedInstanceState.getBoolean(Constants.TIMER_RUNNING)) {
                setTimerColor(Constants.COLOR_RED)
                presenter.startTimer()
            }
            presenter.sabreMode = savedInstanceState.getBoolean(Constants.SABRE_MODE)
        } else {
            RateThisApp.showRateDialogIfNeeded(this)
        }

        // set up the gesture detection
        setupSwipeDetectors()


        // show first run instructions or new version instructions
        if (firstRun()) {
            showFirstInstructions()
        } else {
            val versionNum = getNewVersionIfFirstLaunch()
            if (versionNum != null) {
                displayNewDialog(versionNum)
            }
        }
    }

    private fun showFirstInstructions() {
        TapTargetSequence(this)
                .targets(
                        TapTarget.forView(timer, "Tap the clock to toggle timer!").apply {
                            outerCircleColor(android.R.color.holo_blue_bright)
                            outerCircleAlpha(0.5f)
                            titleTextSize(20)
                            titleTextColor(android.R.color.white)
                            descriptionTextSize(10)
                            targetCircleColor(R.color.colorSplash)
                            descriptionTextColor(R.color.colorRed)
                            textTypeface(Typeface.SANS_SERIF)
                            dimColor(R.color.blackCard)
                            drawShadow(true)
                            transparentTarget(true)
                            targetRadius(120)
                        },
                        TapTarget.forView(green_fencer_name, "Swipe up/down or use buttons to change the score, or click to change player name!").apply {
                            outerCircleColor(android.R.color.holo_blue_bright)
                            outerCircleAlpha(0.5f)
                            titleTextSize(20)
                            titleTextColor(android.R.color.white)
                            targetCircleColor(R.color.colorSplash)
                            descriptionTextSize(10)
                            descriptionTextColor(R.color.colorRed)
                            textTypeface(Typeface.SANS_SERIF)
                            dimColor(R.color.blackCard)
                            drawShadow(true)
                            tintTarget(false)
                            transparentTarget(true)
                            targetRadius(70)
                        },
                        TapTarget.forView(assign_card_button, "Give red/yellow/black cards to players!").apply {
                            outerCircleColor(android.R.color.holo_blue_bright)
                            outerCircleAlpha(0.5f)
                            titleTextSize(20)
                            titleTextColor(android.R.color.white)
                            targetCircleColor(R.color.colorSplash)
                            descriptionTextSize(10)
                            descriptionTextColor(R.color.colorRed)
                            textTypeface(Typeface.SANS_SERIF)
                            dimColor(R.color.blackCard)
                            drawShadow(true)
                            tintTarget(false)
                            transparentTarget(true)
                            targetRadius(50)
                        },
                        TapTarget.forView(sabre_mode, "Set Sabre mode to hide timer!").apply {
                            outerCircleColor(android.R.color.holo_blue_bright)
                            outerCircleAlpha(0.5f)
                            titleTextSize(20)
                            titleTextColor(android.R.color.white)
                            targetCircleColor(R.color.colorSplash)
                            descriptionTextSize(10)
                            descriptionTextColor(R.color.colorRed)
                            textTypeface(Typeface.SANS_SERIF)
                            dimColor(R.color.blackCard)
                            drawShadow(true)
                            tintTarget(false)
                            transparentTarget(true)
                            targetRadius(50)
                        }
                ).start()
    }

    private fun firstRun(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val firstRun = prefs.getBoolean(Constants.FIRST_RUN, true)
        if (firstRun) {
            // first run of the app
            val editor = prefs.edit()
            editor.putBoolean(Constants.FIRST_RUN, false)
            editor.apply()
            return true
        }
        return false
    }


    internal fun setupSwipeDetectors() {
        /* Detector:
         * On down, pause timer.
         * If the finger landed above the original position, increment
         * Else, decrement
         */
        val greenOnTouchListener = View.OnTouchListener { _, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (presenter.pauseOnScoreChange()) {
                        presenter.stopTimer()
                    }
                    greenY1 = event.y
                    true
                }
                MotionEvent.ACTION_UP -> {
                    greenY2 = event.y
                    if (greenY1 > greenY2) {
                        // swipe up
                        changeScoreAndCheckForVictories(presenter.greenFencer, Utility.TO_ADD)
                    } else if (greenY2 > greenY1) {
                        // swipe down
                        changeScoreAndCheckForVictories(presenter.greenFencer, Utility.TO_SUBTRACT)
                    } else {
                        presenter.stopTimer()
                        getNewName(green_fencer_name, presenter.greenFencer)
                    }
                    true
                }
                else -> false
            }
        }

        // set up gestures
        green_body.setOnTouchListener(greenOnTouchListener)
        green_fencer_name.setOnTouchListener(greenOnTouchListener)

        val redOnTouchListener = View.OnTouchListener { _, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (presenter.pauseOnScoreChange()) {
                        presenter.stopTimer()
                    }
                    redY1 = event.y
                    true
                }
                MotionEvent.ACTION_UP -> {
                    redY2 = event.y
                    if (redY1 > redY2) {
                        // swipe up
                        changeScoreAndCheckForVictories(presenter.redFencer, Utility.TO_ADD)
                    } else if (redY2 > redY1) {
                        // swipe down
                        changeScoreAndCheckForVictories(presenter.redFencer, Utility.TO_SUBTRACT)
                    } else {
                        presenter.stopTimer()
                        getNewName(red_fencer_name, presenter.redFencer)
                    }
                    true
                }
                else -> false
            }
        }
        red_body.setOnTouchListener(redOnTouchListener)
        red_fencer_name.setOnTouchListener(redOnTouchListener)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // save current time and whether the timer is running
        outState.putInt(Constants.CURRENT_TIME, presenter.currentSeconds)
        outState.putBoolean(Constants.TIMER_RUNNING, presenter.timerRunning)
        outState.putBoolean(Constants.SABRE_MODE, presenter.sabreMode)
        super.onSaveInstanceState(outState)
    }

    // for slidingrootnav, create each item
    private fun createItemFor(position: Int): DrawerItem<*> {
        return SimpleItem(screenTitles[position])
                .withSelectedTextTint(R.color.colorAccent)
                .withTextTint(R.color.colorAccent)
    }

    // check if the app is being first run (since last update)
    fun getNewVersionIfFirstLaunch(): String? {
        val versionName = BuildConfig.VERSION_NAME
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val lastVersion = prefs.getString(Constants.LAST_VERSION_NUMBER, null)
        if (lastVersion == null || lastVersion != versionName) {
            // first run of the app
            val editor = prefs.edit()
            editor.putString(Constants.LAST_VERSION_NUMBER, versionName)
            editor.apply()
            return versionName
        }
        return null
    }

    // display a dialog containing what's new, reading from array
    fun displayNewDialog(versionName: String) {
        val changes = resources.getStringArray(R.array.change_log)
        // build change log from string arrays
        val changelogBuilder = StringBuilder()
        for (change in changes) {
            changelogBuilder.append("\u2022 ") // bullet point
            changelogBuilder.append(change)
            changelogBuilder.append("\n")
        }
        val changeLog = changelogBuilder.toString()

        var proVersion = ""
        if (!resources.getBoolean(R.bool.lite_version)) {
            proVersion = "Pro "
        }
        // build alert dialog with changelog
        val whatsNew = AlertDialog.Builder(this)
        whatsNew.apply {
            setTitle("What's new in FenceMe! $proVersion$versionName:")
            setMessage(changeLog)
            setPositiveButton("Dismiss") { dialog, _ -> dialog.dismiss() }
            setNegativeButton("Rate app") { dialog, _ ->
                launchRateApp()
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    // set up navigation drawers

    // launch intent to rate app
    fun launchRateApp() {
        val uri = Uri.parse("market://details?id=" + applicationContext.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + applicationContext.packageName)))
        }

    }

    // handle carding info
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) {
            return
        }
        // if there is a card that was given, change the scores
        val cardingPlayer = data.getStringExtra(CardPlayerActivity.FENCER_TO_CARD) // string, not a fencer
        val cardToGive = data.getStringExtra(CardPlayerActivity.RETURN_CARD)
        presenter.handleCarding(cardingPlayer, cardToGive)
        if (requestCode == Constants.OPEN_CARD_ACTIVITY && resultCode == Activity.RESULT_OK) {
            if (!presenter.checkForVictories(presenter.redFencer)) {
                presenter.checkForVictories(presenter.greenFencer)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onPause() {
        //presenter.stopTimer();
        super.onPause()
    }

    override fun setTimerColor(color: String) {
        // change the timer button color using transitions.
        val anim = ValueAnimator()

        when (color) {
            Constants.COLOR_GREEN -> {
                anim.setIntValues(ContextCompat.getColor(this, R.color.colorTimerStop), ContextCompat.getColor(this, R.color.colorTimerStart))
            }
            Constants.COLOR_RED -> {
                anim.setIntValues(ContextCompat.getColor(this, R.color.colorTimerStart), ContextCompat.getColor(this, R.color.colorTimerStop))
            }
        }
        anim.setEvaluator(ArgbEvaluator())
        anim.addUpdateListener { valueAnimator -> coordinator.setBackgroundColor(valueAnimator.animatedValue as Int) }
        anim.duration = 150
        anim.start()
    }

    override fun vibrateStart() {
        vibrator.vibrate(50)
    }

    override fun vibrateStop() {
        vibrator.vibrate(longArrayOf(0, 50, 70, 50), -1)
    }

    // stop the ringtone if it is playing
    fun stopRingTone() {
        if (alarmTone != null && alarmTone!!.isPlaying) { // stop the alarm if it is currently playing.
            alarmHandler.removeCallbacks(alarms)
            alarmTone!!.stop()
        }
    }

    override fun displayWinnerDialog(winner: Fencer) {
        val winnerDialogBuilder = AlertDialog.Builder(this)
        winnerDialogBuilder.setTitle(winner.name + " wins!").apply {
            setMessage(winner.name + " has won the bout!")
            setPositiveButton("Reset Bout") { _, _ ->
                presenter.resetBout()
                stopRingTone()
            }
            setOnCancelListener { _ ->
                presenter.stopTimer()
                setScoreChangeability(true)
            }
            create()
            show()
        }
    }

    override fun timerUp() {

        // disable keep screen on
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // set alarms
        alarmHandler.post(alarms)

        // check for victories

        var winnerFencer: Fencer? = presenter.higherPoints()
        if (winnerFencer != null) {
            setScoreChangeability(false)
            val winnerDialogBuilder = AlertDialog.Builder(this)
            winnerDialogBuilder.apply {
                setTitle(winnerFencer!!.name + " wins!")
                setMessage(winnerFencer!!.name + " has won the bout!")
                setPositiveButton("Reset Bout") { _, _ ->
                    stopRingTone()
                    vibrator.cancel()
                    Toast.makeText(applicationContext, "Bout reset!", Toast.LENGTH_SHORT).show()
                    presenter.resetBout()
                    setScoreChangeability(true)
                }
                setOnCancelListener {
                    stopRingTone()
                    vibrator.cancel()
                    presenter.stopTimer()
                    setScoreChangeability(true)
                }
                create()
                show()
            }
        } else if (presenter.tiebreaker) {
            if (presenter.greenFencer.priority) {
                winnerFencer = presenter.greenFencer
            } else {
                winnerFencer = presenter.redFencer
            }
            val winnerDialogBuilder = AlertDialog.Builder(this)
            winnerDialogBuilder.apply {
                setTitle(winnerFencer!!.name + " wins!")
                setMessage(winnerFencer!!.name + " has won the bout!")
                setPositiveButton("Reset Bout") { _, _ ->
                    stopRingTone()
                    vibrator.cancel()
                    Toast.makeText(applicationContext, "Bout reset!", Toast.LENGTH_SHORT).show()
                    presenter.resetBout()
                    setScoreChangeability(true)
                }
                setOnCancelListener { _ ->
                    vibrator.cancel()
                    presenter.stopTimer()
                    setScoreChangeability(true)
                }
                create()
                show()
            }
        } else { // score is tied
            presenter.stopTimer()
            val tiebreakerBuilder = AlertDialog.Builder(this)
            tiebreakerBuilder.apply {
                setTitle("Tie")
                setMessage("Score is tied!")
                setPositiveButton("Start Tiebreaker") { _, _ ->
                    stopRingTone()
                    vibrator.cancel()
                    setScoreChangeability(true)
                    makeTieBreaker()
                }
                setOnCancelListener { _ ->
                    stopRingTone()
                    vibrator.cancel()
                    setScoreChangeability(true)
                }
                create()
                show()
            }
        }
    }

    override fun setScoreChangeability(allowChange: Boolean) {
        arrayOf(plus_red, minus_red, plus_green, minus_green).forEach { it.isEnabled = allowChange }
    }

    override fun onResume() {
        super.onResume()
        checkAndSetDoubleTouch()
    }

    override fun updateTime(time: String) {
        timer.text = time
    }

    private fun setViews(savedInstanceState: Bundle?) {
        if (!isPro(this)) {
            setupAds(BuildConfig.DEBUG)
        } else {
            //timer.textSize = 148f
        }
        setSupportActionBar(toolbar)

        assign_card_button.setOnClickListener { displayCardMenu() }

        // set values to redScore and greenScore
        // set onclickListeners for buttons
        plus_red.setOnClickListener(createScoreChanger(Utility.TO_ADD, presenter.redFencer))
        minus_red.setOnClickListener(createScoreChanger(Utility.TO_SUBTRACT, presenter.redFencer))
        plus_green.setOnClickListener(createScoreChanger(Utility.TO_ADD, presenter.greenFencer))
        minus_green.setOnClickListener(createScoreChanger(Utility.TO_SUBTRACT, presenter.greenFencer))
        // on double touch
        double_touch.setOnClickListener { _ ->
            // stop timer of needed
            if (presenter.pauseOnScoreChange()) {
                presenter.stopTimer()
            }
            // increment both points, check for victory
            presenter.incrementBothPoints()
            presenter.checkForVictories()
            if (presenter.popupOnScoreChange()) {
                Snackbar.make(coordinator, "Gave double touch", Snackbar.LENGTH_SHORT)
                        .setAction("Dismiss") { }
                        .show()
            }
        }

        val toggleTimer = View.OnClickListener {
            if (presenter.timerRunning) {
                // remove stay awake if it's on
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else if (presenter.stayAwakeDuringTimer() && !presenter.sabreMode) {
                // add stay awake if setting is enabled and not in sabre
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            presenter.toggleTimer()
        }

        coordinator.setOnClickListener(toggleTimer)
        timer.setOnClickListener(toggleTimer)

        reset_timer.setOnClickListener { presenter.resetTimer() }

        checkAndSetDoubleTouch()
        presenter.resetCards()

        // set action bar
        // set up navigation drawer and reset cards

        val builder = SlidingRootNavBuilder(this).apply {
            withMenuLayout(R.layout.menu_left_drawer)
            withToolbarMenuToggle(toolbar)
            withSavedState(savedInstanceState)
            withGravity(SlideGravity.LEFT)
        }

        navigationMenu = builder.inject()

        val adapter = DrawerAdapter(Arrays.asList(
                createItemFor(CARD_A_PLAYER),
                createItemFor(TIEBREAKER),
                createItemFor(RESET_BOUT)
        ))
        adapter.setListener(this)
        val list = findViewById(R.id.list) as RecyclerView

        list.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(this@MainActivity)
        }.adapter = adapter
    }

    // set up ads with automatic debug detection
    private fun setupAds(test: Boolean) {
        // set up ads and in app purchases
        MobileAds.initialize(applicationContext, "ca-app-pub-6647745358935231~7845605907")

        val adRequest: AdRequest
        if (test) {
            adRequest = AdRequest.Builder().addTestDevice("1E4125EDAE1F61B3A38F14662D5C93C7").build()
        } else {
            adRequest = AdRequest.Builder().build()
        }
        adView.loadAd(adRequest)
    }

    public override fun onDestroy() {
        presenter.stopTimer()
        Utility.saveCurrentMatchPreferences(this, presenter)
        super.onDestroy()
    }

    fun resetBout(v: View) {
        AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setPositiveButton("Reset") { _, _ ->
                    //changeTimeButton.setText(getString(R.string.button_start_timer));
                    presenter.resetBout()
                    vibrator.cancel()
                    Toast.makeText(this, "Bout reset!", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .setMessage("Resetting will reset all points, the timer, and all cards awarded.")
                .create()
                .show()
    }

    // create options menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onBackPressed() {
        if (!navigationMenu.isMenuHidden) {
            navigationMenu.closeMenu(true)
        } else {
            super.onBackPressed()
        }
    }

    // create activities for options menu selections
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Activate the navigation drawer toggle
        val id = item.itemId
        presenter.stopTimer()
        when (id) {
            R.id.about -> { // about this app
                startActivity(Intent(this, AboutActivity::class.java))
                return true
            }
            R.id.whats_new -> { // what's new
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                val lastVersion = prefs.getString(Constants.LAST_VERSION_NUMBER, null)
                displayNewDialog(lastVersion)
                return true
            }
            R.id.rate_this_app -> { // rate this app
                RateThisApp.showRateDialog(this)
                return true
            }
            R.id.go_pro -> { // get pro
                val appPackageName = "com.helionlabs.fencemepro" // getPackageName() from Context or Activity object
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)))
                } catch (anfe: android.content.ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)))
                }

            }
            R.id.settings -> { // launch settings and pause bout
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            R.id.help -> {
                showFirstInstructions()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun setTimer(v: View) {
        if (presenter.timerRunning) {
            Toast.makeText(this, "Paused timer", Toast.LENGTH_SHORT).show()
        }
        presenter.stopTimer()
        val newFragment = TimePickerFragment.newInstance(R.string.button_set_timer, presenter)
        newFragment.show(supportFragmentManager, "dialog")
    }

    internal fun changeScoreAndCheckForVictories(fencer: Fencer, toAdd: Int) {
        /* check if supposed to add. If points less than needed points, or points are equal, increment.
         * If supposed to subtract and points are greater than zero, decrement
         */
        if (toAdd == Utility.TO_ADD && (fencer.getPoints() < presenter.pointsToWin || presenter.equalPoints())) {
            fencer.incrementNumPoints()
        } else if (toAdd == Utility.TO_SUBTRACT && fencer.getPoints() > 0) {
            fencer.decrementNumPoints()
        }

        if (presenter.pauseOnScoreChange()) {
            presenter.stopTimer()
        }
        if (!presenter.checkForVictories(fencer) && toAdd == Utility.TO_ADD && presenter.popupOnScoreChange()) {
            Snackbar.make(coordinator, "Gave touch to " + fencer.name, Snackbar.LENGTH_SHORT)
                    .setAction("Dismiss") { _ -> }
                    .show()
        }
    }

    // create an on click listener for each of the plus/minus buttons.
    private fun createScoreChanger(toAdd: Int, fencer: Fencer): View.OnClickListener {
        return View.OnClickListener { changeScoreAndCheckForVictories(fencer, toAdd) }
    }

    fun changeRedName(v: View) {
        presenter.stopTimer()
        getNewName(v, presenter.redFencer)
    }

    fun changeGreenName(v: View) {
        presenter.stopTimer()
        getNewName(v, presenter.greenFencer)
    }

    // create dialog and request for a new name
    private fun getNewName(v: View, fencer: Fencer) {
        val view = v as TextView
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Player Name")
        val inputName = EditText(this)
        inputName.setText(view.text)
        inputName.setSelectAllOnFocus(true)
        inputName.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        inputName.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_NAME_LENGTH))
        builder.setView(inputName)
        builder.setPositiveButton("OK") { _, _ ->
            var name: String
            name = inputName.text.toString()
            if (name == "") {
                name = fencer.defaultName
            }
            //view.setText(name);
            fencer.name = name
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        val alertToShow = builder.create()
        if (alertToShow.window != null) {
            alertToShow.window!!.setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
        alertToShow.show()
    }

    override fun onItemSelected(position: Int) {
        //Toast.makeText(activity, "selected: " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();

        when (position) {
            MainActivity.CARD_A_PLAYER -> {
                displayCardMenu()
                navigationMenu.closeMenu(true)
            }
            MainActivity.TIEBREAKER -> {
                navigationMenu.closeMenu(true)
                makeTieBreaker()
            }
            MainActivity.RESET_BOUT -> {
                navigationMenu.closeMenu(true)
                presenter.resetBout()
            }
        }
    }

    fun displayCardMenu() {
        val builder = android.app.AlertDialog.Builder(this)
        val playerArray = arrayOf(presenter.redFencer.name, presenter.greenFencer.name, "Reset Cards")
        builder.setTitle("Card a player")
                .setItems(playerArray) { _, which ->
                    val player = playerArray[which]
                    if (player == "Reset Cards") {
                        presenter.resetCards()
                        Toast.makeText(this, "Cards have been reset!", Toast.LENGTH_SHORT).show()
                    } else {
                        // create intent to card player and pause timer
                        presenter.stopTimer()
                        val cardPlayer = Intent(this, CardPlayerActivity::class.java)
                        val b = Bundle()
                        b.putSerializable(CardPlayerActivity.RED_FENCER, presenter.redFencer)
                        b.putSerializable(CardPlayerActivity.GREEN_FENCER, presenter.greenFencer)
                        b.putString(CardPlayerActivity.Companion.FENCER_TO_CARD, player)
                        cardPlayer.putExtras(b)
                        startActivityForResult(cardPlayer, Constants.OPEN_CARD_ACTIVITY)
                    }
                }
                .create()
                .show()
    }

    companion object {
        val CARD_A_PLAYER = 0
        val TIEBREAKER = 1
        val RESET_BOUT = 2
        internal val MAX_NAME_LENGTH = 20
        lateinit var LOG_TAG: String

        fun isPro(context: Context): Boolean {
            return !context.resources.getBoolean(R.bool.lite_version)
        }
    }
}
