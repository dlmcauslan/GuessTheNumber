package com.example.dlmcauslan.guessthenumber

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

private const val NUMBER_OF_WINS = "number_of_wins"
private const val NUMBER_OF_LOSSES = "number_of_losses"

class MainViewRepository(private val preferences: SharedPreferences) {

    /**
     * Live data to hold the number of wins
     */
    fun getNumberOfWins(): LiveData<Int> = numberOfWins
    private val numberOfWins = MutableLiveData<Int>()

    /**
     * Live data to hold the number of loses
     */
    fun getNumberOfLosses(): LiveData<Int> = numberOfLosses
    private val numberOfLosses = MutableLiveData<Int>()
    
    fun saveWin() {
        val updatedWinCount = numberOfWins.value!! + 1
        preferences.edit().putInt(NUMBER_OF_WINS, updatedWinCount).apply()
        numberOfWins.value = updatedWinCount
    }

    fun saveLoss() {
        val updatedLossCount = numberOfLosses.value!! + 1
        preferences.edit().putInt(NUMBER_OF_LOSSES, updatedLossCount).apply()
        numberOfLosses.value = updatedLossCount
    }

    init {
        numberOfWins.value = preferences.getInt(NUMBER_OF_WINS, 0)
        numberOfLosses.value = preferences.getInt(NUMBER_OF_LOSSES, 0)
    }
}