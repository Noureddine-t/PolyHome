package com.noureddinetaleb.polyhome.data

/**
 * Data class representing the data of a device.
 *
 * @property id The id of the device.
 * @property type The type of the device (sliding shutter,rolling shutter, garage door, light).
 * @property availableCommands The available commands for the device (["open", "close", "stop"] for shutters and doors, ["on", "off"] for lights).
 * @property power Tells whether the light is powered, 1 for on and 0 for off else `null` for other devices.
 * @property opening The current opening percentage of the device (0 to 1), or `null` if the device is light.
 * @property openingMode The current opening mode of the device: 0 for open, 1 for close, 2 for stop, or `null` if the device is a light.
 * @constructor Initializes a `DevicesData` object with the specified properties.
 * Some properties, such as `power`, `opening`, or `openingMode`, are optional
 * and default to `null` if not relevant to the specified device type.
 */
data class DevicesData(
    val id: String,
    val type: String,
    val availableCommands: List<String>,
    val power: Int? = null,
    val opening: Int? = null,
    val openingMode: Int? = null,
)
