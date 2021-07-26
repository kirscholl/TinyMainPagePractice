package com.example.tinymainpage.utils

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.tinymainpage.R
import com.example.tinymainpage.base.TinyMainPageApplication

object ImageLoader {

    /**
     * 加载图片
     * @param context
     * @param url
     * @param iv
     */
    fun load(context: Context?, url: String?, iv: ImageView?) {
        // 1.开启无图模式 2.非WiFi环境 不加载图片
        if (!SettingUtil.getIsNoPhotoMode() || NetWorkUtil.isWifi(TinyMainPageApplication.mContext)) {
            iv?.apply {
                visibility = View.VISIBLE
                Glide.with(context ?: TinyMainPageApplication.mContext).clear(iv)
                val options = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .placeholder(R.drawable.bg_placeholder)
                Glide.with(context ?: TinyMainPageApplication.mContext)
                    .load(url)
                    .transition(DrawableTransitionOptions().crossFade())
                    .apply(options)
                    .into(iv)
            }
        } else {
            iv?.visibility = View.GONE
        }
    }

    /**
     * 开启了无图模式也要加载图片的情况下使用
     */
    fun loadBanner(context: Context?, url: String?, iv: ImageView?) {
        iv?.apply {
            Glide.with(context ?: TinyMainPageApplication.mContext).clear(iv)
            val options = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(R.drawable.bg_placeholder)
            Glide.with(context ?: TinyMainPageApplication.mContext)
                .load(url)
                .transition(DrawableTransitionOptions().crossFade())
                .apply(options)
                .into(iv)
        }
    }
}