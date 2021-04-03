package com.meteoro.jetsurvey.signinsignup

import java.util.regex.Pattern

// Consider an email valid if there's some text before and after a "@"
private const val EMAIL_VALID_REGEX = "^(.+)@(.+)\$"

class EmailState : TextFieldState(validator = ::isEmailValid, errorFor = ::emailValidationError)

/**
 * Returns an error to be displayed or null if no error was found
 * */
private fun emailValidationError(email: String): String {
    return "Invalid email: $email"
}

private fun isEmailValid(email: String): Boolean {
    return Pattern.matches(EMAIL_VALID_REGEX, email)
}