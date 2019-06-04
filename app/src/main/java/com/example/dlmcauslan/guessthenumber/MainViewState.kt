package com.example.dlmcauslan.guessthenumber

data class MainViewState(
        val currentGuess: String = "",
        val remainingGuessesText: String = "",
        val buttonText: String = "",
        val numberOfWins: Int = 0,
        val numberOfLosses: Int = 0
)