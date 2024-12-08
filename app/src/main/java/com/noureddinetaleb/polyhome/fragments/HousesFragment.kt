package com.noureddinetaleb.polyhome.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
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


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [HousesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
                }
                else if(responseCode == 400){
                    Toast.makeText(requireContext(), "Les données fournies sont incorrectes", Toast.LENGTH_SHORT).show()
                }
                else if(responseCode==403){
                    Toast.makeText(requireContext(), "Accès interdit (token invalide)", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(requireContext(), "Une erreur s’est produite au niveau du serveur", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateHomesList() {
        mainScope.launch {
            withContext(Dispatchers.Main) {
                homesAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun initializeSpinners() {
        val spinHomes = view?.findViewById<Spinner>(R.id.spinHomes)
        spinHomes?.adapter = homesAdapter
    }

    /**
     * Load devices
     */
    private fun loadDevices(houseId: Int) {
        Api().get<DevicesListData>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices", ::loadDevicesSuccess, token)
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

    private fun initializeDevicesList() {
        val ordersListView = view?.findViewById<ListView>(R.id.lstDevices)
        ordersListView?.adapter = devicesAdapter
    }


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_houses, container, false)
        homesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, homes)
        mainScope.launch {
            token = TokenStorage(requireContext()).read()
            loadHomes()
            initializeSpinners()
            devicesAdapter = DevicesAdapter(requireContext(), devices)
            initializeDevicesList()
        }

        val btnValidate = view.findViewById<Button>(R.id.btnValidate)
        btnValidate.setOnClickListener {
            val spinHomes = view.findViewById<Spinner>(R.id.spinHomes)
            val selectedHome = spinHomes?.selectedItem as? HomesData
            val houseId = selectedHome?.houseId
            loadDevices(houseId?:-1)

        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HousesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HousesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



}