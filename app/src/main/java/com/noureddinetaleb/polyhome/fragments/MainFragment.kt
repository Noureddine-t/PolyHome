package com.noureddinetaleb.polyhome.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.noureddinetaleb.polyhome.R
import com.noureddinetaleb.polyhome.activities.DrawerActivity
import com.noureddinetaleb.polyhome.api.Api
import com.noureddinetaleb.polyhome.data.HomesData
import com.noureddinetaleb.polyhome.data.UsersWithAccessData
import com.noureddinetaleb.polyhome.storage.TokenStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragment : Fragment() {

    private val homes = ArrayList<HomesData>()
    private lateinit var token: String
    private val mainScope = MainScope()
    private var houseId = -1
    private val usersWithAccess = ArrayList<UsersWithAccessData>()

    /**
     * Load homes
     */
    private fun loadHomes() {
        Api().get<List<HomesData>>("https://polyhome.lesmoulinsdudev.com/api/houses", ::loadHomesSuccess, token)
    }

    /**
     * Handle homes loading success then
     * Load devices once homes are uploaded
     * @param responseCode the response code from the server
     * @param loadedHomes the list of homes loaded from the server
     * @see loadUsers
     */
    private fun loadHomesSuccess(responseCode: Int, loadedHomes: List<HomesData>?) {
        mainScope.launch {
            withContext(Dispatchers.Main) {
                if (responseCode == 200 && loadedHomes != null) {
                    homes.clear()
                    homes.addAll(loadedHomes)
                    val houseCount = view?.findViewById<TextView>(R.id.house_count)
                    houseCount?.text = homes.size.toString()
                    Toast.makeText(requireContext(), "Liste de mes maisons reçue avec succès (count)", Toast.LENGTH_SHORT).show()
                    houseId = homes.find { it.owner }?.houseId ?: -1
                    loadUsers(houseId)
                } else if (responseCode == 400) {
                    Toast.makeText(requireContext(), "H count: Les données fournies sont incorrectes pour charger mes maisons", Toast.LENGTH_SHORT).show()
                } else if (responseCode == 403) {
                    Toast.makeText(requireContext(), "H count: Accès interdit (token invalide)", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "H count: Une erreur s’est produite au niveau du serveur", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Load users with access to the house
     * @param houseId the house id
     */
    private fun loadUsers(houseId: Int) {
        Api().get<List<UsersWithAccessData>>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users",
            ::loadUsersSuccess,
            token
        )
    }

    /**
     * Handle 'users with access' loading success
     * @param responseCode the response code from the server
     * @param loadedUsers the list of users with access loaded from the server
     * @see sendDataToActivity send houseId to the activity to be used in other fragments
     */
    private fun loadUsersSuccess(responseCode: Int, loadedUsers: List<UsersWithAccessData>?) {
        mainScope.launch {
            withContext(Dispatchers.Main) {
                if (responseCode == 200 && loadedUsers != null) {
                    usersWithAccess.clear()
                    usersWithAccess.addAll(loadedUsers)
                    val usersWithAccessCount = view?.findViewById<TextView>(R.id.user_count)
                    usersWithAccessCount?.text = usersWithAccess.size.toString()
                    Toast.makeText(requireContext(), "La liste des utilisateurs ayant accès a bien été retournée (count)", Toast.LENGTH_SHORT).show()
                    sendDataToActivity()
                }
                else if (responseCode == 500) {
                    Toast.makeText(requireContext(), "U count: Une erreur s’est produite au niveau du serveur", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(requireContext(), "U count: Erreur est survenue", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Send houseId and
     * homes list to the activity
     * to be used in other fragments
     */
    private fun sendDataToActivity() {
        val activity = requireActivity() as? DrawerActivity
        activity?.setHouseId(houseId)
        activity?.setHomesList(homes)
    }

    /**
     * Handle main page fragment creation
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     *       the fragment view
     *       null if an exception is caught
     *@see loadHomes
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        mainScope.launch {
            token = TokenStorage(requireContext()).read()
            loadHomes()
        }
        return view
    }

    /**
     * Handle main page fragment creation
     * creating buttons to redirect to other fragments (houses and users)
     * @param view the fragment view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val housesButton = view.findViewById<Button>(R.id.manage_houses_button)
        val usersButton = view.findViewById<Button>(R.id.manage_users_button)

        housesButton?.setOnClickListener {
            val homesFragment = HomesFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, homesFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        usersButton?.setOnClickListener {
            val usersFragment = UsersFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, usersFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

}