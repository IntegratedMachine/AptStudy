package com.example.gavin.apttest

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gavin.apt_annotation.BindView
import com.example.gavin.apt_library.BindViewTools

class MainActivity : AppCompatActivity() {
    @BindView(R.id.tv)
    public var textView: TextView? = null

    @BindView(R.id.btn)
    public var mButton: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BindViewTools.bind(this)
        textView!!.text = "bind TextView success"
        mButton!!.text = "bind Button success"
    }

    fun isKotlinClass(clazz: Class<*>) {
        clazz::class.java
    }
}
