package com.example.instagramme.models

data class User(val name: String = "", val username: String = "", val website: String = "",
                val bio: String = "", val email: String = "", val phone: String = "",
                val photo: String? = null)