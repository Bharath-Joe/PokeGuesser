package com.example.pokeguesser

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pokeguesser.data.BottomNavItem
import com.example.pokeguesser.model.GameViewModel
import com.example.pokeguesser.screens.CorrectScreen
import com.example.pokeguesser.screens.UserScreen
import com.example.pokeguesser.ui.theme.Red
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavigationScreen() {
    val context = LocalContext.current
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val bottomNavigationItems = listOf(
        BottomNavItem(
            name = "Instructions",
            route = "instructions",
            icon = Icons.Rounded.Info,
        ),
        BottomNavItem(
            name = "Home",
            route = "home",
            icon = Icons.Rounded.Home,
        ),
        BottomNavItem(
            name = "User Profile",
            route = "user",
            icon = Icons.Rounded.Person,
        )
    )
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            PokemonAppBottomNavigation(navController, bottomNavigationItems)
        },
    ) {
        MainScreenNavigationConfigurations(navController ,gso)
    }
}

@Composable
fun MainScreenNavigationConfigurations(navController: NavHostController, gso: GoogleSignInOptions, gameViewModel: GameViewModel = viewModel()){
    NavHost(navController = navController, startDestination = "instructions") {
        composable("home") {
            GradientBackground {
                HomeScreen(navController, gameViewModel)
            }
        }
        composable("instructions") {
            GradientBackground {
                InstructionsOnly(navController, gso)
            }
        }
        composable("correct") {
            GradientBackground {
                CorrectScreen(gameViewModel, onPlayAgain = {
                    gameViewModel.resetGameNonSuspend()
                    navController.navigate("home")
                })
            }
        }
        composable("user") {
            GradientBackground {
                UserScreen(gameViewModel, gso, navController)
            }
        }
    }
}

@Composable
fun GradientBackground(content: @Composable () -> Unit) {
    val gradientColors = listOf(Color(0xFFFFFFFF), Color(0xFFFFFFFF)) // Example gradient colors

    Box(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(gradientColors),
                alpha = 0.8f // Adjust the alpha value to your preference
            )
            .fillMaxSize()
    ) {
        content()
    }
}

@Composable
fun PokemonAppBottomNavigation(navController: NavHostController, items: List<BottomNavItem>){
    val backStackEntry = navController.currentBackStackEntryAsState()
    NavigationBar(containerColor = Red, modifier = Modifier.height(50.dp)) {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            NavigationBarItem(
                selected = selected,
                onClick = { navController.navigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = "${item.name} Icon",
                        tint = Color.White
                    )
                },
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFE26369))
            )
        }
    }
}