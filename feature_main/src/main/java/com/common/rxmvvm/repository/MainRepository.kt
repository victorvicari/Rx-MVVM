package com.common.rxmvvm.repository

import android.app.Application
import com.common.core.api.APIService
import com.common.core.base.BaseResponse
import com.common.core.models.FeedData
import com.common.core.models.TodayResp
import com.common.core.vo.NetworkBoundResource
import io.reactivex.Flowable

class MainRepository(private val apiService: APIService, private val app: Application) {


    fun getTodayList(): Flowable<BaseResponse<TodayResp>> {
        return object : NetworkBoundResource<TodayResp, TodayResp>(app) {
            override fun shouldFetch(data: TodayResp?): Boolean = true

            override fun shouldLoadFromCache(): Boolean = false

            override fun loadFromDB(): TodayResp? = null

            override fun cache(data: TodayResp) {

            }

            override fun callApi(): Flowable<BaseResponse<TodayResp>> = apiService.getTodayList()

        }.asFlowable()
    }

    fun getCategoryList(category: String, page: Int): Flowable<BaseResponse<List<FeedData>>> {
        return object : NetworkBoundResource<List<FeedData>, List<FeedData>>(app) {
            override fun shouldFetch(data: List<FeedData>?): Boolean = true

            override fun shouldLoadFromCache(): Boolean = false

            override fun loadFromDB(): List<FeedData>? = null

            override fun cache(data: List<FeedData>) {

            }

            override fun callApi(): Flowable<BaseResponse<List<FeedData>>> = apiService.getCategoryList(category, page)

        }.asFlowable()
    }
}