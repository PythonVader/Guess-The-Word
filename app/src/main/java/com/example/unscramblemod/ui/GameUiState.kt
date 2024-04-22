package com.example.unscramblemod.ui

import com.example.unscramblemod.model.MAX_TIME

data class GameUiState(
    var word: String = "",
    var description: String = "",
    var isWrong : Boolean = false,
    var score: Int = 0,
    var isGameFinished: Boolean = false,
    var timeLeft:Long = MAX_TIME
)