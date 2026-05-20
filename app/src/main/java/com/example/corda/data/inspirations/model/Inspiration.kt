package com.example.corda.data.inspirations.model

data class Inspiration(
    val id: String,
    val name: String,
    val description: String,
    val labels: List<String>,
    val imageUri: String? = null,
    val attributes: List<InspirationAttribute> = emptyList()
)

data class InspirationAttribute(
    val id: String,
    val label: String,
    val url: String
)
