package com.example.instagramme.models

data class User(
    val name: String = "", val username: String = "", val email: String = "",
    val website: String? = null, val bio: String?  = null, val phone: Any? = null,
    val photo: String? = null)