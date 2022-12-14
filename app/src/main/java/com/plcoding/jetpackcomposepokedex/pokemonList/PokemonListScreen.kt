package com.plcoding.jetpackcomposepokedex.pokemonList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.isFocused
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import coil.request.ImageRequest
import com.google.accompanist.coil.CoilImage
import com.plcoding.jetpackcomposepokedex.R
import com.plcoding.jetpackcomposepokedex.module.PokedexListEntry
import com.plcoding.jetpackcomposepokedex.module.PokemonListViewModel
import com.plcoding.jetpackcomposepokedex.ui.theme.RobotoCondensed
import com.plcoding.jetpackcomposepokedex.util.Constants.POKEMON_DETAIL_SCREEN


@Composable
fun PokemonListScreen(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltNavGraphViewModel()
) {
    Surface(color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Image(painter = painterResource(id = R.drawable.ic_international_pok_mon_logo )
                , contentDescription = "Pokemon"
            , modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterHorizontally)
            )
            SearchBar(
                hint = "Searching...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ){
                //TODO: HERE WE IMPLEMENT THE SEARCH LOGIC
                viewModel.searchPokemonList(it)
            }
            Spacer(modifier = Modifier.height(16.dp))
            PokemonList(navController = navController)
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    onSearch : (String) -> Unit = {}
) {
    var text by remember {
        mutableStateOf("")
    }

    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }

    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = it != FocusState.Active
                }
        )
        if (isHintDisplayed) {
            if (text.isEmpty()){
                Text(text = hint,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    color = Color.LightGray
                )
            }
        }
    }
}

@Composable
fun PokemonList(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltNavGraphViewModel()
){
    val pokemonList by remember { viewModel.pokemonList }
    val endReached by remember { viewModel.endReached }
    val isLoading by remember { viewModel.isLoading }
    val loadError by remember { viewModel.loadError }
    val isSearching by remember { viewModel.isSearching }

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        val itemCount = if (pokemonList.size % 2 == 0) {
            pokemonList.size / 2
        } else {
            pokemonList.size / 2 + 1
        }
        items(itemCount){
            if (it >= itemCount -1 && !endReached && !isLoading && !isSearching) {
                LaunchedEffect(key1 = true){
                    viewModel.loadPokemonPaginated()
                }
            }
            PokemonRow(rowIndex = it,
                entries = pokemonList,
                navController = navController)
        }
    }
    Box(contentAlignment = Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading){
            CircularProgressIndicator(color = MaterialTheme.colors.primary)
        }
        if (loadError.isNotEmpty()) {
            RetrySection(error = loadError) {
                viewModel.loadPokemonPaginated()
            }
        }
    }

}


@Composable
fun PokedexEntery(
    entry: PokedexListEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel = hiltNavGraphViewModel()
) {

    val defaultDominantColor = MaterialTheme.colors.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }
    Box(contentAlignment = Alignment.Center, modifier = modifier
        .shadow(elevation = 5.dp, shape = RoundedCornerShape(10.dp))
        .clip(RoundedCornerShape(10.dp))
        .background(
            brush = Brush.verticalGradient(
                listOf(
                    dominantColor,
                    defaultDominantColor
                )
            )
        )
        .clickable {
            navController.navigate(
                "$POKEMON_DETAIL_SCREEN/${dominantColor.toArgb()}/${entry.pokemonName}"
            )
        }
    ) {
        Column {
            CoilImage(request = ImageRequest.Builder(LocalContext.current)
                .data(entry.imageUrl)
                .target {
                    viewModel.calcDominantColor(it) { color ->
                        dominantColor = color
                    }
                }
                .build(),
                contentDescription = entry.pokemonName,
                fadeIn = true,
                modifier = Modifier
                    .size(120.dp)
                    .align(CenterHorizontally)) {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.scale(0.5f)
                )
            }
            Text(
                text = entry.pokemonName,
                fontFamily = RobotoCondensed,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun PokemonRow(
    rowIndex: Int,
    entries: List<PokedexListEntry>,
    navController: NavController
){
    Column {
        Row {
            PokedexEntery(
                entry = entries[rowIndex * 2],
                navController = navController,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            if (entries.size >= rowIndex * 2 + 2) {
                PokedexEntery(
                    entry = entries[rowIndex * 2 + 1],
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Composable
fun RetrySection(
    error: String,
    onRetry : () -> Unit
){
    Column {
        Text(text = error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onRetry() },
            modifier = Modifier.align(CenterHorizontally)
        ) {
            Text(text = "Retry")
        }
    }

}
