package com.viifo.labelview.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.doOnTextChanged
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

    private lateinit var mainLabel: LabelLayout
    private lateinit var subLabel: LabelLayout
    private lateinit var btnPrint: AppCompatButton
    private lateinit var tvPrint: AppCompatTextView
    private lateinit var tvSubSelectAll: AppCompatTextView
    private lateinit var etSearch: AppCompatEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_label)
        mainLabel = findViewById(R.id.main_label)
        subLabel = findViewById(R.id.sub_label)
        btnPrint = findViewById(R.id.btn_print)
        tvPrint = findViewById(R.id.tv_print)
        tvSubSelectAll = findViewById(R.id.tv_sub_select_all)
        etSearch = findViewById(R.id.et_search)

        mainLabel.apply {
            val mainLabels = labels.map { it.name }
            setOnItemSelectedChangeListener<String> { selected, status ->
                selected.getOrNull(0)?.let { mainLabel ->
                    // ???????????????????????????????????????
                    labels.filter { it.name == mainLabel }.getOrNull(0)?.let {
                        updateSubLabels(mainLabel, it.list)
                    }
                }
            }
            setLabelList(data = mainLabels, selectedData = listOf(mainLabels[0]))
        }
        btnPrint.setOnClickListener {
            tvPrint.text = "$mySelected"
        }
        tvSubSelectAll.apply {
            setOnClickListener {
                if (!selectAll) {
                    subLabel.selectedAll()
                    text = "????????????"
                } else {
                    subLabel.clearAllSelected()
                    text = "??????"
                }
            }
        }
        etSearch.doOnTextChanged { text, _, _, _ ->
            val mainLabelName = mainLabel.getSelectItems<String>().getOrNull(0) ?: ""
            labels.filter { it.name == mainLabelName }.getOrNull(0)?.let {
                updateSubLabels(
                    mainLabelName,
                    it.list.filter { it.contains(text.toString()) }
                )
            }
        }
    }

    private fun updateSubLabels(mainLabel: String, subLabels: List<String>) {
        selectAll = false
        tvSubSelectAll.text = "??????"
        subLabel.apply {
            maxSelected = subLabels.size
            setLabelList(data = subLabels, selectedData = mySelected.find { it.name == mainLabel }?.list, canOverflow = true)
            setOnItemSelectedChangeListener<String> { selected, status ->
                // ???????????????????????????????????????????????????????????????
                if (status !is LabelChangeStatus.INIT) {
                    mySelected.find { it.name == mainLabel }?.let {
                        mySelected.remove(it)
                        mySelected.add(Options(mainLabel, mutableListOf<String>().also { it.addAll(selected) }))
                    } ?: kotlin.run {
                        mySelected.add(Options(mainLabel, mutableListOf<String>().also { it.addAll(selected) }))
                    }
                }
                tvSubSelectAll.text = if (selected.size == subLabels.size) {
                    selectAll = true
                    "????????????"
                } else {
                    selectAll = false
                    "??????"
                }
            }
        }
    }

}