package com.viifo.labelview

sealed class LabelChangeStatus {
    object INIT : LabelChangeStatus()
    data class REMOVE(val item: List<*>) : LabelChangeStatus()
    data class ADD(val item: List<*>) : LabelChangeStatus()
}