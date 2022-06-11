package com.example.seaselmobile.utils

import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions


fun NavController.replace(@IdRes id: Int) {
    val navOptions = graph.lastOrNull()
        ?.let { currentDestination ->
            NavOptions.Builder()
                .setPopUpTo(
                    currentDestination.id,
                    inclusive = true,
                    saveState = false
                ).build()
        }

    navigate(id, null, navOptions)
}

fun NavController.setRoot(@IdRes id: Int) {
    val navOptions = NavOptions.Builder()
        .setPopUpTo(
            graph.findStartDestination().id,
            inclusive = true,
            saveState = false
        ).build()
    navigate(id, null, navOptions)
}