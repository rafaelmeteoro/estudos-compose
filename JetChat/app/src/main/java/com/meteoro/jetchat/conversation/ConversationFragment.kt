package com.meteoro.jetchat.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ViewWindowInsetObserver
import com.google.accompanist.insets.navigationBarsPadding
import com.meteoro.jetchat.MainViewModel
import com.meteoro.jetchat.R
import com.meteoro.jetchat.data.exampleUiState
import com.meteoro.jetchat.ui.theme.JetChatTheme

class ConversationFragment : Fragment() {

    private val activityViewModel: MainViewModel by activityViewModels()

    @OptIn(ExperimentalAnimatedInsets::class) // Opt-in to experiment animated insets support
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(inflater.context).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

            // Create a ViewWindowInsetObserver using this view, and call start() to
            // start listening now. The WindowInsets instance is returned, allowing us to
            // provide it to AmbientWindowInsets in our content below.
            val windowInsets = ViewWindowInsetObserver(this)
                // We use the `windowInsetsAnimationsEnabled` parameter to enable animated
                // insets support. This allows our `ConversationContent` to animate with the
                // on-screen keyboard (IME) as it enters/exits the screen.
                .start(windowInsetsAnimationsEnabled = true)

            setContent {
                CompositionLocalProvider(
                    LocalBackPressedDispatcher provides requireActivity().onBackPressedDispatcher,
                    LocalWindowInsets provides windowInsets
                ) {
                    JetChatTheme {
                        ConversationContent(
                            uiState = exampleUiState,
                            navigateToProfile = { user ->
                                // Click callback
                                val bundle = bundleOf("userId" to user)
                                findNavController().navigate(
                                    R.id.nav_profile,
                                    bundle
                                )
                            },
                            onNavIconPressed = {
                                activityViewModel.openDrawer()
                            },
                            // Add padding so that we are inset from any left/right navigation bars
                            // (usually shown when in landscape orientation)
                            modifier = Modifier.navigationBarsPadding(bottom = false)
                        )
                    }
                }
            }
        }
    }
}