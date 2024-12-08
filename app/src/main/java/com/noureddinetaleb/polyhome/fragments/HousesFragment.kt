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

class HousesFragment : Fragment() {

    private val homes = ArrayList<HomesData>()
    private lateinit var token: String
    private val mainScope = MainScope()
    private lateinit var homesAdapter: ArrayAdapter<HomesData>

    private val devices = ArrayList<DevicesData>()
    private lateinit var devicesAdapter: DevicesAdapter

    /**
     * Load homes in order to get homeId
     */
    private fun loadHomes() {
        Api().get<List<HomesData>>("https://polyhome.lesmoulinsdudev.com/api/houses", ::loadHomesSuccess, token)
    }

    /**
     * Handle homes loading success then
     * Load devices once homes are uploaded
     */
    private fun loadHomesSuccess(responseCode: Int, loadedHomes: List<HomesData>?) {
        mainScope.launch {
            withContext(Dispatchers.Main) {
                if (responseCode == 200 && loadedHomes != null) {
                    homes.clear()
                    homes.addAll(loadedHomes)
                    updateHomesList()
                    Toast.makeText(requireContext(), "Requête acceptée", Toast.LENGTH_SHORT).show()
                } else if (responseCode == 400) {
                    Toast.makeText(requireContext(), "Les données fournies sont incorrectes", Toast.LENGTH_SHORT).show()
                } else if (responseCode == 403) {
                    Toast.makeText(requireContext(), "Accès interdit (token invalide)", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Une erreur s’est produite au niveau du serveur",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

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
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices",
            ::loadDevicesSuccess,
            token
        )
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

                    Toast.makeText(requireContext(), "Requête acceptée", Toast.LENGTH_SHORT).show()
                } else if (responseCode == 400) {
                    Toast.makeText(requireContext(), "Les données fournies sont incorrectes", Toast.LENGTH_SHORT).show()
                } else if (responseCode == 403) {
                    Toast.makeText(
                        requireContext(),
                        "Accès interdit (token invalide ou ne correspondant pas au propriétaire de la maison ou à un tiers ayant accès)",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (responseCode == 500) {
                    Toast.makeText(
                        requireContext(),
                        "Une erreur s’est produite au niveau du serveur (ou maison indisponible)",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(requireContext(), "Une erreur s’est produite", Toast.LENGTH_SHORT).show()
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
        val view = inflater.inflate(R.layout.fragment_houses, container, false)
        homesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, homes)
        mainScope.launch {
            token = TokenStorage(requireContext()).read()
            loadHomes()
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