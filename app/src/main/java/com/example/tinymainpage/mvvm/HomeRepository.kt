package com.example.tinymainpage.mvvm

import com.example.tinymainpage.httpUtils.*


class HomeRepository : CommonRepository() {

    suspend fun getBanner(): ResponseData<List<Banner>> = request {
        RetrofitClient.service.getBanners()
    }

    suspend fun getArticles(page: Int): ResponseData<ArticleResponseBody> = request {
        RetrofitClient.service.getArticles(page)
    }

    suspend fun getTopArticles(): ResponseData<MutableList<Article>> = request {
        RetrofitClient.service.getTopArticles()
    }
}