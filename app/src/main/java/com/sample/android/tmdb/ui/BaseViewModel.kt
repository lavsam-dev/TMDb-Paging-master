package com.sample.android.tmdb.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.paging.PagedList
import com.sample.android.tmdb.domain.TmdbItem
import com.sample.android.tmdb.paging.Listing
import com.sample.android.tmdb.paging.NetworkState
import com.sample.android.tmdb.util.DisposableManager
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class BaseViewModel<T : TmdbItem>(app: Application)
    : AndroidViewModel(app) {

    // thread pool used for network requests
    protected val networkIO: ExecutorService = Executors.newFixedThreadPool(5)

    protected abstract val repoResult: LiveData<Listing<T>>

    val items: LiveData<PagedList<T>> by lazy { switchMap(repoResult) { it.pagedList } }
    val networkState: LiveData<NetworkState> by lazy { switchMap(repoResult) { it.networkState } }
    val refreshState: LiveData<NetworkState> by lazy { switchMap(repoResult) { it.refreshState } }

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun retry() {
        repoResult.value?.retry?.invoke()
    }

    override fun onCleared() {
        super.onCleared()
        DisposableManager.clear()
    }
}