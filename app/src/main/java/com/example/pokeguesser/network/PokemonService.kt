package com.example.pokeguesser.network

import com.example.pokeguesser.data.Pokemon
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonService {
    @GET("pokemon/{id}")
    suspend fun getPokemonById(@Path("id") name: Int): Pokemon

    @GET("pokemon-species/{id}")
    suspend fun getPokemonSpeciesById(@Path("id") name: Int): Pokemon
}