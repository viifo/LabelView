package com.viifo.labelview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class LabelAdapter<T: Any>(
    @LayoutRes private val layoutRes: Int
) : RecyclerView.Adapter<LabelHolder>() {

    private var _data = listOf<T>()
    val data: List<T> get() = _data
    private var _selectedData = mutableListOf<T>()
    val selectedData: List<T> get() = _selectedData
    var onItemClickListener: ((T) -> Unit)? = null
    var onBindViewListener: ((holder: LabelHolder, item: T, selected: Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LabelHolder(
        LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
    ).also { holder ->
        onItemClickListener?.let {  listener ->
            holder.itemView.setOnClickListener {
                listener(data[holder.adapterPosition])
            }
        }
    }

    override fun onBindViewHolder(holder: LabelHolder, position: Int) {
        val item = data[position]
        val selected = selectedData.contains(item)
        onBindViewListener?.invoke(holder, item, selected) ?: kotlin.run {
            (holder.itemView as TextView).let {
                it.text = item as String
                it.setBackgroundResource(
                    if (selected) R.drawable.label_view_shape_hover
                    else R.drawable.label_view_shape_normal
                )
                it.setTextColor(
                    ContextCompat.getColor(
                        it.context,
                        if (selected) R.color.labelLayout_textColorHover
                        else R.color.labelLayout_textColorNormal
                    )
                )
            }
        }
    }

    override fun getItemCount() = data.size

    fun getItemPosition(item: T?): Int {
        return if (item != null && data.isNotEmpty()) data.indexOf(item) else -1
    }

    fun addSelectedData(defSelected: T? = null, cover: Boolean = false) {
        defSelected?.let {
            if (cover || !selectedData.contains(it)) {
                _selectedData.add(it)
                notifyItemChanged(getItemPosition(it))
            }
        }
    }

    fun removeSelectedData(defSelected: T? = null) {
        defSelected?.let {
            _selectedData.remove(it)
            notifyItemChanged(getItemPosition(it))
        }
    }

    fun reverseSelectedData(defSelected: T? = null) {
        defSelected?.let {
            if (selectedData.contains(it)) removeSelectedData(it)
            else addSelectedData(it, true)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: List<T>?, defSelected: List<T>? = null) {
        _data = items ?: emptyList()
        _selectedData.clear()
        defSelected?.forEach {
            if (data.contains(it)) {
                _selectedData.add(it)
            }
        }
        notifyDataSetChanged()
    }
}