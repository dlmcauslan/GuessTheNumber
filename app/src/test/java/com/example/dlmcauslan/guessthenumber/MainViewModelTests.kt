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
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTests {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel

    private val mockRepository = MockRepository()

    @Mock
    private lateinit var mockObserver: Observer<MainViewState>

    @Mock
    private lateinit var mockViewEffectObserver: Observer<MainViewModel.ViewEffects>

    @Before
    fun setup() {
        viewModel = MainViewModel(mockRepository)
        viewModel.getViewState().observeForever(mockObserver)
        viewModel.getViewEffects().observeForever(mockViewEffectObserver)
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

        verify(mockObserver).onChanged(expectedViewState)
    }

    @Test fun `increaseButtonClicked increases current guess by one`() {
        val expectedViewState = MainViewState(
                currentGuess = "6",
                remainingGuessesText = "You have 3 guesses remaining",
                buttonText = "Guess",
                numberOfWins = 0,
                numberOfLosses = 0
        )

        viewModel.increaseButtonClicked()

        verify(mockObserver).onChanged(expectedViewState)
    }

    @Test fun `decreaseButtonClicked decreases current guess by one`() {
        val expectedViewState = MainViewState(
                currentGuess = "4",
                remainingGuessesText = "You have 3 guesses remaining",
                buttonText = "Guess",
                numberOfWins = 0,
                numberOfLosses = 0
        )

        viewModel.decreaseButtonClicked()

        verify(mockObserver).onChanged(expectedViewState)
    }

    @Test fun `guess updates correctly when increaseButtonClicked and decreaseButtonClicked are called multiple times`() {
        val expectedViewState = MainViewState(
                currentGuess = "2",
                remainingGuessesText = "You have 3 guesses remaining",
                buttonText = "Guess",
                numberOfWins = 0,
                numberOfLosses = 0
        )

        viewModel.decreaseButtonClicked()
        viewModel.decreaseButtonClicked()
        viewModel.increaseButtonClicked()
        viewModel.decreaseButtonClicked()
        viewModel.increaseButtonClicked()
        viewModel.increaseButtonClicked()
        viewModel.decreaseButtonClicked()
        viewModel.decreaseButtonClicked()
        viewModel.decreaseButtonClicked()

        verify(mockObserver).onChanged(expectedViewState)
    }

    @Test fun `increaseButtonClicked doesn't increase guess if at maximum`() {
        viewModel.setGameStateForTests(
                GameState(currentGuess = 10,
                        answer = 4,
                        guessesRemaining = 3,
                        isGameOver = false,
                        isGuessCorrect = false
                )
        )

        val expectedViewState = MainViewState(
                currentGuess = "10",
                remainingGuessesText = "You have 3 guesses remaining",
                buttonText = "Guess",
                numberOfWins = 0,
                numberOfLosses = 0
        )

        viewModel.increaseButtonClicked()

        verify(mockObserver).onChanged(expectedViewState)
    }

    @Test fun `decreaseButtonClicked doesn't decrease guess if at minimum`() {
        viewModel.setGameStateForTests(
                GameState(currentGuess = 1,
                        answer = 4,
                        guessesRemaining = 3,
                        isGameOver = false,
                        isGuessCorrect = false
                )
        )

        val expectedViewState = MainViewState(
                currentGuess = "1",
                remainingGuessesText = "You have 3 guesses remaining",
                buttonText = "Guess",
                numberOfWins = 0,
                numberOfLosses = 0
        )

        viewModel.decreaseButtonClicked()

        verify(mockObserver).onChanged(expectedViewState)
    }

    @Test fun `guessButtonClicked decreases the remainingGuessesText by one`() {
        viewModel.setGameStateForTests(
                GameState(currentGuess = 5,
                        answer = 4,
                        guessesRemaining = 3,
                        isGameOver = false,
                        isGuessCorrect = false
                )
        )

        val expectedViewState = MainViewState(
                currentGuess = "5",
                remainingGuessesText = "You have 2 guesses remaining",
                buttonText = "Guess",
                numberOfWins = 0,
                numberOfLosses = 0
        )

        viewModel.guessButtonClicked()

        verify(mockObserver).onChanged(expectedViewState)
    }

    @Test fun `guessButtonClicked updates view state to won when the guess is correct`() {
        viewModel.setGameStateForTests(
                GameState(currentGuess = 5,
                        answer = 5,
                        guessesRemaining = 3,
                        isGameOver = false,
                        isGuessCorrect = false
                )
        )

        val expectedViewState = MainViewState(
                currentGuess = "5",
                remainingGuessesText = "You guessed correctly, Congratulations!",
                buttonText = "Play again?",
                numberOfWins = 0,
                numberOfLosses = 0
        )

        viewModel.guessButtonClicked()

        verify(mockObserver).onChanged(expectedViewState)
    }

    @Test fun `guessButtonClicked updates view state to lost when the guess is incorrect and you are on your last guess`() {
        viewModel.setGameStateForTests(
                GameState(currentGuess = 5,
                        answer = 8,
                        guessesRemaining = 1,
                        isGameOver = false,
                        isGuessCorrect = false
                )
        )

        val expectedViewState = MainViewState(
                currentGuess = "5",
                remainingGuessesText = "Sorry, you have run out of guesses.\nTry again?",
                buttonText = "Play again?",
                numberOfWins = 0,
                numberOfLosses = 0
        )

        viewModel.guessButtonClicked()

        verify(mockObserver).onChanged(expectedViewState)
    }

    @Test fun `guessButtonClicked restarts the game if the game is over`() {
        viewModel.setGameStateForTests(
                GameState(currentGuess = 5,
                        answer = 8,
                        guessesRemaining = 0,
                        isGameOver = true,
                        isGuessCorrect = false
                )
        )

        val expectedViewState = MainViewState(
                currentGuess = "5",
                remainingGuessesText = "You have 3 guesses remaining",
                buttonText = "Guess",
                numberOfWins = 0,
                numberOfLosses = 0
        )

        viewModel.guessButtonClicked()

        // This default game state will get called once on startup, then again when the guess button
        // is clicked.
        verify(mockObserver, times(2)).onChanged(expectedViewState)
    }

    @Test fun `increaseButtonClicked fires off an invalid guess effect if at maximum`() {
        viewModel.setGameStateForTests(
                GameState(currentGuess = 10,
                        answer = 4,
                        guessesRemaining = 3,
                        isGameOver = false,
                        isGuessCorrect = false
                )
        )

        viewModel.increaseButtonClicked()

        verify(mockViewEffectObserver).onChanged(MainViewModel.ViewEffects.InvalidGuess)
    }

    @Test fun `decreaseButtonClicked fires off an invalid guess if at minimum`() {
        viewModel.setGameStateForTests(
                GameState(currentGuess = 1,
                        answer = 4,
                        guessesRemaining = 3,
                        isGameOver = false,
                        isGuessCorrect = false
                )
        )

        viewModel.decreaseButtonClicked()

        verify(mockViewEffectObserver).onChanged(MainViewModel.ViewEffects.InvalidGuess)
    }

    @Test fun `guessButtonClicked fires off a higherOrLower effect set to true if the answer is higher`() {
        viewModel.setGameStateForTests(
                GameState(currentGuess = 5,
                        answer = 8,
                        guessesRemaining = 3,
                        isGameOver = false,
                        isGuessCorrect = false
                )
        )

        viewModel.guessButtonClicked()

        verify(mockViewEffectObserver).onChanged(MainViewModel.ViewEffects.HigherOrLower(true))
    }

    @Test fun `guessButtonClicked fires off a higherOrLower effect set to true if the answer is lower`() {
        viewModel.setGameStateForTests(
                GameState(currentGuess = 4,
                        answer = 2,
                        guessesRemaining = 3,
                        isGameOver = false,
                        isGuessCorrect = false
                )
        )

        viewModel.guessButtonClicked()

        verify(mockViewEffectObserver).onChanged(MainViewModel.ViewEffects.HigherOrLower(false))
    }

    @Test fun `the view state updates correctly when the number of wins is changed in the repository`() {
        val expectedViewState = MainViewState(
                currentGuess = "5",
                remainingGuessesText = "You have 3 guesses remaining",
                buttonText = "Guess",
                numberOfWins = 5,
                numberOfLosses = 0
        )

        mockRepository.numberOfWins.value = 5

        verify(mockObserver).onChanged(expectedViewState)
    }

    @Test fun `the view state updates correctly when the number of losses is changed in the repository`() {
        val expectedViewState = MainViewState(
                currentGuess = "5",
                remainingGuessesText = "You have 3 guesses remaining",
                buttonText = "Guess",
                numberOfWins = 0,
                numberOfLosses = 7
        )

        mockRepository.numberOfLosses.value = 7

        verify(mockObserver).onChanged(expectedViewState)
    }

    private class MockRepository: IMainViewRepository {
        val numberOfWins = MutableLiveData<Int>()
        override fun getNumberOfWins(): LiveData<Int> = numberOfWins

        val numberOfLosses = MutableLiveData<Int>()
        override fun getNumberOfLosses(): LiveData<Int> = numberOfLosses

        override fun saveWin() {}

        override fun saveLoss() {}
    }
}