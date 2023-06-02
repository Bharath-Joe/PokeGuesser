package com.example.pokeguesser.data

data class Pokemon(
    val name: String,
    val weight: Int,
    val height: Int,
    val types: ArrayList<Type>,
    val sprites: Sprite,
    val is_baby: Boolean,
    val is_legendary: Boolean,
    val is_mythical: Boolean,
    val generation: Generation
)

data class Generation (
    val name: String,
)

data class Sprite(
    val front_default: String,
)

data class Type(
    val slot: Int,
    val type: TypeInfo
)

data class TypeInfo(
    val name: String,
    val url: String
)