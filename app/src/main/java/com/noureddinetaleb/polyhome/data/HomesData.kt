package com.noureddinetaleb.polyhome.data

/**
 * Data class representing the data of a house.
 *
 * @property houseId The id of the house.
 * @property owner Tells whether the user is the owner of the house or not (true if owner, false otherwise).
 * @constructor Initializes a `HomesData` object with the specified properties.
 */
data class HomesData(
    val houseId: Int,
    val owner: Boolean,
)
