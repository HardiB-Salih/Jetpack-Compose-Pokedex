package com.plcoding.jetpackcomposepokedex.pokemonDetail

import androidx.lifecycle.ViewModel
import com.hardib.salih.daggerhilt.pokedex_application.data.responses.Pokemon
import com.plcoding.jetpackcomposepokedex.data.repository.PokemonReposotory
import com.plcoding.jetpackcomposepokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val reposotory: PokemonReposotory
): ViewModel() {

    suspend fun getPokemonInfo(pokemonName: String) : Resource<Pokemon>{
        return reposotory.getPokemonInfo(pokemonName)
    }
}