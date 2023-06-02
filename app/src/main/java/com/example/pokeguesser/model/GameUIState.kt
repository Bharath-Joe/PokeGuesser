package com.example.pokeguesser.model

data class GameUIState(
    val currentPokemon: String = "",
    val currentPokemonTypes: ArrayList<String> = arrayListOf(),
    val currentPokemonHeight: String = "",
    val currentPokemonWeight: Double = 0.0,
    val currentPokemonFirstLetter: String = "",
    val currentPokemonFront: String = "",
    val currentPokemonGeneration: String = "",
    val currentPokemonBaby: Boolean = false,
    val currentPokemonLegendary: Boolean = false,
    val currentPokemonMythical: Boolean = false,
    val isUserCorrect: Boolean = true,
    var score: Long = 0,
    var currentHintClickCount: Int = 0,
    var highestScore: Long = 0,
    var highestScorePokemon: String = ""
)
