package com.example.tinymainpage.webView

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import com.example.tinymainpage.R
import com.example.tinymainpage.base.BaseViewModelActivity
import com.example.tinymainpage.constant.Constant
import com.example.tinymainpage.getAgentWebView
import com.example.tinymainpage.mvvm.CommonViewModel
import com.just.agentweb.AgentWeb
import com.just.agentweb.AgentWebView
import com.just.agentweb.WebViewClient
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.view_popupwindow.view.*

class WebViewActivity : BaseViewModelActivity<CommonViewModel>() {

    private var mAgentWeb: AgentWeb? = null
    private lateinit var webView: WebView
    private lateinit var mWebClient: WebViewClient
    private var shareTitle: String = ""
    private var shareUrl: String = ""
    private var shareId: Int = -1
    private lateinit var popWindow: PopupWindow

    companion object {

        const val JIAN_SHU = "https://www.jianshu.com"

        fun start(context: Context?, id: Int, title: String, url: String, bundle: Bundle? = null) {
            Intent(context, WebViewActivity::class.java).run {
                putExtra(Constant.CONTENT_ID_KEY, id)
                putExtra(Constant.CONTENT_TITLE_KEY, title)
                putExtra(Constant.CONTENT_URL_KEY, url)
                context?.startActivity(this, bundle)
            }
        }

        fun start(context: Context?, url: String) {
            start(context, -1, "", url)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_web_view

    override fun providerVMClass() = CommonViewModel::class.java

    override fun initData() {}

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        intent.extras?.let {
            shareId = it.getInt(Constant.CONTENT_ID_KEY, -1)
            shareTitle = it.getString(Constant.CONTENT_TITLE_KEY, "")
            shareUrl = it.getString(Constant.CONTENT_URL_KEY, "")
        }
        setTop(shareTitle, R.drawable.points)
        toolbar_subtitle_image.setOnClickListener {
            if (!this@WebViewActivity::popWindow.isInitialized) initPopWindow(it)
            else if (popWindow.isShowing) popWindow.dismiss()
            else popWindow.showAsDropDown(it, 0, 35)
        }
        initWebView()
    }

    private fun initWebView() {
        webView = AgentWebView(this)
        val layoutParams = LinearLayout.LayoutParams(-1, -1)
        mWebClient = if (shareUrl.startsWith(JIAN_SHU)) JianShuWebClient() else BaseWebClient()
        mAgentWeb = getAgentWebView(
            shareUrl, this, ll_webView, layoutParams,
            webView,
            mWebClient,
            mWebChromeClient,
            resources.getColor(R.color.colorPrimary)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AgentWebView.setWebContentsDebuggingEnabled(true)
        }
        mAgentWeb?.webCreator?.webView?.apply {
            overScrollMode = WebView.OVER_SCROLL_NEVER
            settings.domStorageEnabled = true
            settings.javaScriptEnabled = true
            settings.loadsImagesAutomatically = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
        }
    }

    private val mWebChromeClient = object : com.just.agentweb.WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            toolbar_title.text = title
        }

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }
    }

    override fun onBackPressed() {
        mAgentWeb?.let {
            if (!it.back()) {
                super.onBackPressed()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (mAgentWeb?.handleKeyEvent(keyCode, event)!!) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        mAgentWeb?.webLifeCycle?.onResume()
        super.onResume()
    }

    override fun onPause() {
        mAgentWeb?.webLifeCycle?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mAgentWeb?.webLifeCycle?.onDestroy()
        super.onDestroy()
    }

    override fun startHttp() {}

    @SuppressLint("ClickableViewAccessibility")
    private fun initPopWindow(view: View) {
        val popView = LayoutInflater.from(this).inflate(R.layout.view_popupwindow, null, false)
        popView.tv_pop_all.text = "分享"
        popView.tv_pop_done.text = "收藏"
        popView.tv_pop_todo.text = "浏览器打开"
        popView.tv_pop_read_later.visibility = View.VISIBLE
        popView.tv_pop_read_later.text = "稍后阅读"
        popWindow = PopupWindow(
            popView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popWindow.run {
            animationStyle = R.anim.fade_in
            isTouchable = true
            setTouchInterceptor { view, motionEvent ->
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return@setTouchInterceptor false
            }
            setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.white)))
            showAsDropDown(view, 0, 35)
        }
    }
}