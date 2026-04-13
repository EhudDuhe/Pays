package com.ehud.pays.model

import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName("name") val name: Name? = null,
    @SerializedName("flags") val flags: Flags? = null,
    @SerializedName("capital") val capital: List<String>? = null,
    @SerializedName("population") val population: Long? = 0,
    @SerializedName("continents") val continents: List<String>? = emptyList()
)

data class Name(
    @SerializedName("common") val common: String? = ""
)

data class Flags(
    @SerializedName("png") val png: String? = ""
)
