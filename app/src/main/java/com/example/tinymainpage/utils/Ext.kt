package com.example.tinymainpage


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.webkit.WebView
import com.example.tinymainpage.constant.Constant
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 得到一个自定义的AgentWebView
 */
fun getAgentWebView(
    url: String,
    activity: Activity,
    webContent: ViewGroup,
    layoutParams: ViewGroup.LayoutParams,
    webView: WebView,
    webViewClient: com.just.agentweb.WebViewClient?,
    webChromeClient: com.just.agentweb.WebChromeClient?,
    indicatorColor: Int
): AgentWeb {
    return AgentWeb.with(activity)
        .setAgentWebParent(webContent, 1, layoutParams)//传入AgentWeb 的父控件
        .useDefaultIndicator(indicatorColor, 2)// 使用默认进度条
        .setWebView(webView)
        .setWebViewClient(webViewClient)
        .setWebChromeClient(webChromeClient)
        .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
        .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
        .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//打开其他应用时，弹窗咨询用户是否前往其他应用
        .interceptUnkownUrl()
        .createAgentWeb()//
        .ready()
        .go(url)
}

/**
 * String 转 Calendar
 */
fun String.stringToCalendar(): Calendar {
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val date = sdf.parse(this)
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar
}