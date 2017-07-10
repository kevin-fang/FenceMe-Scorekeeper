package com.kfang.fencemelibrary.presentation

/**
 * Interface used for Shared Preferences
 */

internal interface SharedPreferencesRepository {
    fun stayAwakeDuringTimer(): Boolean

    fun pointsToWin(): Int

    fun popupOnScoreChange(): Boolean

    fun restoreOnAppReset(): Boolean

    fun vibrateOnTimerFinish(): Boolean

    fun enableDoubleTouch(): Boolean

    fun vibrateOnTimerToggle(): Boolean

    fun pauseOnScoreChange(): Boolean

    fun getBoutLengthMinutes(): Int

}
