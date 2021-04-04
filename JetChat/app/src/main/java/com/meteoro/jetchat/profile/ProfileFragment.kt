package com.meteoro.jetchat.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ViewWindowInsetObserver
import com.meteoro.jetchat.MainViewModel
import com.meteoro.jetchat.ui.theme.JetChatTheme

class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Consider using safe args plugin
        val userId = arguments?.getString("userId")
        viewModel.setUserId(userId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(inflater.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            // Create a ViewWindowInsetObserver using this view, and call start() to
            // start listening now. The WindowInsets instance is returned, allowing us to
            // provide it to AmbientWindowInsets in our content below.
            val windowInsets = ViewWindowInsetObserver(this).start()

            setContent {
                val userData by viewModel.userData.observeAsState()

                CompositionLocalProvider(LocalWindowInsets provides windowInsets) {
                    JetChatTheme {
                        if (userData == null) {
                            ProfileError()
                        } else {
                            ProfileScreen(
                                userData = userData!!,
                                onNavIconPressed = {
                                    activityViewModel.openDrawer()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}