package com.meteoro.jetsurvey.signinsignup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.meteoro.jetsurvey.Screen
import com.meteoro.jetsurvey.util.Event

class WelcomeViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>> = _navigateTo

    fun handleContinue(email: String) {
        if (userRepository.isKnownUserEmail(email)) {
            _navigateTo.value = Event(Screen.SignIn)
        } else {
            _navigateTo.value = Event(Screen.SignUp)
        }
    }

    fun signInAsGuest() {
        userRepository.signInAsGuest()
        _navigateTo.value = Event(Screen.Survey)
    }
}

@Suppress("UNCHECKED_CAST")
class WelComeViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
            return WelcomeViewModel(UserRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}