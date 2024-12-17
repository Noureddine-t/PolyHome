package com.noureddinetaleb.polyhome.data

/**
 * Data class representing the user login to send in order to grant or remove access to the owner's house.
 *
 * @property userLogin The name of the user login whome we want to grant access.
 * @constructor Initializes a `SendUserLogin` object with the specified user login.
 */
data class SendUserLogin(
    val userLogin: String,
)
