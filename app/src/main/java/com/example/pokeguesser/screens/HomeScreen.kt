package com.example.pokeguesser

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.pokeguesser.model.GameUIState
import com.example.pokeguesser.model.GameViewModel
import com.example.pokeguesser.ui.theme.Red
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(navController: NavHostController, gameViewModel: GameViewModel = viewModel()){
    val uiState by gameViewModel.uiState.collectAsState()
    Column(
        modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("PokéGuesser",
            fontFamily = FontFamily(Font(R.font.varela)),
            fontSize = 27.sp,
        )
        Image(
            painter = painterResource(id = R.drawable.pokemonball),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(200.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            PokemonSound(context = LocalContext.current, gameViewModel)
            Text("Pokémon Cry", fontFamily = FontFamily(Font(R.font.dtm)))
        }
        Hints(gameViewModel, uiState)
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserGuess(gameViewModel)
            IconButton(onClick = {
                if(gameViewModel.userGuess != ""){
                    gameViewModel.checkUserGuess()
                    navController.navigate("correct")
                }
            }) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Submit Icon")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Stopwatch(gameViewModel)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            gameViewModel.checkUserGuess()
            navController.navigate("correct")
        }) {
            Text(text = "Give Up", fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.varela)), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun Stopwatch(gameViewModel: GameViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        while (true) {
            val currentTime = System.currentTimeMillis()
            gameViewModel.elapsedMilliseconds = currentTime - startTime
            delay(1)
        }
    }
    val elapsedSeconds = gameViewModel.elapsedMilliseconds / 1000
    val milliseconds = gameViewModel.elapsedMilliseconds % 1000
    Text(
        text = "${elapsedSeconds}:${String.format("%3d", milliseconds)} seconds",
        fontFamily = FontFamily(Font(R.font.roboto)),
        fontSize = 32.sp
    )
}

@Composable
fun UserGuess(gameViewModel: GameViewModel = viewModel()){
    OutlinedTextField(
        value = gameViewModel.userGuess,
        onValueChange = {gameViewModel.updateUserGuess(it)},
        placeholder = {Text(text = "Type Here...", fontFamily = FontFamily(Font(R.font.varela)))}
    )
}

@Composable
fun Hints(gameViewModel: GameViewModel = viewModel(), uiState: GameUIState) {
    val hints = listOf("Hint 1", "Hint 2", "Hint 3", "Hint 4")
    Column(Modifier.padding(16.dp)) {
        Row(Modifier.padding(bottom = 16.dp)) {
            HintCard(gameViewModel, hints[0],"Pokemon Types: ${uiState.currentPokemonTypes}",
                Modifier
                    .fillMaxWidth()
                    .weight(1f))
            Spacer(modifier = Modifier.width(30.dp))
            HintCard(gameViewModel, hints[1],"Height: ${uiState.currentPokemonHeight}\nWeight: ${uiState.currentPokemonWeight} lbs",
                Modifier
                    .fillMaxWidth()
                    .weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(Modifier.padding(bottom = 16.dp)){
            HintCard(gameViewModel, hints[2], "Generation: ${uiState.currentPokemonGeneration}",
                Modifier
                    .fillMaxWidth()
                    .weight(1f))
            Spacer(modifier = Modifier.width(30.dp))
            HintCard(gameViewModel, hints[3],   "First Letter of Pokemon's Name: ${(uiState.currentPokemonFirstLetter)}" +
                                                    "\nLegendary Pokemon: ${uiState.currentPokemonLegendary}" +
                                                    "\nBaby Pokemon: ${uiState.currentPokemonBaby}" +
                                                    "\nMythical Pokemon: ${uiState.currentPokemonMythical}",
                Modifier
                    .fillMaxWidth()
                    .weight(1f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HintCard(gameViewModel: GameViewModel = viewModel(), hint: String, info: String, modifier: Modifier) {
    var isClicked by remember { mutableStateOf(false) }
    if (isClicked) {
        AlertDialog(
            onDismissRequest = { isClicked = false },
            title = { Text(hint, fontFamily = FontFamily(Font(R.font.dtm))) },
            text = { Text(text = info, fontFamily = FontFamily(Font(R.font.varela)), fontSize = 15.sp)},
            confirmButton = {
                Button(onClick = {
                    isClicked = false
                    gameViewModel.updateHintCount()
                }) {
                    Text("OK", fontFamily = FontFamily(Font(R.font.dtm)))
                }
            }
        )
    }
    Card(onClick = { isClicked = true }, modifier = modifier, colors = CardDefaults.cardColors(containerColor = Red)) {
        Text(hint,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            fontFamily = FontFamily(Font(R.font.dtm)),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PokemonSound(context: Context, gameViewModel: GameViewModel = viewModel()) {
    val exoPlayer = remember {SimpleExoPlayer.Builder(context).build()}
    val isPlaying = remember { mutableStateOf(false) } // Initialize as false

    val isPlayingState by rememberUpdatedState(newValue = isPlaying.value)
    val playIcon = painterResource(id = R.drawable.ic_play)
    val pauseIcon = painterResource(id = R.drawable.ic_pause)
    val currentIcon = if (isPlayingState) pauseIcon else playIcon
    IconButton(onClick = {
        if (isPlayingState) {
            exoPlayer.pause()
            isPlaying.value = false
        } else {
            val pokemonName = gameViewModel.curPokemon.replace("-", "")
            val mediaItem = MediaItem.fromUri("https://play.pokemonshowdown.com/audio/cries/${pokemonName}.mp3")
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
            isPlaying.value = true
        }
    }) {
        Icon(painter = currentIcon, contentDescription = if (isPlayingState) "Pause" else "Play", modifier = Modifier.size(25.dp))
    }

    DisposableEffect(exoPlayer) {
        val completionListener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    isPlaying.value = false
                }
            }
        }
        exoPlayer.addListener(completionListener)
        onDispose {
            exoPlayer.stop()
            exoPlayer.release()
        }
    }
}