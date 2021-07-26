package com.example.tinymainpage.base

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.tinymainpage.R
import com.example.tinymainpage.utils.TinyMMKV.Companion.mmkv
import com.jeremyliao.liveeventbus.LiveEventBus
import kotlinx.android.synthetic.main.toolbar_layout.*

abstract class BaseActivity : AppCompatActivity() {

    val TAG = javaClass.simpleName

    /**
     * 列表接口每页请求的条数
     */
    val pageSize = 20

    /**
     * 布局文件id
     */
    abstract fun getLayoutId(): Int

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 初始化 View
     */
    abstract fun initView()

    /**
     * 开始请求
     */
    abstract fun startHttp()

    /**
     * 无网状态—>有网状态 的自动重连操作，子类可重写该方法
     */
    open fun doReConnected() {
        // 使用LiveEventBus观测isConnected，判断网络是否已连接
        LiveEventBus.get("isConnected", Boolean::class.java).observe(this, {
            if (it) startHttp()
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //防止输入法顶起底部布局
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        super.onCreate(savedInstanceState)

        //0是默认值
        if (mmkv.decodeInt("max_size") == 0) {
            mmkv.encode("max_size", resources.displayMetrics.heightPixels)
        }

//        setWindowStatusTransparent(this) //设置状态栏透明

        if (getLayoutId() > 0) {
            // 如果不是启动页，则设置layoutID
            setContentView(getLayoutId())
        }

        initData()
        initView()

        // 重新连接网络
        doReConnected()
    }

    open fun setTop(title: String, subTitle: Any? = null, isBack: (() -> Unit)? = {
        toolbar_left_image_back?.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.write_back)
        )
        toolbar_left_image_back?.setOnClickListener { onBackPressed() }
    })
    {
        toolbar_title?.text = title
        toolbar_title?.isSelected = true

        //默认显示返回按钮
        isBack?.invoke()

        //根据subtitle的数据类型来显示图片或文字
        when (subTitle) {
            is String -> {
                toolbar_subtitle_image?.visibility = View.GONE
                toolbar_subtitle?.visibility = View.VISIBLE
                toolbar_subtitle?.text = subTitle
            }
            is Int -> {
                toolbar_subtitle?.visibility = View.GONE
                toolbar_subtitle_image?.visibility = View.VISIBLE
                toolbar_subtitle_image?.setImageResource(subTitle)
            }
            else -> {
                toolbar_subtitle?.visibility = View.GONE
                toolbar_subtitle_image?.visibility = View.GONE
            }
        }
    }
}