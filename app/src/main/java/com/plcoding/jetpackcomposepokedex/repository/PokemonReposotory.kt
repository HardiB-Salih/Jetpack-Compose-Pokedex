package com.plcoding.jetpackcomposepokedex.repository

import com.hardib.salih.daggerhilt.pokedex_application.data.responses.Pokemon
import com.hardib.salih.daggerhilt.pokedex_application.data.responses.PokemonList
import com.plcoding.jetpackcomposepokedex.remote.PokeApi
import com.plcoding.jetpackcomposepokedex.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PokemonReposotory @Inject constructor(
    private val api: PokeApi
){

    suspend fun getPokemonList(limit: Int ,offset: Int): Resource<PokemonList> {
        val response = try {
            api.getPokemonList(limit, offset)
        } catch (e: Exception) {
            return  Resource.Error("An unknown Error Occurred")
        }
        return Resource.Success(response)
    }

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        val response = try {
            api.getPokemonInfo(pokemonName)
        } catch (e: Exception) {
            return  Resource.Error("An unknown Error Occurred")
        }
        return Resource.Success(response)
    }

}