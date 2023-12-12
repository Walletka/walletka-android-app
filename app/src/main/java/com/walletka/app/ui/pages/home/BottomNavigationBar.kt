package com.walletka.app.ui.pages.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.walletka.app.R


sealed class BottomNavItem(
    val title: String,
    val icon: Int,
    var _selected: Boolean,
    val _onClick: () -> Unit
) {
    data class Home (var selected: Boolean, var onClick: () -> Unit) :
        BottomNavItem(
            "Home",
            R.drawable.baseline_assured_workload_24,
            selected,
            onClick
        )

    data class List (var selected: Boolean, var onClick: () -> Unit):
        BottomNavItem(
            "Trade",
            R.drawable.baseline_assured_workload_24,
            selected,
            onClick
        )

    data class Analytics (var selected: Boolean, var onClick: () -> Unit):
        BottomNavItem(
            "Apps",
            R.drawable.baseline_assured_workload_24,
            selected,
            onClick
        )

}

@Composable
fun BottomNavigation(items: List<BottomNavItem>) {

    NavigationBar(modifier = Modifier.height(75.dp)) {
        items.forEach { item ->
            AddItem(
                screen = item
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomNavItem,
    colors: NavigationBarItemColors = NavigationBarItemDefaults.colors(),
) {
    NavigationBarItem(
        label = {
            Text(text = screen.title)
        },
        icon = {
            Icon(
                painterResource(id = screen.icon),
                contentDescription = screen.title,
            )
        },
        selected = screen._selected,
        alwaysShowLabel = true,
        onClick = screen._onClick,
        colors = colors
    )
}