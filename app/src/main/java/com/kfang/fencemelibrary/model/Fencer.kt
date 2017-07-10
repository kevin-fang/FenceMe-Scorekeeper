package com.kfang.fencemelibrary.model

import android.databinding.BaseObservable
import android.databinding.Bindable

import com.kfang.fencemelibrary.BR

import java.io.Serializable


/**
 * Class to contain fencers
 */

class Fencer
// is decrement card functionality really needed?

(var defaultName: String) : BaseObservable(), Serializable {
    internal var points: Int = 0
    var redCards: Int = 0
    var yellowCards: Int = 0
    private var hasPriority: Boolean = false

    init {
        this.points = 0
        this.redCards = 0
        this.yellowCards = 0
    }

    fun assignPriority() {
        this.hasPriority = true
    }

    fun hasPriority(): Boolean {
        return this.hasPriority
    }

    @Bindable
    fun getPoints(): Int {
        return points
    }

    fun setPoints(points: Int) {
        this.points = points
        notifyPropertyChanged(BR.points)
    }

    fun incrementNumPoints() {
        this.points++
        notifyPropertyChanged(BR.points)
    }

    fun decrementNumPoints() {
        this.points--
        notifyPropertyChanged(BR.points)
    }

    var name: String
        @Bindable
        get() = defaultName
        set(name) {
            this.defaultName = name
            notifyPropertyChanged(BR.name)
        }

    fun incrementRedCards() {
        this.redCards++
    }

    fun incrementYellowCards() {
        this.yellowCards++
    }

    fun resetCards() {
        this.redCards = 0
        this.yellowCards = 0
    }
}
