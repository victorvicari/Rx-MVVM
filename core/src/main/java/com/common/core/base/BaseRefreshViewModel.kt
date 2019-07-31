package com.common.core.base


import android.util.Log
import com.common.core.extensions.disposedBag
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject

abstract class BaseRefreshViewModel: BaseViewModel() {

    open val dataListPublishSubject = PublishSubject.create<MutableList<Any>>()
    private val refreshStatePublishSubject = PublishSubject.create<RefreshState>()
    private val refreshPublishSubject = PublishSubject.create<Int>()
    protected var currentPage = 1

    enum class RefreshState {
        REFRESH_SUCCESS,
        LOAD_MORE_SUCCESS,
        NO_MORE_DATA
    }
    data class RefreshInputs(
        val pullToRefreshObservable: PublishSubject<Boolean>,
        val loadMoreObservable: PublishSubject<Boolean>
    )

    data class RefreshOutputs(
        val refreshState: PublishSubject<RefreshState>
    )

    open fun transformsRefresh(inputs: RefreshInputs): RefreshOutputs {
        inputs.pullToRefreshObservable.subscribe {
            Log.d("test", "pullToRefresh onNext")
            currentPage = 1
            refreshPublishSubject.onNext(currentPage)
        }.disposedBag(dispose)
        inputs.loadMoreObservable.subscribe {
            Log.d("test", "loadMore onNext")
            currentPage += 1
            refreshPublishSubject.onNext(currentPage)
        }.disposedBag(dispose)

        val dataList = arrayListOf<Any>()
        refreshPublishSubject
            .flatMap { getDataApi().toObservable() }
            .subscribe({
                Log.d("test", "onNext= currentPage= $currentPage")
                if (currentPage == 1) {
                    dataList.clear()
                    dataList.addAll(it.data)
                    dataListPublishSubject.onNext(dataList)
                    refreshStatePublishSubject.onNext(RefreshState.REFRESH_SUCCESS)
                }else {
                    dataList.addAll(it.data)
                    if (it.data.size <= 10) {
                        dataListPublishSubject.onNext(dataList)
                        refreshStatePublishSubject.onNext(RefreshState.NO_MORE_DATA)
                    }else {
                        refreshStatePublishSubject.onNext(RefreshState.LOAD_MORE_SUCCESS)
                    }
                }
            }, {
                if (currentPage == 1) {
                    refreshStatePublishSubject.onNext(RefreshState.REFRESH_SUCCESS)
                }else {
                    refreshStatePublishSubject.onNext(RefreshState.LOAD_MORE_SUCCESS)
                }
                Log.e("test", "onError")
                it.printStackTrace()
            }).disposedBag(dispose)

        return RefreshOutputs(refreshStatePublishSubject)
    }

    abstract fun  getDataApi(): Flowable<BaseResponse<List<Any>>>
}