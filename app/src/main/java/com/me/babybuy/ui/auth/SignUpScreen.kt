package com.me.babybuy.ui.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.me.babybuy.data.Resource
import com.me.babybuy.data.model.RegistrationForm
import com.me.babybuy.ui.component.AppHeader
import com.me.babybuy.ui.component.AppTextField
import com.me.babybuy.ui.component.PasswordTextField
import com.me.babybuy.ui.navigation.ROUTE_HOME
import com.me.babybuy.ui.theme.spacing

/**
 * A [Composable] function for sign up page.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(authViewModel: AuthViewModel, navController: NavHostController) {
    var loading by remember { mutableStateOf(false) }
    val form = authViewModel.registrationForm
    val formValue by form.state.collectAsState()
    val signUpFlow = authViewModel.signUpFlow.collectAsState()
    var errorMessage by remember { mutableStateOf("") }

    Scaffold {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            val (headerRef, nameRef, emailRef, passwordRef, cfpRef, buttonRef, signInRef, errorRef) = createRefs()
            val spacing = MaterialTheme.spacing

            Box(
                modifier = Modifier
                    .constrainAs(headerRef) {
                        top.linkTo(parent.top, spacing.extraLarge)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                    .wrapContentSize()
            ) {
                AppHeader()
            }

            AppTextField(
                value = formValue.name.value.orEmpty(),
                onValueChange = { form.update(RegistrationForm::name, it) },
                isError = formValue.name.isInvalid,
                errorMessage = "Cannot be empty",
                label = "Username",
                modifier = Modifier.constrainAs(nameRef) {
                    top.linkTo(headerRef.bottom, spacing.large)
                    start.linkTo(parent.start, spacing.large)
                    end.linkTo(parent.end, spacing.large)
                    width = Dimension.fillToConstraints
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            AppTextField(
                value = formValue.email.value.orEmpty(),
                onValueChange = { form.update(RegistrationForm::email, it) },
                isError = formValue.email.isInvalid,
                errorMessage = "Cannot be empty and must be a valid email address",
                label = "Email",
                modifier = Modifier.constrainAs(emailRef) {
                    top.linkTo(nameRef.bottom, spacing.medium)
                    start.linkTo(parent.start, spacing.large)
                    end.linkTo(parent.end, spacing.large)
                    width = Dimension.fillToConstraints
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            PasswordTextField(
                value = formValue.password.value.orEmpty(),
                onValueChange = { form.update(RegistrationForm::password, it) },
                isError = formValue.password.isInvalid,
                errorMessage = "Cannot be empty",
                label = "Password",
                modifier = Modifier.constrainAs(passwordRef) {
                    top.linkTo(emailRef.bottom, spacing.medium)
                    start.linkTo(parent.start, spacing.large)
                    end.linkTo(parent.end, spacing.large)
                    width = Dimension.fillToConstraints
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                )
            )

            PasswordTextField(
                value = formValue.confirmPassword.value.orEmpty(),
                onValueChange = { form.update(RegistrationForm::confirmPassword, it) },
                isError = formValue.confirmPassword.isInvalid,
                errorMessage = "Does not match with the password",
                label = "Confirm password",
                modifier = Modifier.constrainAs(cfpRef) {
                    top.linkTo(passwordRef.bottom, spacing.medium)
                    start.linkTo(parent.start, spacing.large)
                    end.linkTo(parent.end, spacing.large)
                    width = Dimension.fillToConstraints
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.constrainAs(buttonRef) {
                        top.linkTo(cfpRef.bottom, spacing.large)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )
            } else {
                Text(
                    modifier = Modifier.constrainAs(errorRef) {
                        top.linkTo(passwordRef.bottom, spacing.medium)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    text = errorMessage,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error
                )

                Button(
                    onClick = {
                        authViewModel.signUp()
                    },
                    modifier = Modifier.constrainAs(buttonRef) {
                        top.linkTo(cfpRef.bottom, spacing.large)
                        start.linkTo(parent.start, spacing.extraLarge)
                        end.linkTo(parent.end, spacing.extraLarge)
                        width = Dimension.fillToConstraints
                    }
                ) {
                    Text(
                        text = "Sign up",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Text(
                modifier = Modifier
                    .constrainAs(signInRef) {
                        top.linkTo(buttonRef.bottom, spacing.medium)
                        start.linkTo(parent.start, spacing.extraLarge)
                        end.linkTo(parent.end, spacing.extraLarge)
                    }
                    .clickable {
                        navController.popBackStack()
                    }
                    .padding(horizontal = spacing.large),
                text = "Already have an account? Click here to sign in",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            signUpFlow.value.let {
                loading = it is Resource.Loading
                if (it is Resource.Failure) {
                    errorMessage = it.errorMessage
                } else if (it is Resource.Success) {
                    LaunchedEffect(Unit) {
                        navController.navigate(ROUTE_HOME) {
                            launchSingleTop = true
                            popUpTo(ROUTE_HOME) { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}