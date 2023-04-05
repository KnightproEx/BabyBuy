package com.me.babybuy.util

fun String.formatCurrency(): String {
    return when {
        this == "0" -> this
        length == 2 -> "0.$this"
        length == 1 -> "0.0$this"
        this.isEmpty() -> ""
        else -> "${substring(0, lastIndex - 1)}.${substring(lastIndex - 1, length)}"
    }
}