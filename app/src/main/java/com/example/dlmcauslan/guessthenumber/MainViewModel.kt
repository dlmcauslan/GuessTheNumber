package com.example.dlmcauslan.guessthenumber

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class MainViewModel: ViewModel() {

    private var gameState = GameState(
            currentGuess = 0,
            answer = Random.nextInt(1,11),
            guessesRemaining = 3,
            isGameOver = false,
            isGuessCorrect = false)
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
                    gameState.isGameOver -> "Sorry, you have run out of guesses. Try again?"
                    else -> "You have ${gameState.guessesRemaining} guesses remaining"
                }

        viewState.value = MainViewState(
                remainingGuessesText = remainingGuessesText,
                buttonText = buttonText
        )

    }

    /**
     * Sealed class representing the different view effects that can be fired off by the ui
     */
    // TODO() check these arent recreated on rotation
    sealed class ViewEffects {
        object InvalidGuess: ViewEffects()
        data class HigherOrLower(val isHigher: Boolean): ViewEffects()
    }

    /**
     * Start a new game
     */
    private fun startNewGame() {
        gameState = GameState(
                currentGuess = 0,
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
     * Live data used to fire off one-time view effects
     */
    fun getViewEffects(): LiveData<ViewEffects> = viewEffects
    private val viewEffects = MutableLiveData<ViewEffects>()

    /**
     * Update the view state when the 'Guess' button is clicked
     */
    fun guessButtonClicked(guess: String) {
        if (gameState.isGameOver) {
            startNewGame()
            // TODO() potentially fire off event to clear text field
            return
        }

        val guessAsInt = guess.toInt()

        if (guessAsInt < 1 || guessAsInt > 10) {
            // TODO() fire off invalid guess event
        } else {
            val isGuessCorrect = guessAsInt == gameState.answer
            val guessesRemaining = gameState.guessesRemaining - 1
            gameState = gameState.copy(
                    currentGuess = guessAsInt,
                    guessesRemaining = guessesRemaining,
                    isGameOver = guessesRemaining == 0 || isGuessCorrect,
                    isGuessCorrect = isGuessCorrect
            )
            // TODO() fire off event to indicate whether it is higher or lower
        }
    }

    init {
        setViewState(gameState)
    }
}