package com.noureddinetaleb.polyhome.data

/**
 * Data class representing the data of the users with access data to the user's house.
 *
 * @property userLogin The name of the user login with access to the owner's house.
 * @property owner Tells whether the user is the owner of the house or not (1 if owner, 0 if not).
 * @constructor Initializes a `UsersWithAccessData` object with the specified properties.
 */
data class UsersWithAccessData(
    val userLogin: String,
    val owner: Int,
)
