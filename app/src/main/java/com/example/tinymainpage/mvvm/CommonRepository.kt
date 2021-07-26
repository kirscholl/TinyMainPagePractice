package com.example.tinymainpage.mvvm

import com.example.tinymainpage.httpUtils.ResponseData
import com.example.tinymainpage.httpUtils.RetrofitClient
import com.example.tinymainpage.utils.BaseRepository

open class CommonRepository : BaseRepository() {
    suspend fun logout(): ResponseData<Any> = request {
        RetrofitClient.service.logout()
    }
}