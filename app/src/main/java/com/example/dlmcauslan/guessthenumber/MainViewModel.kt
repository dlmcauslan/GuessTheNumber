package com.example.dlmcauslan.guessthenumber

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class MainViewModel: ViewModel() {

    /**
     * Sealed class representing the different events that can be fired off by the ui
     */
    sealed class Events {
        object InvalidGuess: Events()
        data class HigherOrLower(val isHigher: Boolean): Events()
    }

    /**
     * Start a new game
     */
    fun startNewGame() {
        viewState.value = MainViewState(
                guess = 0,
                answer = Random.nextInt(1,11),
                guessesRemaining = 3,
                isGameOver = false,
                isGuessCorrect = false)
    }

    /**
     * Live data to hold the view state
     */
    fun getViewState(): LiveData<MainViewState> = viewState
    private val viewState = MutableLiveData<MainViewState>()

    /**
     * Live data used to fire off one-time events
     */
    fun getEvents(): LiveData<Events> = events
    private val events = MutableLiveData<Events>()

    /**
     * Update the view state when the 'Guess' button is clicked
     */
    fun guessButtonClicked(guess: String) {
        if (viewState.value?.isGameOver == true) {
            startNewGame()
            return
        }

        val guessAsInt = guess.toIntOrNull()

        if (guessAsInt == null) {
            //TODO() fire off invalid guess event
        } else if (guessAsInt < 1 || guessAsInt > 10) {
            // TODO() fire off invalid guess event
        } else {
            updateViewState {
                val isGuessCorrect = guessAsInt == it.answer
                val guessesRemaining = it.guessesRemaining - 1
                viewState.value = it.copy(
                        guess = guessAsInt,
                        guessesRemaining = guessesRemaining,
                        isGameOver = guessesRemaining == 0 || isGuessCorrect,
                        isGuessCorrect = isGuessCorrect
                )
            }
            // TODO() fire off event to indicate whether it is higher or lower
        }
    }

    /**
     * Helper function to update view state
     */
    private fun updateViewState(callback: (viewState:MainViewState) -> Unit) {
        viewState.value?.let {
            callback(it)
        }
    }

}