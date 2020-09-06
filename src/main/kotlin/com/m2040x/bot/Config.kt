package com.m2040x.bot

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class Config(
    val token: String,
    val nsfwEmojiId: String,
    val adminRole: String
)

val config = Json.parse(Config.serializer(), File("config.json").readText())
