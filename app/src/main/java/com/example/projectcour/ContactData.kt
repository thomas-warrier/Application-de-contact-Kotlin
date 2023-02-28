package com.example.projectcour

import java.io.Serializable

data class ContactData(
    val photoUri : String,
    val nom: String,
    val prenom: String,
    val email: String,
    val naissance: String,
    val telephone: String,
    val genre: Genre,
    val favoris : Boolean
):Serializable

enum class Genre {
    HOMME,
    FEMME,
    AUTRE
}

