package com.example.tinymainpage.base

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.tinymainpage.utils.toast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import retrofit2.HttpException

abstract class BaseViewModelActivity<VM : BaseViewModel> : BaseActivity() {

    protected lateinit var viewModel: VM

    //viewModel实例
    abstract fun providerVMClass(): Class<VM>

    override fun onCreate(savedInstanceState: Bundle?) {
        initVM()
        super.onCreate(savedInstanceState)
        startObserve()
        startHttp()
    }

    //初始化ViewModel,并监听生命周期
    private fun initVM() {
        providerVMClass().let {
            viewModel = ViewModelProvider(this).get(it)
            lifecycle.addObserver(viewModel)
        }
    }

    private fun startObserve() {
        //处理一些通用异常，比如网络超时等
        viewModel.run {
            getError().observe(this@BaseViewModelActivity, {
                requestError(it)
            })
            getFinally().observe(this@BaseViewModelActivity, {
                requestFinally(it)
            })
        }
    }

    //接口请求完毕，子类可以重写此方法做一些操作
    open fun requestFinally(it: Int?) {}

    //接口请求出错，子类可以重写此方法做一些操作
    open fun requestError(it: Exception?) {
        //处理一些已知异常
        it?.run {
            when (it) {
                is CancellationException -> Log.d("${TAG}--->接口请求取消", it.message.toString())
                is TimeoutCancellationException -> toast("请求超时")
                is HttpException -> {
                    if (it.code() == 504) toast("无法连接服务器,请检查网络设置")
                    else toast(it.message.toString())
                }
                else -> toast(it.message.toString())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(viewModel)
    }
}