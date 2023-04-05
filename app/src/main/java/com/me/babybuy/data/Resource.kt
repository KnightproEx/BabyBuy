package com.me.babybuy.data

sealed class Resource<out T> {
    object Loading: Resource<Nothing>()
    data class Success<out T>(val data: T): Resource<T>()
    data class Failure(val errorMessage: String): Resource<Nothing>()
}
