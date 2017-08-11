package com.kfang.fencemelibrary.misc

import android.content.Context
import android.content.res.TypedArray
import android.preference.DialogPreference
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.NumberPicker

/**
 * Number Picker Preference. Used for setting time in the settings screen
 */

class NumberPickerPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs) {

    lateinit var picker: NumberPicker
    var value: Int = 0
        set(value) {
            field = value
            persistInt(this.value)
        }

    override fun onCreateDialogView(): View {
        val layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.CENTER

        picker = NumberPicker(context)
        picker.layoutParams = layoutParams
        val dialogView = FrameLayout(context)
        dialogView.addView(picker)

        return dialogView
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        picker.minValue = MIN_VALUE
        picker.maxValue = MAX_VALUE
        picker.wrapSelectorWheel = WRAP_SELECTOR_WHEEL
        picker.value = value
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            picker.clearFocus()
            val newValue = picker.value
            if (callChangeListener(newValue)) {
                value = newValue
            }
        }
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        Log.d("NumberPickerPreference", "in OnGetDefaultValue, a[index] ${a.getInt(index, -1)}")
        return a.getInt(index, Constants.DEFAULT_POINTS)
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        Log.d("NumberPickerPreference", "in onsetinitialvalue, a[index] $defaultValue")
        value = if (restorePersistedValue) getPersistedInt(MIN_VALUE) else defaultValue as Int
        persistInt(value)
    }

    companion object {
        private val MAX_VALUE = 100
        private val MIN_VALUE = 1
        private val WRAP_SELECTOR_WHEEL = true
    }

}
