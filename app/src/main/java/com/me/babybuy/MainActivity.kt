package com.me.babybuy

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.me.babybuy.ui.navigation.AppNavHost
import com.me.babybuy.ui.auth.AuthViewModel
import com.me.babybuy.ui.home.ItemViewModel
import com.me.babybuy.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * The [AndroidEntryPoint] of the Jetpack Compose application.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val authViewModel by viewModels<AuthViewModel>()
    private val itemViewModel by viewModels<ItemViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                AppNavHost(authViewModel, itemViewModel)
            }
        }
    }
}