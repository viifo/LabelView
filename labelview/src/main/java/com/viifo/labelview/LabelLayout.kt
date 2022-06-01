package com.viifo.labelview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class LabelLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    /** 标签布局 - 默认为流式布局 */
    private var labelMode: LabelMode = LabelMode.FLEX
    /** 标签方向 - 默认为 水平 | 行 */
    private var orientation: Orientation = Orientation.HORIZONTAL
    /** item 布局资源 ID */
    private var itemLayout: Int = 0
    /** 是否开启 item 动画 */
    private var enableItemAnimator: Boolean = false
    /** 是否支持多选 */
    var multiChoice: Boolean = false
    /** 最大可选数量(开启多选有效) */
    var maxSelected: Int = 1
    /** 是否支持滚动 */
    var scrollable: Boolean = false

    /******* 流式布局使用参数 ******/
    /** 流式布局时 item 是否填充行剩余空间 */
    private var flexGrow: Boolean = false
    /**
     * 流式布局时 item 的对齐方式
     * {@link com.google.android.flexbox.JustifyContent }
     */
    private var justifyContent: Int = 0

    /******* 网格布局使用参数 ******/
    /** 每行显示的 item 个数 */
    private var spanCount: Int = 0

    /** item 选中状态改变监听器 */
    private var itemSelectedChangeListener: ((selected: List<*>, status: LabelChangeStatus) -> Unit)? = null

    /**
     * 初始化属性参数
     * @param context
     * @param attrs
     */
    @SuppressLint("CustomViewStyleable")
    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.LabelLayout).apply {

            labelMode = LabelMode.valueOf(getInteger(R.styleable.LabelLayout_mode, 0))
            orientation = Orientation.valueOf(getInteger(R.styleable.LabelLayout_orientation, 0))
            itemLayout = getResourceId(R.styleable.LabelLayout_itemLayout, R.layout.label_view_item_default)
            enableItemAnimator = getBoolean(R.styleable.LabelLayout_enableItemAnimator, false)
            multiChoice = getBoolean(R.styleable.LabelLayout_multiChoice, false)
            maxSelected = getInt(R.styleable.LabelLayout_maxSelected, 1)
            scrollable = getBoolean(R.styleable.LabelLayout_scrollable, false)

            flexGrow = getBoolean(R.styleable.LabelLayout_flexGrow, false)
            justifyContent = getInt(R.styleable.LabelLayout_justifyContent, JustifyContent.FLEX_START)
            spanCount = getInt(R.styleable.LabelLayout_spanCount, 0)

        }.recycle()
    }

    private fun initLabelLayout(context: Context) {
        if (!enableItemAnimator) {
            // 关闭 item 动画
            itemAnimator = null
        }
        layoutManager = when(labelMode) {
            LabelMode.LINE -> object : LinearLayoutManager(context, orientation.value, false) {
                override fun canScrollHorizontally() = if (orientation == HORIZONTAL) scrollable else false
                override fun canScrollVertically()  = if (orientation == VERTICAL) scrollable else false
            }
            LabelMode.GRID -> object : GridLayoutManager(context, spanCount) {
                override fun canScrollVertically() = scrollable
            }
            else -> object : FlexboxLayoutManager(context) {
                override fun canScrollVertically() = scrollable
            }.also {
                it.flexDirection = if (orientation == Orientation.HORIZONTAL) FlexDirection.ROW else FlexDirection.COLUMN
                it.flexWrap = FlexWrap.WRAP
                it.justifyContent = justifyContent
            }
        }
    }

    fun <T : Any> setOnItemSelectedChangeListener(
        listener: ((selected: List<T>, status: LabelChangeStatus) -> Unit)? = null
    ) {
        itemSelectedChangeListener = listener as (((List<*>, LabelChangeStatus) -> Unit))?
        (adapter as LabelAdapter<T>?)?.onItemSelectedChangeListener = itemSelectedChangeListener
    }

    private fun <T : Any> bindView(
        converter: ((holder: LabelHolder, item: T, selected: Boolean) -> Unit)? = null
    ) {
        swapAdapter(
            LabelAdapter<T>(itemLayout, multiChoice, maxSelected).also { labelAdapter ->
                labelAdapter.onItemClickListener = { labelAdapter.reverseSelectedData(it) }
                labelAdapter.onItemSelectedChangeListener = itemSelectedChangeListener
                labelAdapter.onBindViewListener = converter
            },
            false
        )
    }

    /**
     * @param canOverflow 是否允许溢出选中集合，true - 允许选中集合中的 item 不在所有标签 item 中
     */
    fun <T : Any> setLabelList(
        data: List<T>?,
        selectedData: List<T>? = null,
        canOverflow: Boolean = false,
        converter: ((holder: LabelHolder, item: T, selected: Boolean) -> Unit)? = null,
    ) {
        if (converter != null || adapter == null) { bindView(converter) }
        (adapter as LabelAdapter<T>?)?.setData(data, selectedData, canOverflow)
    }

    fun <T : Any> selectedItem(item: T) {
        (adapter as LabelAdapter<T>?)?.addSelectedData(item)
    }

    fun selectedAll() {
        (adapter as LabelAdapter<*>?)?.selectedAll()
    }

    fun clearAllSelected() {
        (adapter as LabelAdapter<*>?)?.clearAllSelected()
    }

    fun <T : Any> selectedPosition(position: Int) {
        (adapter as LabelAdapter<T>?)?.let {
            it.addSelectedData(it.data[position])
        }
    }

    fun <T : Any> getSelectItems() = (adapter as LabelAdapter<T>?)?.selectedData ?: emptyList()

    fun <T : Any> getAllItems() = (adapter as LabelAdapter<T>?)?.data ?: emptyList()

    init {
        initAttrs(context, attrs)
        initLabelLayout(context)
    }

    /**
     * 布局方向枚举, 当布局模式为 flex 或 line 时可用
     */
    enum class Orientation(val value: Int) {
        HORIZONTAL(0), // 水平 | 行
        VERTICAL(1); // 垂直 | 列

        companion object {
            fun valueOf(value: Int) = when(value) {
                1 -> VERTICAL
                else -> HORIZONTAL
            }
        }
    }

    enum class LabelMode(val value: Int) {
        FLEX(0), // 流式布局
        GRID(1), // 网格布局
        LINE(1);  // 线性布局

        companion object {
            fun valueOf(value: Int) = when(value) {
                1 -> GRID
                2 -> LINE
                else -> FLEX
            }
        }
    }

}