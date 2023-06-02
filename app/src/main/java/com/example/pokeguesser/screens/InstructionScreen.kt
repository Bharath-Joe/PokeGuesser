package com.example.pokeguesser

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun InstructionScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.padding(32.dp),
    ) {
        Text(
            text = "Welcome to PokéGuesser!",
            fontFamily = FontFamily(Font(R.font.brunoace)),
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Train to be a Pokémon Master.",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontFamily = FontFamily(Font(R.font.roboto))
        )
        Spacer(modifier = Modifier.height(16.dp))
        Rules()
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("home")},
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
        ) {
            Text(text = "Log In", fontFamily = FontFamily(Font(R.font.roboto)))
        }
        ClickableText(
            text = AnnotatedString("Don't have an account? Sign Up"),
            style = TextStyle(textDecoration = TextDecoration.Underline),
            onClick = {},
            modifier = Modifier.align(Alignment.CenterHorizontally))
        Image(
            painter = painterResource(id = R.drawable.pokemon_image),
            contentDescription = "",
            modifier = Modifier.align(Alignment.CenterHorizontally))
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