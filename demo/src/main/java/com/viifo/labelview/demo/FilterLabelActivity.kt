package com.viifo.labelview.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.viifo.labelview.LabelChangeStatus
import com.viifo.labelview.LabelLayout

class FilterLabelActivity : AppCompatActivity() {

    data class Options (
        val name: String,
        val list: List<String>
    )

    private val labels = listOf(
        Options("main_1", listOf("label1", "label2", "label3", "label4","label5", "label6", "label7", "label8","label9")),
        Options("main_2", listOf("label11", "label12", "label13", "label14","label15", "label16", "label17", "label18","label19")),
        Options("main_3", listOf("label21", "label22", "label23", "label24","label25", "label26", "label27", "label28","label29")),
    )

    private var selectAll = false
    private val mySelected: MutableList<Options> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_label)
        findViewById<LabelLayout>(R.id.main_label).apply {
            val mainLabels = labels.map { it.name }
            setOnItemSelectedChangeListener<String> { selected, status ->
                selected.getOrNull(0)?.let { mainLabel ->
                    // 一级标签变动，更新二级标签
                    labels.filter { it.name == mainLabel }.getOrNull(0)?.let {
                        updateSubLabels(mainLabel, it.list)
                    }
                }
            }
            setLabelList(data = mainLabels, selectedData = listOf(mainLabels[0]))
        }
        findViewById<AppCompatButton>(R.id.btn_print).setOnClickListener {
            findViewById<AppCompatTextView>(R.id.tv_print).text = "$mySelected"
        }
        findViewById<AppCompatTextView>(R.id.tv_sub_select_all).apply {
            setOnClickListener {
                if (!selectAll) {
                    this@FilterLabelActivity.findViewById<LabelLayout>(R.id.sub_label).selectedAll()
                    text = "取消全选"
                } else {
                    this@FilterLabelActivity.findViewById<LabelLayout>(R.id.sub_label).clearAllSelected()
                    text = "全选"
                }
                selectAll = !selectAll
            }
        }
    }

    private fun updateSubLabels(mainLabel: String, subLabels: List<String>) {
        selectAll = false
        this@FilterLabelActivity.findViewById<AppCompatTextView>(R.id.tv_sub_select_all).text = "全选"
        findViewById<LabelLayout>(R.id.sub_label).apply {
            maxSelected = subLabels.size
            setLabelList(data = subLabels, selectedData = mySelected.find { it.name == mainLabel }?.list)
            setOnItemSelectedChangeListener<String> { selected, status ->
                // 不是初始化数据时的选中，保存选中的二级标签
                if (status !is LabelChangeStatus.INIT) {
                    mySelected.find { it.name == mainLabel }?.let {
                        mySelected.remove(it)
                        mySelected.add(Options(mainLabel, mutableListOf<String>().also { it.addAll(selected) }))
                    } ?: kotlin.run {
                        mySelected.add(Options(mainLabel, mutableListOf<String>().also { it.addAll(selected) }))
                    }
                }
                this@FilterLabelActivity.findViewById<AppCompatTextView>(R.id.tv_sub_select_all).text = if (selected.size == subLabels.size) {
                    selectAll = true
                    "取消全选"
                } else {
                    selectAll = false
                    "全选"
                }
            }
        }
    }

}