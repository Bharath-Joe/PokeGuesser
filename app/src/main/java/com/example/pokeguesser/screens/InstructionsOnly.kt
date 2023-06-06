package com.example.pokeguesser


import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn


@Composable
fun InstructionsOnly(navController: NavHostController, gso: GoogleSignInOptions) {
    val context = LocalContext.current
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener { signInTask ->
                        if (signInTask.isSuccessful) {
                            navController.navigate("home")
                        } else {
                            Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: ApiException) {
                Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier.padding(32.dp),
    ) {
        Text(
            text = "Welcome to PokéGuesser!",
            fontFamily = FontFamily(Font(R.font.varela)),
            fontSize = 25.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Train to be a Pokémon Master.",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontFamily = FontFamily(Font(R.font.varela))
        )
        Spacer(modifier = Modifier.height(48.dp))
        Rules()
        Spacer(modifier = Modifier.height(16.dp))
        if(currentUser == null){
            Button(
                onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    signInLauncher.launch(signInIntent)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Sign in with Google", fontFamily = FontFamily(Font(R.font.varela)), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun Rules() {
    val instructions = listOf(
        "1. Your mission is to guess the elusive Pokémon and achieve the highest score possible!\n",
        "2. Listen to the sound of the Pokémon to gather clues for your guess. Don't worry, it won't affect your score!\n",
        "3. Use hints provided to aid in your guess, but beware! Your final score will be impacted by the number of hints you use.\n",
        "4. Time is on your side with no limit, but remember, the longer you take, the more your score will be affected!\n",
        "5. Sharpen your spelling skills! Make sure to correctly spell the Pokémon's name for a successful guess.\n",
        "6. Get ready to embark on this exciting challenge and have a blast!"
    )
    Text(
        text = "Instructions: ",
        fontFamily = FontFamily(Font(R.font.varela)),
        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
    )
    for (instruction in instructions){
        Text(
            text = instruction,
            fontSize = 15.sp,
            fontFamily = FontFamily(Font(R.font.varela)))
    }
}