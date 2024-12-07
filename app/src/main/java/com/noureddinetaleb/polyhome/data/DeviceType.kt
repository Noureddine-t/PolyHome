package com.noureddinetaleb.polyhome.data

/**
 * Enum class for device type
 * @constructor Create empty Device type
 * @property ROLLING_SHUTTER
 * @property SLIDING_SHUTTER
 * @property GARAGE_DOOR
 * @property LIGHT
 */
enum class DeviceType {
    ROLLING_SHUTTER,
    SLIDING_SHUTTER, // not available in the API
    GARAGE_DOOR,
    LIGHT;

    companion object {
        /**
         * From value
         * @param value
         * @return device type
         */
        fun fromValue(value: String): DeviceType? {
            return when (value.lowercase()) {
                "rolling shutter" -> ROLLING_SHUTTER
                "sliding shutter" -> SLIDING_SHUTTER
                "garage door" -> GARAGE_DOOR
                "light" -> LIGHT
                else -> null
            }
        }
    }
}