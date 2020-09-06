package com.m2040x.bot

import kotlinx.serialization.Serializable

@Serializable
data class DeletionLog(val user: String, val time: String, val votes: List<String>)
