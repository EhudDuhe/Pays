package com.ehud.pays.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehud.pays.data.CountryApi
import com.ehud.pays.model.Country
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CountryViewModel : ViewModel() {
    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Liste filtrée basée sur la recherche
    val filteredCountries: StateFlow<List<Country>> = combine(_countries, _searchQuery) { countries, query ->
        if (query.isBlank()) {
            countries
        } else {
            countries.filter { 
                it.name?.common?.contains(query, ignoreCase = true) == true ||
                it.capital?.any { cap -> cap.contains(query, ignoreCase = true) } == true
            }
        }
    }.let { flow ->
        val stateFlow = MutableStateFlow<List<Country>>(emptyList())
        viewModelScope.launch {
            flow.collect { stateFlow.value = it }
        }
        stateFlow.asStateFlow()
    }

    private val api = CountryApi.create()

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun fetchAllCountries() {
        fetch { api.getCountries() }
    }

    fun fetchAfricanCountries() {
        fetch { api.getCountriesByRegion("africa") }
    }

    private fun fetch(call: suspend () -> List<Country>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("CountryViewModel", "Début du chargement...")
                val result = call()
                Log.d("CountryViewModel", "Succès ! Nombre de pays récupérés : ${result.size}")
                _countries.value = result
            } catch (e: Exception) {
                Log.e("CountryViewModel", "Erreur lors du chargement : ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
