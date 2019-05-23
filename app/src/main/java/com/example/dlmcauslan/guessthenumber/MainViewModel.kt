package com.example.dlmcauslan.guessthenumber

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    private var gameState = GameState()
        set(value) {
            field = value
            setViewState(field)
        }

    private fun setViewState(gameState: GameState) {
        // I'd normally use string resources here, but I've left them as raw strings here for clarity.
        val buttonText =
                if (gameState.isGameOver) "Play again?"
                else "Guess"
        val remainingGuessesText =
                when {
                    gameState.isGuessCorrect -> "You guessed correctly, Congratulations!"
                    gameState.isGameOver -> "Sorry, you have run out of guesses.\nTry again?"
                    else -> "You have ${gameState.guessesRemaining} guesses remaining"
                }

        viewState.value = MainViewState(
                currentGuess = "${gameState.currentGuess}",
                remainingGuessesText = remainingGuessesText,
                buttonText = buttonText
        )
    }

    /**
     * Sealed class representing the different view effects that can be fired off by the ui
     */
    sealed class ViewEffects {
        object InvalidGuess: ViewEffects()
        data class HigherOrLower(val isHigher: Boolean): ViewEffects()
    }

    /**
     * Start a new game
     */
    private fun startNewGame() {
        gameState = GameState()
    }

    /**
     * Live data to hold the view state
     */
    fun getViewState(): LiveData<MainViewState> = viewState
    private val viewState = MutableLiveData<MainViewState>()

    /**
     * Live data used to fire off one-time view effects
     */
    fun getViewEffects(): LiveData<ViewEffects> = viewEffects
    private val viewEffects = MutableLiveData<ViewEffects>()

    /**
     * Update the view state when the 'Guess' button is clicked
     */
    fun guessButtonClicked() {
        if (gameState.isGameOver) {
            startNewGame()
            return
        }

        val isGuessCorrect = gameState.currentGuess == gameState.answer
        val guessesRemaining = gameState.guessesRemaining - 1

        // Update the game state
        gameState = gameState.copy(
                guessesRemaining = guessesRemaining,
                isGameOver = guessesRemaining == 0 || isGuessCorrect,
                isGuessCorrect = isGuessCorrect
        )

        // Fire off a view effect to let the user know to guess higher or lower
        if (!gameState.isGameOver) {
            viewEffects.value = ViewEffects.HigherOrLower(isHigher = gameState.currentGuess < gameState.answer)
        }
    }

    fun decreaseButtonClicked() {
        if (gameState.currentGuess == 1) {
            viewEffects.value = ViewEffects.InvalidGuess
        } else {
            gameState = gameState.decrease()
        }
    }

    fun increaseButtonClicked() {
        if (gameState.currentGuess == 10) {
            viewEffects.value = ViewEffects.InvalidGuess
        } else {
            gameState = gameState.increase()
        }
    }

    fun clearViewEffects() {
        viewEffects.value = null
    }

    init {
        setViewState(gameState)
    }
}