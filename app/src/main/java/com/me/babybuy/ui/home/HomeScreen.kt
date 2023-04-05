package com.me.babybuy.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.me.babybuy.data.Resource
import com.me.babybuy.data.model.Item
import com.me.babybuy.ui.auth.AuthViewModel
import com.me.babybuy.ui.navigation.ROUTE_CREATE_ITEM
import com.me.babybuy.ui.theme.spacing

/**
 * A [Composable] function for home page.
 */
// TODO: Empty list
// TODO: Select and export as text (Delegate)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    itemViewModel: ItemViewModel,
    navController: NavHostController,
) {
    val itemsFlow by itemViewModel.itemsFlow.collectAsState()
    var loading by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var items by rememberSaveable { mutableStateOf(listOf<Item>()) }
    var search by rememberSaveable { mutableStateOf("") }
    val sortedItem = items.sortedWith(compareBy({ it.createdAt }, { it.name }))
    val filteredItem = sortedItem.filter { it.name.trim().lowercase().contains(search) }
    var selectedItem by rememberSaveable { mutableStateOf<Item?>(null) }

    LaunchedEffect(Unit) {
        itemViewModel.getItems()
    }

    itemsFlow.let {
        loading = it is Resource.Loading
        if (it is Resource.Failure) {
            errorMessage = it.errorMessage
        } else if (it is Resource.Success) {
            items = it.data
        }
    }

    AppBar(
        authViewModel = authViewModel,
        navController = navController,
        onSearchChanged = { search = it },
        hasSearchBar = true,
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    navController.navigate(ROUTE_CREATE_ITEM) {
                        launchSingleTop = true
                    }
                },
                shape = CircleShape,
            ) {
                Icon(Icons.Filled.Add, "Add")
            }
        }
    ) {
        Column(modifier = Modifier.padding(top = MaterialTheme.spacing.extraLarge)) {
            if (loading) return@Column LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            if (errorMessage != "") return@Column Text(errorMessage)
            if (selectedItem != null) {
                ItemDialog(
                    item = selectedItem!!,
                    itemViewModel = itemViewModel
                ) {
                    selectedItem = null
                }
            }

            LazyVerticalGrid(
                modifier = Modifier.fillMaxHeight(),
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(MaterialTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                items(count = filteredItem.count(), key = { filteredItem[it].id }) { i ->
                    ItemCard(filteredItem[i], itemViewModel) {
                        selectedItem = filteredItem[i]
                    }
                }
            }
        }
    }
}