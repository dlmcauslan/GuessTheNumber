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

        viewModel.startNewGame()
    }

    private fun updateUi(state: MainViewState) {
        when {
            state.isGuessCorrect -> {
                tv_remaining_guesses.text = getString(R.string.you_guessed_correctly)
                bt_guess.text = getString(R.string.play_again)
            }
            state.isGameOver -> {
                tv_remaining_guesses.text = getString(R.string.you_lose)
                bt_guess.text = getString(R.string.play_again)
            }
            else -> {
                tv_remaining_guesses.text = getString(R.string.you_have_X_guesses_remaining, state.guessesRemaining)
                bt_guess.text = getString(R.string.guess)
            }
        }
    }

    private val buttonClickListener = { _: View ->
        val guess = et_guess_value.text.toString()
        viewModel.guessButtonClicked(guess)
    }
}
