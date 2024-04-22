package com.example.unscramblemod.ui

import android.app.Activity
import android.os.CountDownTimer
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unscramblemod.MainActivity
import com.example.unscramblemod.model.COUNT_DOWN
import com.example.unscramblemod.model.MAX_TIME
import com.example.unscramblemod.model.SCORE_INCREASE
import com.example.unscramblemod.model.WordData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Timer
import java.util.TimerTask

class GameViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    private val _timerValue = MutableStateFlow(0)
    val timerValue = _timerValue.asStateFlow()
    val uiState: StateFlow<GameUiState>
        get() = _uiState
    private val wordDescriptionPair = WordData.wordData
    private val usedWords = mutableSetOf<Int>()
    private var currentWord by
    mutableStateOf("")
    init {
    reset()
    }


    fun pickRandomWordAndShuffle(): String {
        val random = (1..11).random()
        return if (random in usedWords) {
            pickRandomWordAndShuffle()
        } else {
            val randomWordPair = wordDescriptionPair[random]
            currentWord = randomWordPair!![0].toString()
//            _uiState.value = GameUiState(description = randomWordPair?.get(1).toString() )
            _uiState.value.description = randomWordPair[1].toString()
            usedWords.add(random)
            scrambleWord(currentWord)
        }
    }

    fun scrambleWord(word: String): String {
        val tempWord = word.toCharArray()
        while (String(tempWord).equals(word)){
            tempWord.shuffle()
        }
        return String(tempWord)

    }

    fun onPlayAgain(){
        reset()
        startGame()
        pickRandomWordAndShuffle()
    }

    fun startGame(){
        object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timerValue.value = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                onFinish(_uiState.value.score)
            }
        }.start()
    }

    fun checkUserGuess(guess: String) {
        if (currentWord.equals(guess, ignoreCase = true)) {
            //UpdateScore
            val currentScore = _uiState.value.score.plus(SCORE_INCREASE)
            //new Word
            updateUiState(currentScore)
        } else {
            //display to user that entry is wrong
            val currentScore = _uiState.value.score.minus(SCORE_INCREASE)
            wrongGuessUpdateUiState(currentScore)
        }
    }

    fun reset() {
        usedWords.clear()
        _uiState.value = GameUiState(word = pickRandomWordAndShuffle())
    }

    fun onSkip() {
        _uiState.update {currentState ->
            currentState.copy(
                word = pickRandomWordAndShuffle(),
                isWrong = false
            )
        }
    }
    fun updateUiState(updatedScore: Int) {
        _uiState.update {currentState ->
            currentState.copy(
                word = pickRandomWordAndShuffle(),
                score = updatedScore,
                isWrong = false
            )
        }

    }
    private fun wrongGuessUpdateUiState(updatedScore: Int) {
        _uiState.update {currentState ->
            currentState.copy(
                word = pickRandomWordAndShuffle(),
                score = updatedScore,
                isWrong = true
            )
        }
    }
    fun onFinish(updatedScore: Int) {
        _uiState.update {currentState ->
            currentState.copy(
                score = updatedScore,
                isGameFinished = true
            )
        }

    }
}