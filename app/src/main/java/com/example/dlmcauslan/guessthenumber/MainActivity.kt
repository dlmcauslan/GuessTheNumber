package com.example.dlmcauslan.guessthenumber

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.getViewState().observe(this, Observer<MainViewState> { state ->
            updateUi(state)
        })

        viewModel.getViewEffects().observe(this, Observer<MainViewModel.ViewEffects> { viewEffect ->
            if (viewEffect == null) return@Observer

            processViewEffects(viewEffect)

            // Clear view effects after they are processed, so they don't get fired off again in case
            // of a screen rotation etc
            viewModel.clearViewEffects()
        })

        bt_guess.setOnClickListener{
            viewModel.guessButtonClicked()
        }

        iv_decrease_guess.setOnClickListener {
            viewModel.decreaseButtonClicked()
        }

        iv_increase_guess.setOnClickListener {
            viewModel.increaseButtonClicked()
        }

    }

    private fun updateUi(state: MainViewState) {
        // Update UI
        tv_remaining_guesses.text = state.remainingGuessesText
        bt_guess.text = state.buttonText
        tv_guess.text = state.currentGuess
    }

    private fun processViewEffects(viewEffect: MainViewModel.ViewEffects) {
        when (viewEffect) {
            is MainViewModel.ViewEffects.InvalidGuess -> {
                Snackbar.make(main_layout, "Guess must be between 1 and 10",  Snackbar.LENGTH_SHORT).show()
            }
            is MainViewModel.ViewEffects.HigherOrLower -> {
                val snackbarText =
                        if (viewEffect.isHigher) "Try a higher guess"
                        else "Try a lower guess"
                Snackbar.make(main_layout, snackbarText, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
