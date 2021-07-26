package com.example.tinymainpage.utils

import com.tencent.mmkv.MMKV

class TinyMMKV {
    companion object {
        private const val fileName = "TinyMainPageApplicationData"

        /**
         * 初始化mmkv
         */
        val mmkv: MMKV
            get() = MMKV.mmkvWithID(fileName)

        /**
         * 删除数据(传参按key删除，否则全删)
         */
        fun deleteKeyOrAll(key: String? = null) {
            if (key == null) mmkv.clearAll()
            else mmkv.removeValueForKey(key)
        }

        /** 查询某个key是否已经存在
         * @param key
         * @return
         */
        fun contains(key: String) = mmkv.contains(key)
    }
}