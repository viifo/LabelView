package com.viifo.labelview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class LabelLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    /** 流式布局时 item 是否填充行剩余空间 */
    private var flexGrow: Boolean = false
    /**
     * 流式布局时 item 的对齐方式
     * {@link com.google.android.flexbox.JustifyContent }
     */
    private var justifyContent: Int = 0
    /** item 布局资源 ID */
    private var labelItemRes: Int = 0
    /** 每行显示的 item 个数, 0 为自适应 */
    private var spanCount: Int = 0
    /** 是否支持多选 */
    private var multiChoice: Boolean = false
    /** 最大可选数量(开启多选有效) */
    private var maxSelected: Int = 1
    /** 最小可选数量(开启多选有效) */
    private var minSelected: Int = 1

    private var itemSelectedChangeListener: ((item: List<*>) -> Unit)? = null

    /**
     * 初始化属性参数
     * @param context
     * @param attrs
     */
    @SuppressLint("CustomViewStyleable")
    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.LabelLayout).apply {
            flexGrow = getBoolean(R.styleable.LabelLayout_flexGrow, false)
            justifyContent = getInt(R.styleable.LabelLayout_justifyContent, JustifyContent.FLEX_START)
            labelItemRes = getResourceId(R.styleable.LabelLayout_labelItem, R.layout.label_view_item_default)
            spanCount = getInt(R.styleable.LabelLayout_spanCount, 0)
            multiChoice = getBoolean(R.styleable.LabelLayout_multiChoice, false)
            maxSelected = getInt(R.styleable.LabelLayout_maxSelected, 1)
            minSelected = getInt(R.styleable.LabelLayout_minSelected, 1)
        }.recycle()
    }

    private fun initLabelLayout(context: Context) {
        overScrollMode = OVER_SCROLL_NEVER
        layoutManager = if (spanCount <= 0) {
            object : FlexboxLayoutManager(context) {
                override fun canScrollVertically() = false
            }.also {
                it.flexDirection = FlexDirection.ROW
                it.flexWrap = FlexWrap.WRAP
                it.justifyContent = justifyContent
            }
        } else {
            object : GridLayoutManager(context, spanCount) {
                override fun canScrollVertically() = false
            }
        }
    }

    fun <T : Any> setOnItemSelectedChangeListener(
        listener: ((item: List<T>) -> Unit)? = null
    ) {
        itemSelectedChangeListener = listener as ((item: List<*>) -> Unit)?
    }

    fun <T : Any> bindView(
        converter: ((holder: LabelHolder, item: T, selected: Boolean) -> Unit)? = null
    ) {
        swapAdapter(
            LabelAdapter<T>(labelItemRes).also { labelAdapter ->
                labelAdapter.onItemClickListener = { item ->
                    labelAdapter.reverseSelectedData(item)
                    itemSelectedChangeListener?.invoke(labelAdapter.selectedData)
                }
                labelAdapter.onBindViewListener = converter
            },
            false
        )
    }

    fun <T : Any> setLabelList(
        data: List<T>?,
        selectedData: List<T>? = null,
        converter: ((holder: LabelHolder, item: T, selected: Boolean) -> Unit)? = null,
    ) {
        if (converter != null || adapter == null) { bindView(converter) }
        (adapter as LabelAdapter<T>).setData(data, selectedData)
    }

    fun <T : Any> selectedItem(item: T) {
        (adapter as LabelAdapter<T>).addSelectedData(item)
    }

    fun <T : Any> selectedItems(items: List<T>?) {
        (adapter as LabelAdapter<T>).let { adapter ->
            items?.forEach {
                adapter.addSelectedData(it)
            }
        }
    }

    fun <T : Any> selectedPosition(position: Int) {
        (adapter as LabelAdapter<T>).let {
            it.addSelectedData(it.data[position])
        }
    }

    init {
        initAttrs(context, attrs)
        initLabelLayout(context)
    }

}