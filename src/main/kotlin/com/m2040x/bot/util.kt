package com.m2040x.bot

import com.jessecorbett.diskord.api.model.User

fun Int.suffix(string: String): String{
    if(this == 1){
        return "$this $string"
    }
    return "$this ${string}s"
}


fun List<User>.mention() = joinToString(", "){
    "<@${it.id}>"
}
