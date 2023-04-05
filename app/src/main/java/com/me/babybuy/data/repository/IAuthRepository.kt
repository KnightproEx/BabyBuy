package com.me.babybuy.data.repository

import com.google.firebase.auth.FirebaseUser
import com.me.babybuy.data.Resource

/**
 * An interface for [AuthRepository].
 */
interface IAuthRepository {
    val currentUser: FirebaseUser?
    suspend fun signIn(email: String, password: String): Resource<FirebaseUser>
    suspend fun signUp(name: String, email: String, password: String): Resource<FirebaseUser>
    fun signOut()
}
