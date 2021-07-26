package com.example.tinymainpage.utils

import android.content.Context
import android.util.AttributeSet
import com.scwang.smart.refresh.header.MaterialHeader

/**
 * 设置自定义的ClassicsHeader样式
 */
class MyRefreshHeader(context: Context, attrs: AttributeSet? = null) :
//    ClassicsHeader(context, attrs) { // 经典下拉刷新头
//    BezierRadarHeader(context, attrs) { // 雷达下拉刷新头
    MaterialHeader(context, attrs) { // 谷歌下拉刷新头


    init {
//        setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
//        setPrimaryColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
//        setAccentColor(ContextCompat.getColor(context, R.color.white))
    }
}