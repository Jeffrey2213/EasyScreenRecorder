package com.example.myapplication

import android.view.View

interface ItemClick {
    fun OnItemClick(v: View, position : Int)
    fun OnItemLongClick(v: View, position : Int)
}