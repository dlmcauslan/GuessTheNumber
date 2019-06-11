package com.example.dlmcauslan.guessthenumber

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel(private val repository: IMainViewRepository): ViewModel() {

    /**
     * Live data to hold the view state
     */
    fun getViewState(): LiveData<MainViewState> = viewState
    private val viewState = MediatorLiveData<MainViewState>()

    private var gameState = GameState()
        set(value) {
            field = value
            setViewState(field)
        }

    private fun setViewState(gameState: GameState) = updateViewState { state ->
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

        state.copy(
                currentGuess = "${gameState.currentGuess}",
                remainingGuessesText = remainingGuessesText,
                buttonText = buttonText
        )
    }

    private fun updateViewState(updateFunction: (state: MainViewState) -> MainViewState) {
        viewState.value = viewState.value?.let { state ->
            updateFunction(state)
        }
    }

    /**
     * Sealed class representing the different view effects that can be fired off by the ui
     */
    sealed class ViewEffects {
        object InvalidGuess: ViewEffects()
        data class HigherOrLower(val isHigher: Boolean): ViewEffects()
    }

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

        gameState = gameState.haveAGuess()

        if (!gameState.isGameOver) {
            // Fire off a view effect to let the user know to guess higher or lower
            viewEffects.value = ViewEffects.HigherOrLower(isHigher = gameState.currentGuess < gameState.answer)
        } else {
            // Game is over so update the win/loss count in the "database"
            if (gameState.isGuessCorrect) repository.saveWin()
            else repository.saveLoss()
        }
    }

    private fun startNewGame() {
        gameState = GameState()
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
        viewState.addSource(repository.getNumberOfWins()) { numberOfWins ->
            updateNumberOfWins(numberOfWins)
        }

        viewState.addSource(repository.getNumberOfLosses()) { numberOfLosses ->
            updateNumberOfLosses(numberOfLosses)
        }

        viewState.value = MainViewState()

        setViewState(gameState)
    }

    private fun updateNumberOfWins(numberOfWins: Int) = updateViewState { state ->
        state.copy(numberOfWins = numberOfWins)
    }

    private fun updateNumberOfLosses(numberOfLosses: Int) = updateViewState { state ->
        state.copy(numberOfLosses = numberOfLosses)
    }

    @VisibleForTesting
    fun setGameStateForTests(newGameState: GameState) {
        gameState = newGameState
    }
}