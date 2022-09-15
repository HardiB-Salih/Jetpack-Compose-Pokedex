package com.plcoding.jetpackcomposepokedex.util

object Constants {
    const val BASE_URL = "https://pokeapi.co/api/v2/"
    const val PAGE_SIZE = 100

    const val POKEMON_LIST_SCREEN = "pokemon_list_screen"
    const val POKEMON_DETAIL_SCREEN = "pokemon_detail_screen"

    fun getPokemonImageUrl(number: String): String {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
    }
}