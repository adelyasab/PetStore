package ru.itis.catstore.users


case class User(id: Long, login: String, passwordHash: String)
