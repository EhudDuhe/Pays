package com.ehud.pays.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.ehud.pays.R
import com.ehud.pays.model.Country
import com.ehud.pays.viewmodel.CountryViewModel

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val viewModel: CountryViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onViewAllClick = {
                    viewModel.fetchAllCountries()
                    navController.navigate("countryList")
                },
                onViewAfricaClick = {
                    viewModel.fetchAfricanCountries()
                    navController.navigate("countryList")
                }
            )
        }
        composable("countryList") {
            CountryListScreen(viewModel = viewModel)
        }
    }
}

@Composable
fun HomeScreen(onViewAllClick: () -> Unit, onViewAfricaClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "karibu",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 64.sp
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onViewAllClick,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Black)
        ) {
            Text("voir les pays", color = androidx.compose.ui.graphics.Color.White)
        }

        Button(
            onClick = onViewAfricaClick,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Black)
        ) {
            Text("voir les pays africains", color = androidx.compose.ui.graphics.Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryListScreen(viewModel: CountryViewModel) {
    val countries by viewModel.filteredCountries.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Liste pays", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchBar(
                query = searchQuery,
                onQueryChanged = { viewModel.onSearchQueryChanged(it) }
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(countries) { country ->
                        CountryItem(country)
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChanged: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        placeholder = { Text("Rechercher un pays...") },
        singleLine = true,
        leadingIcon = {
            Icon(
                painter = painterResource(android.R.drawable.ic_menu_search),
                contentDescription = null
            )
        }
    )
}

@Composable
fun CountryItem(country: Country) {
    val flagUrl = country.flags?.png ?: ""
    val name = country.name?.common ?: "Inconnu"
    
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(flagUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Flag of $name",
            error = painterResource(R.drawable.error),
            placeholder = painterResource(R.drawable.load),
            modifier = Modifier.width(80.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                text = "${country.capital?.joinToString() ?: "N/A"} / ${country.continents?.firstOrNull() ?: "N/A"}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
