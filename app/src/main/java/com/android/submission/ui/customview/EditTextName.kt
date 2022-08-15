package com.android.submission.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.android.submission.R
import com.google.android.material.textfield.TextInputEditText

class EditTextName : TextInputEditText, View.OnTouchListener {
    private lateinit var clearDrawable: Drawable
    private lateinit var personDrawable: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setButtonDrawables()
        setBackgroundResource(R.drawable.border_view)
        textSize = 15f
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        clearDrawable = ContextCompat.getDrawable(
            context,
            R.drawable.ic_baseline_close_24
        ) as Drawable // x button

        personDrawable =
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_person_24) as Drawable

        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) showClearButton() else hideClearButton()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun showClearButton() {
        setButtonDrawables(endOfTheText = clearDrawable)
    }

    private fun hideClearButton() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable = personDrawable,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onTouch(p0: View?, p1: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearDrawable.intrinsicWidth + paddingStart).toFloat()
                if (p1.x < clearButtonEnd) isClearButtonClicked = true
            } else {
                clearButtonStart = (width - paddingEnd - clearDrawable.intrinsicWidth).toFloat()
                if (p1.x > clearButtonStart) isClearButtonClicked = true
            }

            if (isClearButtonClicked) {
                return when (p1.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearDrawable = ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_baseline_close_24
                        ) as Drawable
                        showClearButton() // show button x
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        clearDrawable = ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_baseline_close_24
                        ) as Drawable
                        if (text != null) text?.clear() // clear text
                        hideClearButton()  // hide button x
                        true
                    }
                    else -> false
                }
            } else return false
        }
        return false
    }
}