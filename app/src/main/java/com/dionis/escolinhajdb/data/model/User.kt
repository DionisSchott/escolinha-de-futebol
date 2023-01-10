package com.dionis.escolinhajdb.data.model

data class User(
    val email: String,
    val password: String,
    val authenticated: Boolean
)
