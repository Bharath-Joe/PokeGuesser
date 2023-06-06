package com.example.pokeguesser.model

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeguesser.data.PokemonApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel() : ViewModel() {
    private val pokemonService = PokemonApiService.create()
    var userGuess by mutableStateOf("")
    var elapsedMilliseconds by mutableStateOf(0L)
    private val _generationCheckboxStates = mutableStateListOf<Boolean>().apply { repeat(9) { add(true) } }
    val generationCheckboxStates: List<Boolean> get() = _generationCheckboxStates
    private val _uiState = MutableStateFlow(GameUIState())
    val uiState: StateFlow<GameUIState> = _uiState.asStateFlow()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    private var currentPokemonId: Int? = null
    var curPokemon = ""

    init {
        viewModelScope.launch {
            resetGame()
        }
    }

    fun resetGameNonSuspend() {
        viewModelScope.launch {
            resetGame()
        }
    }

    fun updateUserGuess(guess: String){
        userGuess = guess
    }

    suspend fun resetGame() {
        currentPokemonId = pickRandomPokemonID()
        userGuess = ""
        curPokemon = getPokemonfromId(currentPokemonId!!)
        _uiState.value = GameUIState(
            currentPokemon = getPokemonfromId(currentPokemonId!!),
            currentPokemonTypes = getPokemonTypesfromId(currentPokemonId!!),
            currentPokemonHeight = getPokemonHeightfromId(currentPokemonId!!),
            currentPokemonWeight = getPokemonWeightfromId(currentPokemonId!!),
            currentPokemonGeneration = getPokemonGenerationfromId(currentPokemonId!!),
            currentPokemonFirstLetter = getPokemonFirstLetter(curPokemon),
            currentPokemonBaby = getPokemonBabyfromId(currentPokemonId!!),
            currentPokemonLegendary = getPokemonLegendaryfromId(currentPokemonId!!),
            currentPokemonMythical = getPokemonMythicalfromId(currentPokemonId!!),
            currentPokemonFront = getPokemonSpritefromId(currentPokemonId!!),
        )
    }

    fun checkUserGuess(){
        val newGuess = userGuess.replace(" ", "")
        if(newGuess.equals(curPokemon, ignoreCase = true)){
            _uiState.value.score = 100 - (_uiState.value.currentHintClickCount*5) - ((elapsedMilliseconds/1000) / 5)
            if(_uiState.value.score > _uiState.value.highestScore){
                _uiState.value.highestScore = _uiState.value.score
                _uiState.value.highestScorePokemon = curPokemon
                if (currentUser != null) {
                    val userDocument = db.collection("users").document(currentUser.email ?: "")
                    val userData = hashMapOf(
                        "highestScore" to _uiState.value.highestScore,
                        "highestScorePokemon" to _uiState.value.highestScorePokemon
                    )
                    userDocument.set(userData, SetOptions.merge())
                        .addOnSuccessListener {
                            Log.d(TAG, "User document updated or created successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error updating or creating user document", e)
                        }
                } else {
                    Log.d(TAG, "User is not authenticated")
                }
            }

        }
        else{
            _uiState.update { currentState ->
                currentState.copy(isUserCorrect = false)
            }
        }
    }


    fun updateHintCount(){
        _uiState.update { currentState ->
            currentState.copy(currentHintClickCount = currentState.currentHintClickCount.inc())
        }
    }

    private fun pickRandomPokemonID(): Int {
        val selectedGenerations = generationCheckboxStates
            .mapIndexed { index, isChecked -> if (isChecked) index + 1 else null }
            .filterNotNull()

        val generationRanges = selectedGenerations.map { generation ->
            when (generation) {
                1 -> 1..151 // Range for Generation I
                2 -> 152..251 // Range for Generation II
                3 -> 252..386 // Range for Generation III
                4 -> 387..493 // Range for Generation IV
                5 -> 494..649 // Range for Generation V
                6 -> 650..721 // Range for Generation VI
                7 -> 722..809 // Range for Generation VII
                8 -> 810..905 // Range for Generation VIII
                9 -> 906..1010 // Range for Generation IX
                else -> throw IllegalArgumentException("Invalid generation: $generation")
            }
        }

        val availablePokemonIds = generationRanges.flatMap { it }
        if (availablePokemonIds.isEmpty()) {
            return -1
        }

        return availablePokemonIds.random()
    }

    fun updateGenerationCheckboxState(index: Int, isChecked: Boolean) {
        _generationCheckboxStates[index] = isChecked
    }

    private suspend fun getPokemonfromId(id: Int): String {
        val pokemon = pokemonService.getPokemonById(id)
        return pokemon.name
    }

    private suspend fun getPokemonBabyfromId(id: Int): Boolean {
        val pokemon = pokemonService.getPokemonSpeciesById(id)
        return pokemon.is_baby
    }

    private suspend fun getPokemonLegendaryfromId(id: Int): Boolean {
        val pokemon = pokemonService.getPokemonSpeciesById(id)
        return pokemon.is_legendary
    }

    private suspend fun getPokemonMythicalfromId(id: Int): Boolean {
        val pokemon = pokemonService.getPokemonSpeciesById(id)
        return pokemon.is_mythical
    }

    private suspend fun getPokemonTypesfromId(id: Int): ArrayList<String> {
        val typesList = arrayListOf<String>()
        val pokemon = pokemonService.getPokemonById(id)
        for(type in pokemon.types){
            typesList.add(type.type.name)
        }
        return typesList
    }

    private suspend fun getPokemonWeightfromId(id: Int): Double {
        val pokemon = pokemonService.getPokemonById(id)
        val weightInPounds = pokemon.weight * 0.220462
        return String.format("%.1f", weightInPounds).toDouble()
    }

    private suspend fun getPokemonHeightfromId(id: Int): String {
        val pokemon = pokemonService.getPokemonById(id)
        val heightInInches = pokemon.height * 3.93701
        val totalFeet = (heightInInches / 12).toInt()
        val remainingInches = (heightInInches % 12).toInt()
        return "$totalFeet ft $remainingInches in"
    }

    private suspend fun  getPokemonGenerationfromId(id: Int): String {
        val pokemon = pokemonService.getPokemonSpeciesById(id)
        return pokemon.generation.name
    }

    private suspend fun getPokemonSpritefromId(id: Int): String {
        val pokemon = pokemonService.getPokemonById(id)
        return pokemon.sprites.front_default
    }

    private fun getPokemonFirstLetter(name: String): String {
        return name.first().uppercase()
    }


}