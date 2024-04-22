package com.example.unscramblemod.ui

import android.app.Activity
import android.os.CountDownTimer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unscramblemod.R

enum class Screens{
    MAIN,GAME
}
@Composable
fun MainGameScreen(gameViewModel : GameViewModel = viewModel()) {

    val currentUiState by gameViewModel.uiState.collectAsState()
    var currentGuess by remember {
        mutableStateOf("")
    }
    val currentTimerValue = gameViewModel.timerValue.collectAsState()
    var currentScreen by rememberSaveable {
        mutableStateOf(Screens.MAIN)
    }
    when(currentScreen){
        Screens.MAIN -> MainScreen(onStartGame = {
            gameViewModel.startGame()
            currentScreen = Screens.GAME
        })
        Screens.GAME ->
        Column(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.padding(5.dp).weight(2f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painterResource(id = R.drawable.component_1),
                    contentDescription = null
                )
                Text(text = "Guess The Word!")
            }

            GameLayout(input = currentGuess,
                onInputChange = {currentGuess = it},
                currentDescription= currentUiState.description,
                currentScrambledWord= currentUiState.word,
                timerValue = currentTimerValue.value,
                isWrong = currentUiState.isWrong,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .weight(5f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)).weight(1f)
            ) {
                OutlinedButtonLayout(onClick= { gameViewModel.onSkip() }, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(70.dp))
                FilledButtonLayout(onClick = {gameViewModel.checkUserGuess(currentGuess)}, modifier = Modifier.weight(1f))

            }
            ScoreDisplay(score = currentUiState.score)
            if(currentUiState.isGameFinished){
                FinalScoreDialog(score = currentUiState.score, onPlayAgain = { gameViewModel.onPlayAgain() })
            }
        }

    }

}

@Composable
fun MainScreen(onStartGame: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Image(painter = painterResource(id = R.drawable.component_1), contentDescription = null)
        Spacer(modifier = Modifier.size(60.dp))
        Text(text = "Guess The Word", style = MaterialTheme.typography.titleLarge)
        Button(onClick = { onStartGame() }) {
            Text(text = "Start Game")
        }
    }
}

@Composable
fun ScoreDisplay(score: Int) {
    Text(
        text = "Score: $score",
        style = MaterialTheme.typography.titleLarge
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameLayout(
    input:String,
    onInputChange: (String) -> Unit,
    currentDescription:String,
    currentScrambledWord:String,
    isWrong:Boolean,
    timerValue: Int,
    modifier: Modifier) {
    var expanded by remember {
        mutableStateOf(false)
    }
    Card(modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    )  {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceTint)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .align(alignment = Alignment.End),
                text = "${timerValue}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = currentScrambledWord,
                modifier = Modifier,
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(
                onClick = { expanded = !expanded },
            ) {
                Icon(Icons.Filled.Info, "Icon Button")
            }
            if(expanded){
                Text(text = currentDescription,
                    style = MaterialTheme.typography.labelSmall)
            }
            OutlinedTextField(
                value = input,
                onValueChange = onInputChange ,
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth(),
                label = {if(isWrong){"Wrong Guess"}else{"Enter Word Here"} },
                isError = isWrong,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { ImeAction.Done }
                )

            )
        }

    }


}

@Composable
fun FilledButtonLayout(onClick: () -> Unit , modifier: Modifier) {
    TextButton(
        onClick = onClick, modifier = modifier
            .clip(MaterialTheme.shapes.medium),
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = "Submit",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary
        )

    }
}


@Composable
fun OutlinedButtonLayout(onClick: () -> Unit, modifier: Modifier) {
    OutlinedButton(
        onClick = onClick, modifier = modifier
            .clip(MaterialTheme.shapes.medium),
        colors = ButtonDefaults.outlinedButtonColors(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(
            text = "Skip",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )

    }
}

@Composable
private fun FinalScoreDialog(
    score: Int,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = (LocalContext.current as Activity)

    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
        },
        title = { Text(text =("Congratulations")) },
        text = { Text(text = stringResource(R.string.you_scored, score)) },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = {
                    activity.finish()
                }
            ) {
                Text(text = stringResource(R.string.exit))
            }
        },
        confirmButton = {
            TextButton(onClick = onPlayAgain) {
                Text(text = stringResource(R.string.play_again))
            }
        }
    )
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    UnscrambleModTheme {
//        GameLayout(
//            input = "XYZ",
//            onInputChange = {},
//            currentDescription = A thing that fli,
//            currentScrambledWord = ,
//            isWrong = ,
//            timerValue = ,
//            modifier =
//        )
//    }
//}