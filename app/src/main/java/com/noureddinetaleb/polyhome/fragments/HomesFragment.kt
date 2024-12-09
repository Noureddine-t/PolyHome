package com.noureddinetaleb.polyhome.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
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
import com.noureddinetaleb.polyhome.storage.TokenStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomesFragment : Fragment() {

    private var homes = ArrayList<HomesData>()
    private lateinit var token: String
    private val mainScope = MainScope()
    private lateinit var homesAdapter: ArrayAdapter<HomesData>

    private val devices = ArrayList<DevicesData>()
    private lateinit var devicesAdapter: DevicesAdapter

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
        val ordersListView = view?.findViewById<ListView>(R.id.lstDevices)
        ordersListView?.adapter = devicesAdapter
    }

    //TODO : Send commands to devices
    //TODO : Add additional functionalities: close all , open all , turn off all , turn on all...

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
     * @see loadHomes
     * @see initializeSpinners
     * @see initializeDevicesList
     * @see loadDevices
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // managing houses display
        val view = inflater.inflate(R.layout.fragment_homes, container, false)
        homesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, homes)
        mainScope.launch {
            token = TokenStorage(requireContext()).read()
            updateHomesList()
            initializeSpinners()
            devicesAdapter = DevicesAdapter(requireContext(), devices)
            initializeDevicesList()
        }

        //managing devices display based on selected house
        val btnValidate = view.findViewById<Button>(R.id.btnValidate)
        btnValidate.setOnClickListener {
            val spinHomes = view.findViewById<Spinner>(R.id.spinHomes)
            val selectedHome = spinHomes?.selectedItem as? HomesData
            val houseId = selectedHome?.houseId
            loadDevices(houseId ?: -1)
        }
        return view
    }

}