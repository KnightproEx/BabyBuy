package com.me.babybuy.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.me.babybuy.R
import com.me.babybuy.ui.navigation.ROUTE_SIGN_IN
import com.me.babybuy.ui.auth.AuthViewModel
import com.me.babybuy.ui.theme.spacing

/**
 * A [Composable] function for custom appbar Composable.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    authViewModel: AuthViewModel,
    navController: NavHostController,
    floatingActionButton: @Composable () -> Unit = {},
    hasSearchBar: Boolean = false,
    onSearchChanged: (String) -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    var showSearch by rememberSaveable { mutableStateOf(false) }
    var showActions by rememberSaveable { mutableStateOf(false) }
    var search by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = navigationIcon,
                title = {
                    if (!showSearch) Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            modifier = Modifier
                                .size(20.dp),
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = null
                        )
                        Text(
                            text = "BabyBuy",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                actions = {
                    if (!hasSearchBar) {
                        return@TopAppBar
                    }

                    if (showSearch) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(MaterialTheme.spacing.extraLarge)
                                .padding(horizontal = MaterialTheme.spacing.medium),
                            value = search,
                            label = { Text("Search") },
                            singleLine = true,
                            onValueChange = {
                                search = it
                                onSearchChanged(it.trim().lowercase())
                            },
                            leadingIcon = {
                                IconButton(
                                    onClick = { showSearch = !showSearch },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = null,
                                    )
                                }
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        search = ""; onSearchChanged("")
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                    )
                                }
                            }
                        )
                    }

                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }

                    IconButton(onClick = { showActions = !showActions }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More actions"
                        )
                    }
                    DropdownMenu(
                        expanded = showActions,
                        onDismissRequest = { showActions = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sign out") },
                            leadingIcon = { Icon(Icons.Default.Logout, null) },
                            onClick = {
                                authViewModel.signOut()
                                navController.navigate(ROUTE_SIGN_IN) {
                                    launchSingleTop = true
                                    popUpTo(ROUTE_SIGN_IN) { inclusive = true }
                                }
                            },
                        )
                    }
                }
            )
        },
        floatingActionButton = floatingActionButton,
        content = content
    )
}
