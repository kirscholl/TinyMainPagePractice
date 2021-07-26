package com.example.tinymainpage.utils

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tinymainpage.base.TinyMainPageApplication


/**
 * 自定义Toast
 */
fun Context.toast(resId: Int) {
    val toast = Toast.makeText(this, resId, Toast.LENGTH_SHORT)
    toast.run {
        setGravity(Gravity.CENTER, 0, 0) // 修改Toast弹出位置
        show()
    }
}

fun Context.toast(text: CharSequence) {
    val toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
    toast.run {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

fun Context.longToast(resId: Int) {
    val toast = Toast.makeText(this, resId, Toast.LENGTH_LONG)
    toast.run {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

fun Context.longToast(text: CharSequence) {
    val toast = Toast.makeText(this, text, Toast.LENGTH_LONG)
    toast.run {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

fun View.toast(resId: Int) = context.toast(resId)

fun View.toast(text: CharSequence) = context.toast(text)

fun View.longToast(resId: Int) = context.longToast(resId)

fun View.longToast(text: CharSequence) = context.longToast(text)

fun Fragment.toast(resId: Int) = (activity ?: TinyMainPageApplication.mContext).toast(resId)

fun Fragment.toast(text: CharSequence) = (activity ?: TinyMainPageApplication.mContext).toast(text)

fun Fragment.longToast(resId: Int) = (activity ?: TinyMainPageApplication.mContext).longToast(resId)

fun Fragment.longToast(text: CharSequence) = (activity ?: TinyMainPageApplication.mContext).longToast(text)
