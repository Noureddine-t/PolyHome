package com.noureddinetaleb.polyhome.fragments

import android.app.AlertDialog
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
import com.noureddinetaleb.polyhome.adapter.UsersAdapter
import com.noureddinetaleb.polyhome.api.Api
import com.noureddinetaleb.polyhome.data.SendUserLogin
import com.noureddinetaleb.polyhome.data.UsersLoginData
import com.noureddinetaleb.polyhome.data.UsersWithAccessData
import com.noureddinetaleb.polyhome.storage.TokenStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Fragment for users
 * @constructor Create empty Users fragment
 */
class UsersFragment : Fragment() {

    private val users = ArrayList<String>()
    private val mainScope = MainScope()
    private lateinit var token: String
    private lateinit var usersAdapter: ArrayAdapter<String>

    private val usersWithAccess = ArrayList<UsersWithAccessData>()
    private lateinit var usersWithAccessAdapter: UsersAdapter

    private var houseId : Int = -1

    /**
     * Load users in order to get login
     */
    private fun loadUsers() {
        Api().get<List<UsersLoginData>>("https://polyhome.lesmoulinsdudev.com/api/users", ::loadUsersSuccess)
    }

    /**
     * Handle users loading success
     * @param responseCode the response code from the server
     * @param loadedUsers the list of users loaded from the server
     * @see updateUsersList
     */
    private fun loadUsersSuccess(responseCode: Int, loadedUsers: List<UsersLoginData>?) {
        mainScope.launch {
            withContext(Dispatchers.Main) {
                if (responseCode == 200 && loadedUsers != null) {
                    users.clear()
                    //Fixme: removing users with access from the list of all users is buggy not updating the list
                    val usersWithAccessLogins = usersWithAccess.map { it.userLogin }
                    users.addAll(loadedUsers.map { it.login }.filter { it !in usersWithAccessLogins })
                    updateUsersList()
                    Toast.makeText(requireContext(), "La liste de tous les utilisateurs n'ayant pas accès a bien été retournée", Toast.LENGTH_SHORT).show()
                } else if (responseCode == 500) {
                    Toast.makeText(requireContext(), "all U: Une erreur s’est produite au niveau du serveur", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "all U: Erreur est survenue", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Update users list
     */
    private fun updateUsersList() {
        mainScope.launch {
            withContext(Dispatchers.Main) {
                usersAdapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * Initialize spinners for users
     */
    private fun initializeSpinners() {
        val spinHomes = view?.findViewById<Spinner>(R.id.spinUsers)
        spinHomes?.adapter = usersAdapter
    }

    /**
     * Load users with access
     */
    private fun loadUsersWithAccess(houseId: Int) {
        Api().get<List<UsersWithAccessData>>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users", ::loadUsersWithAccessSuccess, token
        )
    }

    /**
     * Handle 'users with access' loading success
     */
    private fun loadUsersWithAccessSuccess(responseCode: Int, loadedUsersWithAccess: List<UsersWithAccessData>?) {
        mainScope.launch {
            withContext(Dispatchers.Main) {
                if (responseCode == 200 && loadedUsersWithAccess != null) {
                    usersWithAccess.clear()
                    usersWithAccess.addAll(loadedUsersWithAccess)
                    updateUsersWithAccessList()
                    Toast.makeText(requireContext(), "La liste des utilisateurs ayant accès a bien été retournée", Toast.LENGTH_SHORT).show()
                } else if (responseCode == 500) {
                    Toast.makeText(requireContext(), "Access U: Une erreur s’est produite au niveau du serveur", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Access U: Erreur est survenue", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUsersWithAccessList() {
        mainScope.launch {
            withContext(Dispatchers.Main) {
                usersWithAccessAdapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * Initialize users with access list
     */
    private fun initializeUsersWithAccessList() {
        val usersWithAccessListView = view?.findViewById<ListView>(R.id.lstUsersWithAccess)
        usersWithAccessListView?.adapter = usersWithAccessAdapter
    }

    /**
     * Update users with access list
     */
    private fun giveUserAccess(selectedUser: String) {
        val user = SendUserLogin(selectedUser)
        if (houseId != -1) {
            Api().post<SendUserLogin>(
                "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users", user, ::giveUserAccessSuccess, token
            )
        } else {
            mainScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Aucune maison n'a été trouvée", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Handle 'update access' response then
     * Load users with access
     */
    private fun giveUserAccessSuccess(responseCode: Int) {
        mainScope.launch {
            withContext(Dispatchers.Main) {
                when (responseCode) {
                    200 -> {
                        Toast.makeText(requireContext(), "Accès accordé", Toast.LENGTH_SHORT).show()
                        loadUsersWithAccess(houseId)
                        loadUsers()
                    }
                    400 -> {
                        Toast.makeText(requireContext(), "Up: Les données fournies sont incorrectes", Toast.LENGTH_SHORT).show()
                    }
                    403 -> {
                        Toast.makeText(requireContext(), "Up: Accès interdit (token invalide ou ne correspondant pas au propriétaire de la maison)", Toast.LENGTH_SHORT).show()
                    }
                    500 -> {
                        Toast.makeText(requireContext(), "Up: Une erreur s’est produite au niveau du serveur", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(requireContext(), "Up: Erreur est survenue", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * Remove user access to your house
     */
    private fun removeUserAccess(selectedUser: String) {
        val user = SendUserLogin(selectedUser)
        if (houseId != -1) {
            Api().delete<SendUserLogin>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users", user, ::removeUserAccessSuccess, token
            )
        } else {
            mainScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "D: Aucune maison n'a été trouvée", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Handle 'remove user access' response then
     * Load users with access
     */
    private fun removeUserAccessSuccess(responseCode: Int) {
        mainScope.launch {
            withContext(Dispatchers.Main) {
                when (responseCode) {
                    200 -> {
                        Toast.makeText(requireContext(), "Suppression réalisée", Toast.LENGTH_SHORT).show()
                        loadUsersWithAccess(houseId)
                        loadUsers()
                    }
                    400 -> {
                        Toast.makeText(requireContext(), "D: Les données fournies sont incorrectes", Toast.LENGTH_SHORT).show()
                    }
                    403 -> {
                        Toast.makeText(requireContext(), "D: Accès interdit (token invalide ou ne correspondant pas au propriétaire de la maison)", Toast.LENGTH_SHORT).show()
                    }
                    500 -> {
                        Toast.makeText(requireContext(), "D: Une erreur s’est produite au niveau du serveur", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(requireContext(), "D: Erreur est survenue", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * Handle  users fragment creation and
     * Get houseId from [DrawerActivity] to prevent sending requests again
     * @param savedInstanceState the saved instance state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val drawerActivity = activity as? DrawerActivity
        houseId = drawerActivity?.getHouseId() ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_users, container, false)
        // load users list and initialize spinners
        usersAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, users)
        mainScope.launch {
            token = TokenStorage(requireContext()).read()
            loadUsersWithAccess(houseId)
            initializeUsersWithAccessList()
            loadUsers()
            initializeSpinners()
        }

        // grant access to a user to your house
        val spinUsers = view.findViewById<Spinner>(R.id.spinUsers)
        val btnAdd = view.findViewById<Button>(R.id.btnAddUser)
        btnAdd.setOnClickListener {
            val selectedUser = spinUsers.selectedItem as String
            giveUserAccess(selectedUser)
        }

        //manage the deletion of a user
        usersWithAccessAdapter = UsersAdapter(requireContext(), usersWithAccess){ userLogin ->
            val alertDialog = AlertDialog.Builder(context)
                .setTitle("Confirmation de suppression")
                .setMessage("Êtes-vous sûr de vouloir supprimer l'utilisateur $userLogin ? Cette action est irréversible.")
                .setPositiveButton("Confirmer") { dialog, _ -> removeUserAccess(userLogin)
                    dialog.dismiss()
                }
                .setNegativeButton("Annuler") { dialog, _ -> dialog.dismiss() }
                .create()
            alertDialog.show()
        }

        return view
    }

}