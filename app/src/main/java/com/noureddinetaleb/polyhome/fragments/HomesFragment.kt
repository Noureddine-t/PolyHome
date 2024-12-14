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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Homes fragment
 * @constructor Create empty Homes fragment
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
     * Update homes list
     */
    private fun updateHomesList() {
        mainScope.launch {
            withContext(Dispatchers.Main) {
                homesAdapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * Initialize spinners for homes
     */
    private fun initializeSpinners() {
        val spinHomes = view?.findViewById<Spinner>(R.id.spinHomes)
        spinHomes?.adapter = homesAdapter
    }

    /**
     * Load devices
     */
    private fun loadDevices(houseId: Int) {
        Api().get<DevicesListData>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices", ::loadDevicesSuccess, token)
    }

    /**
     * Handle devices loading success
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
     * Initialize devices list
     */
    private fun initializeDevicesList() {
        val devicesListView = view?.findViewById<ListView>(R.id.lstDevices)
        devicesListView?.adapter = devicesAdapter
    }


    /**
     * Send command to device
     */
    private fun sendCommand(houseId: Int, deviceId: String, commandToSend: String) {
        val command = SendCommand(commandToSend)
        Api().post<SendCommand>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command", command, ::sendCommandSuccess, token)
    }

    /**
     * Handle command sending success
     */
    private fun sendCommandSuccess(responseCode: Int) {
        MainScope().launch {
            withContext(Dispatchers.Main) {
                when (responseCode) {
                    200 -> {
                       // Toast.makeText(requireContext(), "Commande envoyé avec succèss", Toast.LENGTH_SHORT).show()
                    }
                    400 -> {
                        Toast.makeText(requireContext(), "C: Les données fournies sont incorrectes pour charger les périphériques", Toast.LENGTH_SHORT).show()
                    }
                    500 -> {
                        Toast.makeText(requireContext(), "C: Une erreur s’est produite au niveau du serveur", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(requireContext(), "C: Une erreur s’est produite", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * Envoyer la commande "Mode Économie" à tous les appareils
     */
    /**
     * Apply Economy Mode
     * Turns off all lights in the house.
     */
    private fun applyEconomyMode() {
        for (device in devices) {
            if (device.type == "light") {
                sendCommand(houseId, device.id, "TURN OFF")
            }
        }
    }


    /**
     * Apply Night Mode
     * Closes all shutters and garage doors in the house and turns off all lights.
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
     * Apply Emergency Mode
     * Opens all shutters and garage doors in the house.
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
     * Apply Alert Mode
     * Flashes all lights and opens all shutters and garage doors.
     */
    private fun applyAlertMode() {
        for (device in devices) {
            when (device.type) {
                "light" -> {
                    mainScope.launch {
                        repeat(10) {
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




    /**
     * Handle homes fragment creation and
     * get homes list from [DrawerActivity] to prevent sending a request to the server
     * @param savedInstanceState saved instance state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val drawerActivity = activity as? DrawerActivity
        homes = drawerActivity?.getHomesList() ?: ArrayList()
    }

    /**
     * Handle homes fragment creation
     * @param inflater layout inflater
     * @param container view group
     * @param savedInstanceState saved instance state
     * @return view
     *       fragment view
     *       null if an exception is caught
     * @see initializeSpinners
     * @see initializeDevicesList
     * @see loadDevices
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {

        // loading homes list and initializing spinners
        val view = inflater.inflate(R.layout.fragment_homes, container, false)
        homesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, homes)
        mainScope.launch {
            token = TokenStorage(requireContext()).read()
            updateHomesList()
            initializeSpinners()
        }

        //loading devices based on selected house
        val btnValidate = view.findViewById<Button>(R.id.btnValidate)
        val spinHomes = view.findViewById<Spinner>(R.id.spinHomes)
        btnValidate.setOnClickListener {
            val selectedHome = spinHomes.selectedItem as HomesData
            houseId = selectedHome.houseId
            loadDevices(houseId)
            mainScope.launch {
                initializeDevicesList()
            }
        }

        // managing devices commands
        devicesAdapter = DevicesAdapter(requireContext(), devices) { deviceId, command ->
            sendCommand(houseId, deviceId, command)
        }

        // managing info buttons for each mode
        val economyInfoButton: ImageButton = view.findViewById(R.id.btnEconomyInfo)
        val nightInfoButton: ImageButton = view.findViewById(R.id.btnNightInfo)
        val emergencyInfoButton: ImageButton = view.findViewById(R.id.btnEmergencyInfo)
        val alertInfoButton: ImageButton = view.findViewById(R.id.btnAlertInfo)

        // managing mode buttons
        val economyModeButton: Button = view.findViewById(R.id.btnEconomyMode)
        val nightModeButton: Button = view.findViewById(R.id.btnNightMode)
        val emergencyModeButton: Button = view.findViewById(R.id.btnEmergencyMode)
        val alertModeButton: Button = view.findViewById(R.id.btnAlertMode)

        economyModeButton.setOnClickListener {
            applyEconomyMode()
        }

        nightModeButton.setOnClickListener {
            applyNightMode()
        }

        emergencyModeButton.setOnClickListener {
            applyEmergencyMode()
        }

        alertModeButton.setOnClickListener {
            applyAlertMode()
        }

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