package com.example.dlmcauslan.guessthenumber

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import android.view.inputmethod.InputMethodManager

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
        // Update UI
        tv_remaining_guesses.text = state.remainingGuessesText
        bt_guess.text = state.buttonText

        // Select text and show keyboard
        et_guess_value.selectAll()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(et_guess_value, InputMethodManager.SHOW_IMPLICIT)
    }

    private val buttonClickListener = { _: View ->
        val guess = et_guess_value.text.toString()
        viewModel.guessButtonClicked(guess)
    }
}
