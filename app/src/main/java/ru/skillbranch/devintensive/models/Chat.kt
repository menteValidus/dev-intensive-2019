package ru.skillbranch.devintensive.models

class Chat (
    val id: String = "0",
    val members: MutableList<User> = mutableListOf(),
    val messages: MutableList<BaseMessage> = mutableListOf()
)