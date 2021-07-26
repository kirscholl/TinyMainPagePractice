package com.example.tinymainpage.mvvm

import androidx.lifecycle.LiveData
import com.example.tinymainpage.httpUtils.Article
import com.example.tinymainpage.httpUtils.Banner
import com.example.tinymainpage.utils.*

class HomeViewModel : CommonViewModel() {

    private val repository = HomeRepository()
    private val bannerDatas = SingleLiveData<List<Banner>>()
    private val articlesDatas = SingleLiveData<List<Article>>()
    private var topArticlesDatas = SingleLiveData<List<Article>>()
    private var topArticlesList = mutableListOf<Article>()
    private var articlesList = mutableListOf<Article>()

    fun getBanner(): LiveData<List<Banner>> {
        launchUI {
            val result = repository.getBanner()
            bannerDatas.value = result.data
        }
        return bannerDatas
    }

    fun getArticles(page: Int): LiveData<List<Article>> {
        launchUI {
            val result = repository.getArticles(page)
            articlesDatas.value = result.data.datas
        }
        return articlesDatas
    }

    fun getArticlesAndTopArticles(page: Int): LiveData<List<Article>> {
        //只在第一页加载置顶数据，其余页直接加载文章列表
        return if (page == 0) {
            launchUI { // 开协程作用域，将置顶文章标记为top=1
                topArticlesList = repository.getTopArticles().data
                topArticlesList.forEach {
                    it.top = "1"
                }
            }
            launchUI { // 开协程作用域，将其他文章加入置顶文章列表中
                articlesList = repository.getArticles(0).data.datas
                topArticlesList.addAll(articlesList)
                topArticlesDatas.value = topArticlesList
            }
            topArticlesDatas
        } else {
            getArticles(page)
        }
    }
}