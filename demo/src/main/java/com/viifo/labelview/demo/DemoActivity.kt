package com.viifo.labelview.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class DemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)
        findViewById<AppCompatButton>(R.id.btn_flex).setOnClickListener {
            startActivity(Intent(this@DemoActivity, FlexLabelActivity::class.java))
        }
        findViewById<AppCompatButton>(R.id.btn_grid).setOnClickListener {
            startActivity(Intent(this@DemoActivity, GridLabelActivity::class.java))
        }
        findViewById<AppCompatButton>(R.id.btn_line).setOnClickListener {
            startActivity(Intent(this@DemoActivity, LineLabelActivity::class.java))
        }
        findViewById<AppCompatButton>(R.id.btn_filter).setOnClickListener {
            startActivity(Intent(this@DemoActivity, FilterLabelActivity::class.java))
        }
    }

}