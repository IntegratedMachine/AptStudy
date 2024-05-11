package com.example.gavin.apttest

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.gavin.apt_annotation.BindView

class CustomView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    @BindView(R.id.tv)
    public var textView: TextView? = null

    @BindView(R.id.btn)
    public var mButton: Button? = null
    init {
        LayoutInflater.from(context).inflate(R.layout.activity_main, this, true)
        textView!!.text = "bind TextView success"
        mButton!!.text = "bind Button success"
    }
}