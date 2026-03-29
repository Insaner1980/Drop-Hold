package com.finntek.dropandhold.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.finntek.dropandhold.R
import com.finntek.dropandhold.ui.screen.CollectionScreen
import com.finntek.dropandhold.ui.screen.PlayScreen
import com.finntek.dropandhold.ui.screen.SettingsScreen

enum class TopLevelRoute(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    Play("play", R.string.nav_play, Icons.Default.PlayArrow),
    Collection("collection", R.string.nav_collection, Icons.Default.Star),
    Settings("settings", R.string.nav_settings, Icons.Default.Settings),
}

@Composable
fun DropAndHoldNavHost() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            NavigationBar {
                TopLevelRoute.entries.forEach { route ->
                    val label = stringResource(route.labelRes)
                    NavigationBarItem(
                        icon = { Icon(route.icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = currentDestination?.hierarchy?.any { it.route == route.route } == true,
                        onClick = {
                            navController.navigate(route.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelRoute.Play.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(TopLevelRoute.Play.route) { PlayScreen() }
            composable(TopLevelRoute.Collection.route) { CollectionScreen() }
            composable(TopLevelRoute.Settings.route) { SettingsScreen() }
        }
    }
}
