package com.me.babybuy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.me.babybuy.ui.auth.AuthViewModel
import com.me.babybuy.ui.auth.SignInScreen
import com.me.babybuy.ui.auth.SignupScreen
import com.me.babybuy.ui.home.CreateItemScreen
import com.me.babybuy.ui.home.HomeScreen
import com.me.babybuy.ui.home.ItemViewModel

/**
 * A [Composable] function for navigation with the [NavHost] Composable.
 */
@Composable
fun AppNavHost(
    authViewModel: AuthViewModel,
    itemViewModel: ItemViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_SIGN_IN,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ROUTE_SIGN_IN) {
            SignInScreen(authViewModel, navController)
        }
        composable(ROUTE_SIGN_UP) {
            SignupScreen(authViewModel, navController)
        }
        composable(ROUTE_HOME) {
            HomeScreen(authViewModel, itemViewModel, navController)
        }
        composable(ROUTE_CREATE_ITEM) {
            CreateItemScreen(
                authViewModel,
                itemViewModel,
                navController
            )
        }
    }
}