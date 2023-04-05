package com.me.babybuy.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.me.babybuy.data.Resource
import com.me.babybuy.data.model.LoginForm
import com.me.babybuy.data.model.RegistrationForm
import com.me.babybuy.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.boguszpawlowski.chassis.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A [ViewModel] class for authentication.
 * Contains functions to perform sign in, sign up and sign out.
 * Uses [MutableStateFlow] for authentication state.
 * Collect [StateFlow] to read changes in authentication state.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {
//    val currentUser: FirebaseUser? get() = repository.currentUser

    private val _signInFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val signInFlow: StateFlow<Resource<FirebaseUser>?> = _signInFlow

    private val _signUpFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val signUpFlow: StateFlow<Resource<FirebaseUser>?> = _signUpFlow

    /**
     * A [Chassis] of [LoginForm].
     */
    @Suppress("RemoveExplicitTypeArguments")
    val loginForm = chassis<LoginForm> {
        LoginForm(
            email = field(initialValue = "") {
                validators(notEmpty())
                reduce { copy(email = it) }
            },
            password = field(initialValue = "") {
                validators(notEmpty())
                reduce { copy(password = it) }
            },
        )
    }

    /**
     * A [Chassis] of [RegistrationForm].
     */
    @Suppress("RemoveExplicitTypeArguments")
    val registrationForm = chassis<RegistrationForm> {
        var p = ""
        RegistrationForm(
            name = field(initialValue = "") {
                validators(notEmpty())
                reduce { copy(name = it) }
            },
            email = field(initialValue = "") {
                validators(notEmpty(), matches(Patterns.EMAIL_ADDRESS.toRegex()))
                reduce { copy(email = it) }
            },
            password = field(initialValue = "") {
                validators(notEmpty())
                reduce {
                    p = it()
                    copy(password = it)
                }
            },
            confirmPassword = field(initialValue = "") {
                validators(Validator {
                    if (it.equals(p)) Valid else DefaultInvalid
                })
                reduce { copy(confirmPassword = it) }
            },
        )
    }

    init {
        repository.currentUser?.let {
            _signInFlow.value = Resource.Success(it)
        }
    }

    /**
     * A job function for signing in.
     */
    fun signIn() = viewModelScope.launch {
        with(loginForm()) {
            if (!isValid) return@launch
            _signInFlow.value = Resource.Loading
            val result = repository.signIn(email(), password())
            _signInFlow.value = result
        }
    }

    /**
     * A job function for signing up.
     */
    fun signUp() = viewModelScope.launch {
        with(registrationForm()) {
            if (!isValid) return@launch
            _signUpFlow.value = Resource.Loading
            val result = repository.signUp(
                name(),
                email(),
                password()
            )
            _signUpFlow.value = result
        }
    }

    /**
     * A function for signing out.
     */
    fun signOut() {
        repository.signOut()
        _signInFlow.value = null
    }

    /**
     * A function to reset the [loginForm].
     */
    fun resetSignInForm() {
        loginForm.reset()
    }

    // TODO: Check on this
    fun resetSignUpFlow() {
        registrationForm.reset()
        _signUpFlow.value = null
    }
}