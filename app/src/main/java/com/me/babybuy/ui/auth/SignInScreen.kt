package com.me.babybuy.ui.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.me.babybuy.data.Resource
import com.me.babybuy.data.model.LoginForm
import com.me.babybuy.ui.component.AppHeader
import com.me.babybuy.ui.component.AppTextField
import com.me.babybuy.ui.component.PasswordTextField
import com.me.babybuy.ui.navigation.ROUTE_HOME
import com.me.babybuy.ui.navigation.ROUTE_SIGN_UP
import com.me.babybuy.ui.theme.spacing

/**
 * A [Composable] function for sign in page.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(viewModel: AuthViewModel, navController: NavController) {
    val signInFlow = viewModel.signInFlow.collectAsState()
    val form = viewModel.loginForm
    val formValue by form.state.collectAsState()
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.resetSignInForm()
    }

    Scaffold {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (headerRef, emailRef, passwordRef, loginButtonRef, signUpTestRef, errorRef) = createRefs()
            val spacing = MaterialTheme.spacing

            signInFlow.value.let {
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
                modifier = Modifier.constrainAs(emailRef) {
                    top.linkTo(headerRef.bottom, spacing.extraLarge)
                    start.linkTo(parent.start, spacing.large)
                    end.linkTo(parent.end, spacing.large)
                    width = Dimension.fillToConstraints
                },
                value = formValue.email.value.orEmpty(),
                onValueChange = { form.update(LoginForm::email, it) },
                isError = formValue.email.isInvalid,
                label = "Email",
                errorMessage = "Cannot be empty",
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            PasswordTextField(
                value = formValue.password.value.orEmpty(),
                modifier = Modifier.constrainAs(passwordRef) {
                    top.linkTo(emailRef.bottom, spacing.medium)
                    start.linkTo(parent.start, spacing.large)
                    end.linkTo(parent.end, spacing.large)
                    width = Dimension.fillToConstraints
                },
                label = "Password",
                onValueChange = { form.update(LoginForm::password, it) },
                isError = formValue.password.isInvalid,
                errorMessage = "Cannot be empty",
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
            )

            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.constrainAs(loginButtonRef) {
                        top.linkTo(passwordRef.bottom, spacing.large)
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
                    onClick = { viewModel.signIn() },
                    modifier = Modifier.constrainAs(loginButtonRef) {
                        top.linkTo(errorRef.bottom, spacing.extraSmall)
                        start.linkTo(parent.start, spacing.extraLarge)
                        end.linkTo(parent.end, spacing.extraLarge)
                        width = Dimension.fillToConstraints
                    }
                ) {
                    Text(
                        text = "Sign in",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }

            Text(
                modifier = Modifier
                    .constrainAs(signUpTestRef) {
                        top.linkTo(loginButtonRef.bottom, spacing.medium)
                        start.linkTo(parent.start, spacing.extraLarge)
                        end.linkTo(parent.end, spacing.extraLarge)
                    }
                    .clickable {
                        navController.navigate(ROUTE_SIGN_UP) {
                            launchSingleTop = true
                        }
                    }
                    .padding(horizontal = spacing.large),
                text = "Do not have an account? Click here to sign up",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
