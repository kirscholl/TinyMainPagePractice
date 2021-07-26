package com.example.tinymainpage

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.ashokvarma.bottomnavigation.ShapeBadgeItem
import com.ashokvarma.bottomnavigation.TextBadgeItem
import com.example.tinymainpage.base.BaseActivity
import com.example.tinymainpage.constant.Constant
import com.example.tinymainpage.receiver.NetworkChangeReceiver
import com.example.tinymainpage.ui.HomeFragment
import com.example.tinymainpage.utils.*
import com.jeremyliao.liveeventbus.LiveEventBus
import kotlinx.android.synthetic.main.main_page.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class MainPageActivity : BaseActivity(), BottomNavigationBar.OnTabSelectedListener{

    companion object {
        const val REQ_CODE_INIT_API_KEY = 666
    }

    /**
     * 网络状态变化的广播
     */
    private var mNetworkChangeReceiver: NetworkChangeReceiver? = null

    // 定义是否退出程序的标记
    private var isExit = 0L
    private lateinit var viewPager: ViewPager
    private lateinit var fragments: ArrayList<Fragment> // fragment集合
    private lateinit var bottomNavigationBar: BottomNavigationBar
    private lateinit var titleList: MutableList<String>
    private var pos = 0

    /**
     * 权限申请
     */
    private val permissionArray = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    private var mPermissionList = arrayListOf<String>()
    private lateinit var mPermissionDialog: AlertDialog

    /**
     * newInstance并不能保证fragment是同一个，可能会重新new一个，所以这里设置一下
     */
    //todo 后续添加其他的Fragment模块
    private val homeFragment = HomeFragment.newInstance()


    inner class MainPagerAdapter : FragmentPagerAdapter(supportFragmentManager) {
        override fun getCount() = fragments.size

        override fun getItem(position: Int) = when (position) {
            0 -> homeFragment
            else -> homeFragment
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            super.destroyItem(container, position, `object`)
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //覆盖跳转动画
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        setTop(SettingUtil.getDefaultPage(), R.drawable.search)
        toolbar_title.setTextColor(ContextCompat.getColor(this,R.color.white))
        toolbar_left_image_back.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.group))

        toolbar_left_image_back.setOnClickListener {
            toast("Open GroupActivity")
        }
        toolbar_subtitle_image.setOnClickListener {
            toast("Open SearchActivity")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        var hasPermissionDismiss = false
        if(requestCode == REQ_CODE_INIT_API_KEY){
            for (i in grantResults.indices){
                if(grantResults[i] != PERMISSION_GRANTED){
                    hasPermissionDismiss = true
                }
            }
            if (hasPermissionDismiss) showPermissionDialog()
        }
    }

    private fun showPermissionDialog() {
        if (!this::mPermissionDialog.isInitialized) {
            mPermissionDialog = AlertDialog.Builder(this)
                .setMessage("权限申请失败")
                .setPositiveButton("去设置") {_, _ ->
                    mPermissionDialog.dismiss() // 隐藏控件，并释放资源（hide方法不会释放资源）
                    // 跳转到引用设置
                    val packageURI = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)
                    startActivity(intent)
                    this.finish()
                }.setNegativeButton("取消") {_, _ ->
                    mPermissionDialog.dismiss()
                    this.finish()
                }.setCancelable(false).create()
            mPermissionDialog.show()
        }
    }

    override fun getLayoutId(): Int {
       return R.layout.main_page
    }

    //父类BasicActivity的onCreate方法自动调用
    override fun initData() {
        initPermission()
    }

    private fun initPermission(){
        //清空没有通过的权限
        mPermissionList.clear()

        for (i in permissionArray.indices) {
            if (!PermissionUtils.checkPermission(this, permissionArray[i])) {
                mPermissionList.add(permissionArray[i])
            }
        }

        if (mPermissionList.size > 0) {
            PermissionUtils.requestPermissions(this, permissionArray, REQ_CODE_INIT_API_KEY)
        }
    }

    //父类BaseActivity的onCreate方法自动调用
    override fun initView() {
        initViewPager()
        initNavigationBar()
    }

    // 未设计相关Fragment，暂时无法跳转
    private fun initViewPager() {
        titleList = mutableListOf()
        titleList.run {
            add(resources.getString(R.string.home))
            add(resources.getString(R.string.system))
            add(resources.getString(R.string.weixin))
            add(resources.getString(R.string.question))
            add(resources.getString(R.string.my))
        }
        fragments = ArrayList()
        fragments.run {
            add(homeFragment)
        }

        //初始化ViewPager,这时只能用viewPager,不能用viewPager2.viewPage2会引起滑动冲突，不好解决
        viewPager = vp2_fragment
        viewPager.run {
            adapter = MainPagerAdapter()
            // 设置viewPager的预加载页面
            offscreenPageLimit = fragments.size
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled( // 滑动中
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {}

                override fun onPageSelected(position: Int) { // 页面改变（滑动停止后生效）
                    pos = position
                    if (position == fragments.size - 1) toolbar.visibility =
                        View.GONE else toolbar.visibility = View.VISIBLE
                    toolbar_title.text = titleList[position]
                    bottom_bar.selectTab(position)
                }

                override fun onPageScrollStateChanged(state: Int) {} // 页面改变（每次状态改变就触发）
            })
        }
    }

    private fun initNavigationBar() {
        bottomNavigationBar = bottom_bar // 底部导航栏
        val homeBadge = TextBadgeItem()
            .setBorderWidth(1)
            .setText(resources.getString(R.string.zero))
        homeBadge.hide() // 设置导航栏的文本角标（如微信中出现未读消息时，导航栏上的红点）
        LiveEventBus.get("homeBadge", Int::class.java).observe(this, {
            if (!SettingUtil.getIsShowBadge()) {
                homeBadge.hide()
                return@observe
            }
            when { // key = homeBadge， value = it， it表示新信息数目
                it == 0 -> homeBadge.hide()
                it > 99 -> {
                    homeBadge.show()
                    homeBadge.setText(resources.getString(R.string.exceed_99))
                }
                else -> {
                    homeBadge.show()
                    homeBadge.setText(it.toString())
                }
            }
        })

        // 设置底部导航栏的角标的形状样式（原型，10dp，10dp）
        val myBadge = ShapeBadgeItem().setShape(ShapeBadgeItem.SHAPE_OVAL).setSizeInDp(this, 10, 10)
        myBadge.hide()
        LiveEventBus.get("myBadge", Boolean::class.java).observe(this, {
            if (!SettingUtil.getIsShowBadge()) {
                myBadge.hide()
                return@observe
            }
            // 如果已经登录了，且可以显示角标，则开启显示
            if (it && TinyMMKV.mmkv.encode(Constant.IS_LOGIN, true)) myBadge.show()
            else myBadge.hide()
        })

        // MODE_FIXED 未选中item会显示文字，但不会有动画
        // MODE_SHIFTING 未选中的item不会显示文字，切换时有动画
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_SHIFTING)
            // BACKGROUND_STYLE_STATIC 点击无水波
            // BACKGROUND_STYLE_RIPPLE 点击有水波
            .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
            .addItem(BottomNavigationItem(R.drawable.home, titleList[0]).setBadgeItem(homeBadge)) // 指定角标
            .addItem(BottomNavigationItem(R.drawable.classification, titleList[1]))
            .addItem(BottomNavigationItem(R.drawable.wechat, titleList[2]))
            .addItem(BottomNavigationItem(R.drawable.message, titleList[3]))
            .addItem(BottomNavigationItem(R.drawable.me, titleList[4]).setBadgeItem(myBadge))
            .setActiveColor(R.color.Light_Blue)//图标和文本未被激活或选中的颜色，默认颜色为Color.LTGRAY
            .setInActiveColor(R.color.grey_search)//图标和文本激活或选中的颜色，默认颜色为Theme’s Primary Color
            .setBarBackgroundColor(R.color.white)//整个空控件的背景色，默认颜色为Color.WHITE
            .setTabSelectedListener(this)//回调方法
            .setFirstSelectedPosition(SettingUtil.getDefaultPage(this))
            .initialise()
        // 统一item的形状大小，字体大小等（非必要）
