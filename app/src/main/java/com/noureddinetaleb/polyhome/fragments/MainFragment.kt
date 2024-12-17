package com.noureddinetaleb.polyhome.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
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

/**
 * Main fragment
 *
 * Shows number of houses and users with access to the house (owner included).
 *
 * @property homes The list of homes.
 * @property usersWithAccess The list of users with access to the house.
 * @property token The token of the user.
 * @property houseId The house id.
 * @property mainScope The main scope for coroutines.
 *
 */
class MainFragment : Fragment() {

    private val homes = ArrayList<HomesData>()
    private lateinit var token: String
    private val mainScope = MainScope()
    private var houseId = -1
    private val usersWithAccess = ArrayList<UsersWithAccessData>()

    /**
     * Load homes from the server the user has access to.
     *
     * @see loadHomesSuccess
     * @see Api
     */
    private fun loadHomes() {
        Api().get<List<HomesData>>("https://polyhome.lesmoulinsdudev.com/api/houses", ::loadHomesSuccess, token)
    }

    /**
     * Handle homes loading success.
     *
     * @param responseCode The response code from the server.
     * @param loadedHomes The list of homes loaded from the server.
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
     * Load users with access to the house from the server.
     *
     * @param houseId The house id
     * @see loadUsersSuccess
     * @see Api
     */
    private fun loadUsers(houseId: Int) {
        Api().get<List<UsersWithAccessData>>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users", ::loadUsersSuccess, token)
    }

    /**
     * Handle 'users with access' loading success.
     *
     * @param responseCode The response code from the server.
     * @param loadedUsers The list of users with access loaded from the server.
     * @see sendDataToActivity
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
                } else if (responseCode == 500) {
                    Toast.makeText(requireContext(), "U count: Une erreur s’est produite au niveau du serveur", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "U count: Erreur est survenue", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Send houseId and
     * homes list to the activity
     * to be used in other fragments.
     */
    private fun sendDataToActivity() {
        val activity = requireActivity() as? DrawerActivity
        activity?.setHouseId(houseId)
        activity?.setHomesList(homes)
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val housesButton = view.findViewById<Button>(R.id.manage_houses_button)
        val usersButton = view.findViewById<Button>(R.id.manage_users_button)

        // Redirect to the homes fragment
        housesButton?.setOnClickListener {
            val homesFragment = HomesFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, homesFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // Redirect to the users fragment
        usersButton?.setOnClickListener {
            val usersFragment = UsersFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, usersFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

}