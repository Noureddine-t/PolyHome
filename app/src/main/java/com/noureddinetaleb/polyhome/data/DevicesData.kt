package com.noureddinetaleb.polyhome.data

/**
 * Data class for devices data
 * @property id the id of the device
 * @property type the type of the device
 * @property availableCommands the available commands for the device
 * @property power the power of the device
 * @property opening the opening of the device
 * @property openingMode the opening mode of the device
 * @constructor Create empty Devices data
 */
data class DevicesData(
    val id: String,
    val type: String,
    val availableCommands: List<String>,
    val power: Int? = null,
    val opening: Int? = null,
    val openingMode: Int? = null,
)
