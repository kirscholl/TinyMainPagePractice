package com.example.tinymainpage.adapter

import android.content.Context
import com.example.tinymainpage.httpUtils.Banner
import com.example.tinymainpage.utils.ImageLoader
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder

/**
 * Banner适配器
 */
open class ImageAdapter(private val context: Context, imgList: List<Banner>) :
    BannerImageAdapter<Banner>(imgList) {

    override fun onBindView(holder: BannerImageHolder?, data: Banner?, position: Int, size: Int) {
        ImageLoader.loadBanner(context, data?.imagePath, holder?.imageView)
    }
}