package com.example.pokeguesser.screens


import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.pokeguesser.R
import com.example.pokeguesser.model.GameUIState
import com.example.pokeguesser.model.GameViewModel
import com.example.pokeguesser.ui.theme.Red
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun UserScreen(gameViewModel: GameViewModel = viewModel(), gso: GoogleSignInOptions, navController: NavHostController) {
    val uiState by gameViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    var name = ""
    name = if(currentUser == null){
        "Guest"
    }
    else{
        currentUser.displayName!!
    }
    val profilePic = currentUser?.photoUrl?.toString()
    val signOut = {
        FirebaseAuth.getInstance().signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            Toast.makeText(context, "Signed out", Toast.LENGTH_SHORT).show()
        }
        navController.navigate("instructions")
    }
    if (currentUser != null) {
        val userDocument = db.collection("users").document(currentUser.email ?: "")
        userDocument.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val highestScore = documentSnapshot.getLong("highestScore")
                    val highestScorePokemon = documentSnapshot.getString("highestScorePokemon")

                    if (highestScore != null && highestScorePokemon != null) {
                        uiState.highestScore = highestScore
                        uiState.highestScorePokemon = highestScorePokemon
                    } else {
                        Log.d(TAG, "Highest score or highest score Pokemon is null")
                    }
                } else {
                    Log.d(TAG, "User document does not exist")
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error retrieving user document", e)
            }
    } else {
        Log.d(TAG, "User is not authenticated")
    }
    Column(
        modifier = Modifier.padding(32.dp),
    ) {
        Text("Guesser Profile",
            fontFamily = FontFamily(Font(R.font.varela)),
            fontSize = 27.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (profilePic != null) {
            UserProfilePicture(profilePic, 120.dp, modifier = Modifier
                .size(120.dp)
                .padding(8.dp)
                .align(Alignment.CenterHorizontally))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = buildAnnotatedString {
                append("Signed In As: ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)) {
                    append(name)
                }
            },
            fontFamily = FontFamily(Font(R.font.varela)),
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = buildAnnotatedString {
                append("Highest Score: ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)) {
                    append("${uiState.highestScore}")
                }
            },
            fontFamily = FontFamily(Font(R.font.varela)),
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = buildAnnotatedString {
                append("Highest Score Pokémon: ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)) {
                    append(uiState.highestScorePokemon)
                }
            },
            fontFamily = FontFamily(Font(R.font.varela)),
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Red, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))
        CheckboxGrid(gameViewModel)
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Red, thickness = 1.dp)
        Spacer(modifier = Modifier.height(32.dp))
        if(currentUser != null){
            ShareButton(shareContent = { context -> shareContent(context)})
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { signOut() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Sign Out",
                    fontFamily = FontFamily(Font(R.font.varela)),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ShareButton(modifier: Modifier = Modifier, shareContent: (Context) -> Unit) {
    val context = LocalContext.current
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Share your profile! ",
                fontFamily = FontFamily(Font(R.font.varela)),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            ShareIconButton(
                onClick = {shareContent(context)},
                imageVector = Icons.Default.Share,
                contentDescription = "Share Icon"
            )
        }
    }
}

@Composable
fun ShareIconButton(onClick: () -> Unit, imageVector: ImageVector, contentDescription: String) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription
        )
    }
}

fun shareContent(context: Context) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "Hello, check out my profile!")
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share via"))
}

@Composable
fun UserProfilePicture(
    profilePic: String,
    imageSize: Dp,
    modifier: Modifier = Modifier
) {
    Image(
        painter = rememberAsyncImagePainter(profilePic),
        contentDescription = null,
        modifier = modifier
            .size(imageSize)
            .clip(CircleShape)
            .border(5.dp, Red, CircleShape)
            .padding(2.dp)
    )
}

@Composable
fun CheckboxGrid(gameViewModel: GameViewModel = viewModel()) {
    val romanNumerals = listOf("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX")
    val generationCheckboxStates = gameViewModel.generationCheckboxStates
    Column(
    ) {
        Text(
            text = "Difficulty Level: Select Generation(s)",
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp),
            fontFamily = FontFamily(Font(R.font.varela)),
        )
        Box(){
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(9) { index ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = generationCheckboxStates[index],
                            onCheckedChange = { isChecked ->
                                gameViewModel.updateGenerationCheckboxState(index, isChecked)
                            },
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = romanNumerals[index],
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier.padding(start = 8.dp),
                            fontFamily = FontFamily(Font(R.font.varela)),
                        )
                    }
                }
            }
        }
    }
}