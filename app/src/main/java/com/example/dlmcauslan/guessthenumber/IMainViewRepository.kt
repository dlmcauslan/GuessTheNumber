package com.example.dlmcauslan.guessthenumber

import androidx.lifecycle.LiveData

interface IMainViewRepository {

    fun getNumberOfWins(): LiveData<Int>

    fun getNumberOfLosses(): LiveData<Int>

    fun saveWin()

    fun saveLoss()
}