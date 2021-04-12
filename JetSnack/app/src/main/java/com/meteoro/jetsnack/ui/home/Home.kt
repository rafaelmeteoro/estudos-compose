package com.meteoro.jetsnack.ui.home

import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.meteoro.jetsnack.R
import com.meteoro.jetsnack.ui.components.JetSnackScaffold
import com.meteoro.jetsnack.ui.theme.JetSnackTheme

@Composable
fun Home(onSnackSelected: (Long) -> Unit) {
    val (currentSection, setCurrentSection) = rememberSaveable {
        mutableStateOf(HomeSections.Feed)
    }
    val navItems = HomeSections.values().toList()
    JetSnackScaffold(
        bottomBar = {
            JetSnackBottomNav(
                currentSection = currentSection,
                onSectionSelected = setCurrentSection,
                item = navItems
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        Crossfade(currentSection) { section ->
            when (section) {
                HomeSections.Feed -> Text(text = "Feed")
                HomeSections.Search -> Unit
                HomeSections.Cart -> Unit
                HomeSections.Profile -> Unit
            }
        }
    }
}

@Composable
private fun JetSnackBottomNav(
    currentSection: HomeSections,
    onSectionSelected: (HomeSections) -> Unit,
    item: List<HomeSections>,
    color: Color = JetSnackTheme.colors.iconPrimary,
    contentColor: Color = JetSnackTheme.colors.iconInteractive
) {

}

private enum class HomeSections(
    @StringRes val title: Int,
    val icon: ImageVector
) {
    Feed(R.string.home_feed, Icons.Outlined.Home),
    Search(R.string.home_search, Icons.Outlined.Search),
    Cart(R.string.home_cart, Icons.Outlined.ShoppingCart),
    Profile(R.string.home_profile, Icons.Outlined.AccountCircle)
}