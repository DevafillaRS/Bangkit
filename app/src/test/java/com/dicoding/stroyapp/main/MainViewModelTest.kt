package com.dicoding.stroyapp.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.stroyapp.DataDummy
import com.dicoding.stroyapp.MainDispatcherRule
import com.dicoding.stroyapp.adapter.PrimaryAdapter
import com.dicoding.stroyapp.getOrAwaitValue
import com.dicoding.stroyapp.paging.Repository
import com.dicoding.stroyapp.response.ListStoryItem
import com.dicoding.stroyapp.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var repository: Repository
    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setup(){
        MockitoAnnotations.initMocks(this)
        mainViewModel = MainViewModel(repository)
    }

    @Test
    fun`when Get Story Should Not Null and Return Data`() = runTest {
        val token = "Bearer token"
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val data: PagingData<ListStoryItem> = StoriesPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        Mockito.`when`(repository.getStory(token)).thenReturn(expectedStory)

        val actualStory: PagingData<ListStoryItem> = mainViewModel.story(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = PrimaryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])

        val actualData = differ.snapshot()
        println("Actual Data: $actualData")
        println(dummyStory[0].name + " " + differ.snapshot()[0]?.name)
    }

    @Test
    fun`when Get Story Empty Should Return No Data`() = runTest {
        val token = "Bearer token"
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        Mockito.`when`(repository.getStory(token)).thenReturn(expectedStory)

        val actualStory: PagingData<ListStoryItem> = mainViewModel.story(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = PrimaryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)
        assertEquals(0, differ.snapshot().size)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

class StoriesPagingSource: PagingSource<Int, LiveData<List<ListStoryItem>>>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}