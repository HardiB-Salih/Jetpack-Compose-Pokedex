package com.hardib.salih.daggerhilt.pokedex_application.data.responses

data class PokemonList(
    val count: Int,
    val next: String,
    val previous: Any,
    val results: List<Result>
)