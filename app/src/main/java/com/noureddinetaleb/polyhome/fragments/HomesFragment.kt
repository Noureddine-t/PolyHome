package com.noureddinetaleb.polyhome.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.noureddinetaleb.polyhome.R
import com.noureddinetaleb.polyhome.activities.DrawerActivity
import com.noureddinetaleb.polyhome.adapter.DevicesAdapter
import com.noureddinetaleb.polyhome.api.Api
import com.noureddinetaleb.polyhome.data.DevicesData
import com.noureddinetaleb.polyhome.data.DevicesListData
import com.noureddinetaleb.polyhome.data.HomesData
import com.noureddinetaleb.polyhome.data.SendCommand
import com.noureddinetaleb.polyhome.storage.TokenStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Homes fragment to manage devices in selected house.
 *
 * @property homes The list of homes user has access to.
 * @property token The token of the user.
 * @property mainScope The main scope for coroutines.
 * @property homesAdapter The adapter for homes list spinner.
 * @property devices The list of devices in the house.
 * @property devicesAdapter The adapter for devices list view.
 * @property houseId The id of a chosen house.
 */
class HomesFragment : Fragment() {

    private var homes = ArrayList<HomesData>()
    private lateinit var token: String
    private val mainScope = MainScope()
    private lateinit var homesAdapter: ArrayAdapter<HomesData>

    private val devices = ArrayList<DevicesData>()
    private lateinit var devicesAdapter: DevicesAdapter
    private var houseId = -1

