package com.example.dlmcauslan.guessthenumber

import androidx.lifecycle.Observer
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito.verify
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTests {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel

    private val mockRepository = MockRepository()
    
    @Mock
    private lateinit var mockObserver: Observer<MainViewState>

    @Before
    fun setup() {
        viewModel = MainViewModel(mockRepository)
        viewModel.getViewState().observeForever(mockObserver)

        // Observe the initial setting of state
//        verify(mockObserver).onChanged(MainViewState())
    }

    @Test
    fun `view model state shows new game ui on startup`() {
        val expectedViewState = MainViewState(
                currentGuess = "5",
                remainingGuessesText = "You have 3 guesses remaining",
                buttonText = "Guess",
                numberOfWins = 0,
                numberOfLosses = 0
        )
        assertEquals(expectedViewState, viewModel.getViewState().value)
    }

    private class MockRepository(): IMainViewRepository {
        val numberOfWins = MutableLiveData<Int>()
        override fun getNumberOfWins(): LiveData<Int> = numberOfWins

        val numberOfLosses = MutableLiveData<Int>()
        override fun getNumberOfLosses(): LiveData<Int> = numberOfLosses

        override fun saveWin() {
        }

        override fun saveLoss() {
        }

    }
}