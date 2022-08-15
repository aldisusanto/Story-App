package com.android.submission.core.source.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.submission.core.source.remote.network.ApiService
import com.android.submission.core.source.remote.response.item.StoryItem

class StoryPagingSource(private val apiService: ApiService, private val header: String) :
    PagingSource<Int, StoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, StoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val response = apiService.getAllStories(
                token = header,
                page = page,
                size = params.loadSize
            )

            LoadResult.Page(
                data = response.listStory,
                prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if (response.listStory.isNullOrEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }


    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}