    /**
     * Update homes list in the spinner.
     */
    private fun updateHomesList() {
        mainScope.launch {
            withContext(Dispatchers.Main) {
                homesAdapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * Initialize spinners for homes.
     */
    private fun initializeSpinners() {
        val spinHomes = view?.findViewById<Spinner>(R.id.spinHomes)
        spinHomes?.adapter = homesAdapter
    }

    /**
     * Load devices from the server based on the chosen house.
     *
     * @param houseId The id of the chosen house.
     * @see loadDevicesSuccess
     * @see Api
     */
    private fun loadDevices(houseId: Int) {
        Api().get<DevicesListData>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices", ::loadDevicesSuccess, token)
    }

    /**
     * Handle devices loading success.
     *
     * @param responseCode The response code from the server.
     * @param loadedDevices The list of devices loaded from the server.
     * @see DevicesAdapter
     */
    private fun loadDevicesSuccess(responseCode: Int, loadedDevices: DevicesListData?) {
        MainScope().launch {
            withContext(Dispatchers.Main) {
                if (responseCode == 200 && loadedDevices != null) {
                    devices.clear()
                    devices.addAll(loadedDevices.devices)
                    devicesAdapter.notifyDataSetChanged()

                    Toast.makeText(requireContext(), "Liste des périphériques reçus avec succès", Toast.LENGTH_SHORT).show()
                } else if (responseCode == 400) {
                    Toast.makeText(requireContext(), "D: Les données fournies sont incorrectes pour charger les périphériques", Toast.LENGTH_SHORT).show()
                } else if (responseCode == 403) {
                    Toast.makeText(requireContext(), "D: Accès interdit (token invalide ou ne correspondant pas au propriétaire de la maison ou à un tiers ayant accès)", Toast.LENGTH_SHORT).show()
                } else if (responseCode == 500) {
                    Toast.makeText(requireContext(), "D: Une erreur s’est produite au niveau du serveur (ou maison indisponible)", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "D: Une erreur s’est produite", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Initialize devices list.
     */
    private fun initializeDevicesList() {
        val devicesListView = view?.findViewById<ListView>(R.id.lstDevices)
        devicesListView?.adapter = devicesAdapter
    }


    /**
     * Send command to device based on the house id.
     *
     * @param houseId The id of the chosen house.
     * @param deviceId The id of the device to send the command to.
     * @param commandToSend The command to send.
     * @see SendCommand
     * @see sendCommandSuccess
     * @see Api
     */
    private fun sendCommand(houseId: Int, deviceId: String, commandToSend: String) {
        val command = SendCommand(commandToSend)
        Api().post<SendCommand>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command", command, ::sendCommandSuccess, token)
    }

    /**
     * Handle command sending success.
     *
     * @param responseCode The response code from the server.
     */
    private fun sendCommandSuccess(responseCode: Int) {
        MainScope().launch {
            withContext(Dispatchers.Main) {
                when (responseCode) {
                    200 -> {}//Toast.makeText(requireContext(), "Commande envoyé avec succèss", Toast.LENGTH_SHORT).show()
                    400 -> Toast.makeText(requireContext(), "C: Les données fournies sont incorrectes pour charger les périphériques", Toast.LENGTH_SHORT).show()
                    500 -> Toast.makeText(requireContext(), "C: Une erreur s’est produite au niveau du serveur", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(requireContext(), "C: Une erreur s’est produite", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    /**
     * Apply Economy Mode: Turns off all lights in the house.
     *
     * @see sendCommand
     */
    private fun applyEconomyMode() {
        for (device in devices) {
            if (device.type == "light") {
                sendCommand(houseId, device.id, "TURN OFF")
            }
        }
    }


    /**
     * Apply Night Mode: Closes all shutters and garage door in the house and turns off all lights.
     *
     * @see sendCommand
     */
    private fun applyNightMode() {
        for (device in devices) {
            when (device.type) {
                "sliding shutter", "rolling shutter", "garage door" -> {
                    sendCommand(houseId, device.id, "CLOSE")
                }

                "light" -> {
                    sendCommand(houseId, device.id, "TURN OFF")
                }
            }
        }
    }


    /**
     * Apply Emergency Mode: Opens all shutters and garage door in the house.
     *
     * @see sendCommand
     */
    private fun applyEmergencyMode() {
        for (device in devices) {
            when (device.type) {
                "sliding shutter", "rolling shutter", "garage door" -> {
                    sendCommand(houseId, device.id, "OPEN")
                }

                "light" -> {
                    sendCommand(houseId, device.id, "TURN OFF")
                }
            }
        }
    }

    /**
     * Apply Alert Mode: Flashes all lights and opens all shutters and garage doors.
     *
     * @see sendCommand
     */
    private fun applyAlertMode() {
        for (device in devices) {
            when (device.type) {
                "light" -> {
                    mainScope.launch {
                        repeat(12) {
                            sendCommand(houseId, device.id, "TURN ON")
                            delay(500)
                            sendCommand(houseId, device.id, "TURN OFF")
                            delay(500)
                        }
                        sendCommand(houseId, device.id, "TURN ON")

                    }
                }

                "sliding shutter", "rolling shutter", "garage door" -> {
                    sendCommand(houseId, device.id, "OPEN")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val drawerActivity = activity as? DrawerActivity
        homes = drawerActivity?.getHomesList() ?: ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Loading homes list and initializing spinners
        val view = inflater.inflate(R.layout.fragment_homes, container, false)
        homesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, homes)
        mainScope.launch {
            token = TokenStorage(requireContext()).read()
            updateHomesList()
            initializeSpinners()
        }

        // Mode buttons
        val economyModeButton: Button = view.findViewById(R.id.btnEconomyMode)
        val nightModeButton: Button = view.findViewById(R.id.btnNightMode)
        val emergencyModeButton: Button = view.findViewById(R.id.btnEmergencyMode)
        val alertModeButton: Button = view.findViewById(R.id.btnAlertMode)

        // Info buttons for each mode
        val economyInfoButton: ImageButton = view.findViewById(R.id.btnEconomyInfo)
        val nightInfoButton: ImageButton = view.findViewById(R.id.btnNightInfo)
        val emergencyInfoButton: ImageButton = view.findViewById(R.id.btnEmergencyInfo)
        val alertInfoButton: ImageButton = view.findViewById(R.id.btnAlertInfo)

        // Hiding mode buttons and info buttons
        economyModeButton.visibility = View.GONE
        nightModeButton.visibility = View.GONE
        emergencyModeButton.visibility = View.GONE
        alertModeButton.visibility = View.GONE
        economyInfoButton.visibility = View.GONE
        nightInfoButton.visibility = View.GONE
        emergencyInfoButton.visibility = View.GONE
        alertInfoButton.visibility = View.GONE

        // Loading devices based on selected house
        val btnValidate = view.findViewById<Button>(R.id.btnValidate)
        val spinHomes = view.findViewById<Spinner>(R.id.spinHomes)
        btnValidate.setOnClickListener {
            val selectedHome = spinHomes.selectedItem as HomesData
            houseId = selectedHome.houseId
            loadDevices(houseId)
            mainScope.launch {
                initializeDevicesList()
            }
            // Showing mode buttons and info buttons
            economyModeButton.visibility = View.VISIBLE
            nightModeButton.visibility = View.VISIBLE
            emergencyModeButton.visibility = View.VISIBLE
            alertModeButton.visibility = View.VISIBLE
            economyInfoButton.visibility = View.VISIBLE
            nightInfoButton.visibility = View.VISIBLE
            emergencyInfoButton.visibility = View.VISIBLE
            alertInfoButton.visibility = View.VISIBLE
        }

        // Managing devices commands
        devicesAdapter = DevicesAdapter(requireContext(), devices) { deviceId, command ->
            sendCommand(houseId, deviceId, command)
        }

        economyModeButton.setOnClickListener { applyEconomyMode() }
        nightModeButton.setOnClickListener { applyNightMode() }
        emergencyModeButton.setOnClickListener { applyEmergencyMode() }
        alertModeButton.setOnClickListener { applyAlertMode() }

        // Managing info buttons for each mode
        economyInfoButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Mode Économie")
            builder.setMessage("Ce mode permet de réduire la consommation énergétique en éteignant toutes les lumières lorsque personne n'est à la maison.")
            builder.setPositiveButton("Fermer", null)
            builder.show()
        }
        nightInfoButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Mode Sortie/Nuit")
            builder.setMessage("Ce mode permet d'éteindre toutes les lumières,de fermer tous les volets et le garage lorsque vous quittez la maison ou pendant la nuit, afin d'optimiser la sécurité et la consommation d'énergie.")
            builder.setPositiveButton("Fermer", null)
            builder.show()
        }
        emergencyInfoButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Mode Secours")
            builder.setMessage("Ce mode permet d'eteindre toutes les lumières et ouvrir tous les volets/portes.Reserver uniquement pour des situations anormales, telles qu'un incendie ou une fuite de gaz.")
            builder.setPositiveButton("Fermer", null)
            builder.show()
        }
        alertInfoButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Mode Alerte")
            builder.setMessage("Ce mode fait clignoter toutes les lumières pour signaler un danger ou une situation d'urgence en plus d'ouvrir tous les volets et portes.")
            builder.setPositiveButton("Fermer", null)
            builder.show()
        }

        return view
    }

}