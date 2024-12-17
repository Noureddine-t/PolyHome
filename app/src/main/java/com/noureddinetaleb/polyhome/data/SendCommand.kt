package com.noureddinetaleb.polyhome.data

/**
 * Data class representing the command to send to the appropriate device.
 *
 * @property command The command to send.
 * @constructor Initializes a `SendCommand` object with the specified command.
 * @see com.noureddinetaleb.polyhome.data.DevicesData.availableCommands
 */
data class SendCommand(
    val command: String
)
