package com.meteoro.jetsurvey.signinsignup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Text
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.meteoro.jetsurvey.ui.theme.JetSurveyTheme

/**
 * Fragment containing the welcome UI.
 * */
class WelcomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                JetSurveyTheme {
                    Text(text = "Welcome fragment")
                }
            }
        }
    }
}