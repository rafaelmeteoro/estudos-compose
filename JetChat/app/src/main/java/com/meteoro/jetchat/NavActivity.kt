package com.meteoro.jetchat

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.accompanist.insets.ProvideWindowInsets
import com.meteoro.jetchat.components.JetChatScaffold
import com.meteoro.jetchat.conversation.BackPressedHandler
import com.meteoro.jetchat.conversation.LocalBackPressedDispatcher
import com.meteoro.jetchat.databinding.ContentMainBinding
import kotlinx.coroutines.launch

/**
 * Main activity for the app.
 * */
class NavActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            // Provide WindowInsets to our content. We don't want to consume them, so that
            // they keep being pass down the view hierarchy (since we're using fragments).
            ProvideWindowInsets(consumeWindowInsets = false) {
                CompositionLocalProvider(
                    LocalBackPressedDispatcher provides this.onBackPressedDispatcher
                ) {
                    val scaffoldState = rememberScaffoldState()

                    val openDrawerEvent = viewModel.drawerShouldBeOpened.observeAsState()
                    if (openDrawerEvent.value == true) {
                        // Open drawer and reset state in VM.
                        LaunchedEffect(Unit) {
                            scaffoldState.drawerState.open()
                            viewModel.resetOpenDrawerAction()
                        }
                    }

                    // Intercepts back navigation when the drawer is open
                    val scope = rememberCoroutineScope()
                    if (scaffoldState.drawerState.isOpen) {
                        BackPressedHandler {
                            scope.launch {
                                scaffoldState.drawerState.close()
                            }
                        }
                    }

                    JetChatScaffold(
                        scaffoldState = scaffoldState,
                        onChatClicked = {
                            findNavController().popBackStack(R.id.nav_home, true)
                            scope.launch {
                                scaffoldState.drawerState.close()
                            }
                        },
                        onProfileClicked = {
                            val bundle = bundleOf("userId" to it)
                            findNavController().navigate(R.id.nav_profile, bundle)
                            scope.launch {
                                scaffoldState.drawerState.close()
                            }
                        },
                    ) {
                        // TODO: Fragments inflated via AndroidViewBinding don't work as expected
                        // https://issuetracker.google.com/179915946
                        // AndroidViewBinding(ContentMainBinding::inflate)
                        FragmentAwareAndroidViewBinding(ContentMainBinding::inflate)
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController().navigateUp() || super.onSupportNavigateUp()
    }

    private fun findNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }
}