//        setBottomNavigationItem(bottomNavigationBar, 2, 22, 12)
        viewPager.currentItem = bottomNavigationBar.currentSelectedPosition
    }

    // 当请求为拍照、从相册中选取或todo时，调用myFragment中的onActivityResult方法（待完成）
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.IMAGE_CAPTURE || requestCode == Constant.IMAGE_SELECT || requestCode == Constant.FROM_TODO) {
//            myFragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onResume() {
        super.onResume()

        val filter = IntentFilter() // 动态注册，监听网络变化广播
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        mNetworkChangeReceiver = NetworkChangeReceiver()
        registerReceiver(mNetworkChangeReceiver, filter) // 注册广播接收器
    }

    override fun onPause() {
        super.onPause()
        if (mNetworkChangeReceiver != null) {
            unregisterReceiver(mNetworkChangeReceiver)
            mNetworkChangeReceiver = null
        }
    }

    // 设置两次点击返回按钮退出应用的功能
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 判断是否点击返回键
            if (System.currentTimeMillis() - isExit <= 2000) { // 如果没在2s内再次点击，则标记用户为不退出状态
                this.finish()
            } else {
                isExit = System.currentTimeMillis()
                toast("再按一次退出应用")
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun startHttp() {

    }

    override fun onTabSelected(position: Int) {
        viewPager.currentItem = position
    }

    override fun onTabUnselected(position: Int) {

    }

    override fun onTabReselected(position: Int) {

    }
}