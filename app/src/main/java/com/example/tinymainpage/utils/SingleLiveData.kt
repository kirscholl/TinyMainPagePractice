package com.example.tinymainpage.utils

import androidx.annotation.MainThread
import androidx.annotation.Nullable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean


/**
 * 解决LiveData粘性事件，即发射的事件早于注册，那么注册之后依然可以收到的事件称为粘性事件
 * 原因：最终判断mLastVersion是否比mVersion大，如果大就会调用onChanged方法，即注册的事件
 * mLastVersion初始值为-1，属于ObserverWrapper，只有在比较完后才会赋值
 * mVersion初始值为-1，属于LiveData，只有在setValue（postValue最终也会调用setValue）时会+1
 * 所以，只要之前发射过一次数据，那么后面注册的观察者都会接收到之前发射的数据，且Version值不可轻易改变
 * 解决办法：
 * 重写LiveData类，重写Observer接口，使用mPending记录是否具有粘性
 */
class SingleLiveData<T> : MutableLiveData<T>() {

    private val mPending: AtomicBoolean = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, {
            if(mPending.compareAndSet(true, false)){
                observer.onChanged(it)
            }
        })
    }

    override fun setValue(@Nullable value: T?) {
        mPending.set(true)
        super.setValue(value)
    }

    @MainThread
    fun call(){
        setValue(null)
    }
}