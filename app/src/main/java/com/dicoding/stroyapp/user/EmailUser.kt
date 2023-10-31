package com.dicoding.stroyapp.user

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doAfterTextChanged

class EmailUser : AppCompatEditText {
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
        hint = "Masukkan Email Anda"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        doAfterTextChanged {
            error = if (it.isNullOrEmpty()) "Email harus diisi"
            else null
        }
    }
}