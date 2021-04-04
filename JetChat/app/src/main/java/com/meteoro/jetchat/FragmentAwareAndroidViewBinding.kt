package com.meteoro.jetchat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.fragment.app.findFragment
import androidx.viewbinding.ViewBinding

/**
 * TODO: Fragments inflated via AndroidViewBinding don't work as expected
 * https://issuetracker.google.com/179915946
 * */
@Composable
fun <T : ViewBinding> FragmentAwareAndroidViewBinding(
    bindingBlock: (LayoutInflater, ViewGroup, Boolean) -> T,
    modifier: Modifier = Modifier,
    update: T.() -> Unit = {}
) {
    val fragmentContainerViews = remember { mutableStateListOf<FragmentContainerView>() }
    AndroidViewBinding(bindingBlock, modifier = modifier) {
        fragmentContainerViews.clear()
        val rootGroup = root as? ViewGroup
        if (rootGroup != null) {
            findFragmentContainersViews(rootGroup, fragmentContainerViews)
        }
        update()
    }
    val activity = LocalContext.current as FragmentActivity
    fragmentContainerViews.forEach { container ->
        DisposableEffect(container) {
            // Find the right FragmentManager
            val fragmentManager = try {
                val parentFragment = container.findFragment<Fragment>()
                parentFragment.childFragmentManager
            } catch (e: Exception) {
                activity.supportFragmentManager
            }
            // Now find the fragment inflated via the FragmentContainerView
            val existingFragment = fragmentManager.findFragmentById(R.id.nav_host_fragment)
            if (existingFragment != null) {
                val fragmentView = existingFragment.requireView()
                // Remove the Fragment from whatever old parent it had
                // (this is most likely an old binding if it is non-null)
                (fragmentView.parent as? ViewGroup)?.run {
                    removeView(fragmentView)
                }
                // Re-add it to the layout if it was moved to RESUMED before
                // this Composable ran
                container.addView(existingFragment.requireView())
            }
            onDispose {
                if (existingFragment != null && !fragmentManager.isStateSaved) {
                    // If the state isn't saved, that means that some state change
                    // has removed this Composable from the hierarchy
                    fragmentManager.commit {
                        remove(existingFragment)
                    }
                }
            }
        }
    }
}

private fun findFragmentContainersViews(
    viewGroup: ViewGroup,
    list: SnapshotStateList<FragmentContainerView>
) {
    viewGroup.forEach {
        if (it is FragmentContainerView) {
            list += it
        } else if (it is ViewGroup) {
            findFragmentContainersViews(it, list)
        }
    }
}