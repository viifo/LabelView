package com.viifo.labelview.demo

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.viifo.labelview.LabelChangeStatus
import com.viifo.labelview.LabelLayout

class FlexLabelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flex_label)
        val labels = listOf(
            "label1", "label2", "label label3", "label4", "label label5", "label6",
            "label7", "label8", "label9", "label label10", "label11", "label12",
            "label13", "label14", "label15", "label label16", "label17", "label18"
        )
        findViewById<LabelLayout>(R.id.label_layout_1).apply {
            setLabelList(labels)
            setOnItemSelectedChangeListener<String> { selected, status ->
                if (status is LabelChangeStatus.ADD) {
                    Toast.makeText(this@FlexLabelActivity, "选中" + status.item.getOrNull(0), Toast.LENGTH_SHORT).show()
                } else if (status is LabelChangeStatus.REMOVE) {
                    Toast.makeText(this@FlexLabelActivity, "取消选中" + status.item.getOrNull(0), Toast.LENGTH_SHORT).show()
                }
            }
        }
        findViewById<LabelLayout>(R.id.label_layout_2).apply {
            setLabelList(labels)
        }
        findViewById<LabelLayout>(R.id.label_layout_3).apply {
            setLabelList(labels)
        }
        findViewById<LabelLayout>(R.id.label_layout_4).apply {
            setLabelList(
                data = labels,
                selectedData = listOf("label label3", "label label10"),
                converter = { holder, item, selected ->
                    holder.itemView.apply {
                        findViewById<TextView>(R.id.label_view_tv_name).also {
                            it.text = item
                            it.setBackgroundColor(Color.parseColor(if (selected) "#8001C8AC" else "#f7f7f7"))
                            it.setTextColor(Color.parseColor(if (selected) "#ffffff" else "#999999"))
                        }
                        findViewById<TextView>(R.id.label_view_iv_checked).isVisible = selected
                    }
                }
            )
        }
        findViewById<LabelLayout>(R.id.label_layout_5).apply {
            setLabelList(labels)
        }
    }

}