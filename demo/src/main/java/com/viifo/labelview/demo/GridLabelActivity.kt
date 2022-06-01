package com.viifo.labelview.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.viifo.labelview.LabelChangeStatus
import com.viifo.labelview.LabelLayout

class GridLabelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid_label)
        val labels = listOf(
            "label1", "label2", "label label3", "label4", "label label5", "label6",
            "label7", "label8", "label9", "label label10", "label11", "label12",
            "label13", "label14", "label15", "label label16", "label17", "label18"
        )
        findViewById<LabelLayout>(R.id.label_layout_1).apply {
            setLabelList(labels)
        }
        findViewById<LabelLayout>(R.id.label_layout_2).apply {
            setLabelList(labels)
        }
    }
}