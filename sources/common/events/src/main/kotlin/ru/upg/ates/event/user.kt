package ru.upg.ates.event

import ru.upg.ates.AtesEvent


data class User(val id: String)

data class UserCreated(
    val user: User
) : AtesEvent
