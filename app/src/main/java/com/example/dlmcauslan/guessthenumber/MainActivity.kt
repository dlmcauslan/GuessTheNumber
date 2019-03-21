package com.example.dlmcauslan.guessthenumber

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.getViewState().observe(this, Observer<MainViewState>{ state ->
            updateUi(state)
        })

        bt_guess.setOnClickListener(buttonClickListener)
    }

    private fun updateUi(state: MainViewState) {
        tv_remaining_guesses.text = state.remainingGuessesText
        bt_guess.text = state.buttonText
    }

    private val buttonClickListener = { _: View ->
        val guess = et_guess_value.text.toString()
        viewModel.guessButtonClicked(guess)
    }
}
