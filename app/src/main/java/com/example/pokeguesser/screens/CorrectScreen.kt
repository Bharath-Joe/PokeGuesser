package com.example.pokeguesser.screens

import android.app.Activity
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pokeguesser.model.GameViewModel
import com.example.pokeguesser.R


@Composable
fun CorrectScreen(
    gameViewModel: GameViewModel = viewModel(),
    onPlayAgain: () -> Unit
) {
    val uiState by gameViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (uiState.isUserCorrect) {
                "That's correct, it's a ${uiState.currentPokemon}!"
            } else {
                "Sorry, that is incorrect. It's a ${uiState.currentPokemon}"
            },
            fontFamily = FontFamily(Font(R.font.varela)),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (!gameViewModel.userGuess.isEmpty()) {
            Text(
                text = "You guessed: ${gameViewModel.userGuess}",
                fontFamily = FontFamily(Font(R.font.varela)),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        AsyncImage(
            model = uiState.currentPokemonFront,
            contentDescription = null,
            modifier = Modifier
                .size(250.dp)
                .padding(bottom = 16.dp)
                .border(width = 1.5.dp, color = Color.Black)
        )

        Text(
            text = "You Earned ${uiState.score} Points!",
            fontFamily = FontFamily(Font(R.font.varela)),
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                onPlayAgain()
            },
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(text = "Play Again", fontFamily = FontFamily(Font(R.font.varela)), fontWeight = FontWeight.Bold)
        }
    }
}