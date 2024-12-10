package com.noureddinetaleb.polyhome.data

/**
 * Data class for users with access to the house
 * @property userLogin the name of the user login with access to the house
 * @property owner tells whether the user is the owner of the house or not
 * @constructor Create empty Users with access data
 */
data class UsersWithAccessData(
    val userLogin: String,
    val owner: Int,
)
