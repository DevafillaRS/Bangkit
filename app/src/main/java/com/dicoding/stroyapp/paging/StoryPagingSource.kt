package com.dicoding.stroyapp.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.stroyapp.api.ApiService
import com.dicoding.stroyapp.response.ListStoryItem

class StoryPagingSource(private val apiService: ApiService, private val token: String) :
    PagingSource<Int, ListStoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStory(authorization = token, page, params.loadSize)
            Log.d("StoryPagingSource", "load: $responseData")

            val data = responseData.listStory

            LoadResult.Page(
                data = data,
                prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if (data.isNullOrEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}