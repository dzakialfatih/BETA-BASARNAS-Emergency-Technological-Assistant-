package com.example.beta.response

data class RefugeeDetailResponse(
    val id: String,
    val name: String,
    val age: Int,
    val gender: String,
    val posko: String,
    val imageUrl: String?
)
