package com.example.tinymainpage.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.tinymainpage.constant.Constant
import com.example.tinymainpage.utils.NetWorkUtil
import com.example.tinymainpage.utils.TinyMMKV.Companion.mmkv
import com.jeremyliao.liveeventbus.LiveEventBus

class NetworkChangeReceiver : BroadcastReceiver() {

    private var hasNetwork = mmkv.decodeBool(Constant.HAS_NETWORK_KEY, true)

    override fun onReceive(context: Context, intent: Intent) {
        val isConnected = NetWorkUtil.isNetworkConnected(context)
        if (isConnected) {
            if (!hasNetwork) {
                LiveEventBus.get("isConnected").post(isConnected)
            }
        } else {
            //同时开启WIFI和数据的时候，关闭WIFI可能会造成短时间断网，所以这里再判断一次
            if (!NetWorkUtil.isNetworkConnected(context)) {
                LiveEventBus.get("isConnected").post(isConnected)
            }
        }
        hasNetwork = isConnected
    }
}