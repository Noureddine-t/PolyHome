package com.noureddinetaleb.polyhome.data

/**
 * Data class representing the register data.
 *
 * @property login The name of the user to register.
 * @property password The password of the user chosen during registration.
 * @constructor Initializes a `RegisterData` object with the specified properties.
 */
data class RegisterData(
    val login: String,
    val password: String
)
