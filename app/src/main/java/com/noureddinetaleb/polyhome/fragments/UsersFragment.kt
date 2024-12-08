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
import com.noureddinetaleb.polyhome.adapter.UsersAdapter
import com.noureddinetaleb.polyhome.api.Api
import com.noureddinetaleb.polyhome.data.HomesData
import com.noureddinetaleb.polyhome.data.SendUserLogin
import com.noureddinetaleb.polyhome.data.UsersLoginData
import com.noureddinetaleb.polyhome.data.UsersWithAccessData
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
 * Use the [UsersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UsersFragment : Fragment() {


    private val users = ArrayList<String>()
    private val mainScope = MainScope()
    private lateinit var token: String
    private lateinit var usersAdapter: ArrayAdapter<String>

    private val homes = ArrayList<HomesData>()
    private val usersWithAccess = ArrayList<UsersWithAccessData>()
    private lateinit var usersWithAccessAdapter: UsersAdapter

    private var houseId = -1

    /**
     * Load users in order to get login
     */
    private fun loadUsers() {
        Api().get<List<UsersLoginData>>("https://polyhome.lesmoulinsdudev.com/api/users", ::loadUsersSuccess)
    }

    /**
     * Handle users loading success
     */
    private fun loadUsersSuccess(responseCode: Int, loadedUsers: List<UsersLoginData>?) {
        mainScope.launch {
            withContext(Dispatchers.Main) {
        if (responseCode == 200 && loadedUsers != null) {
            users.clear()
            users.addAll(loadedUsers.map { it.login })
            updateUsersList()
            Toast.makeText(requireContext(), "La liste des utilisateurs a bien été retournée", Toast.LENGTH_SHORT).show()
        }
        else if(responseCode == 500){
            Toast.makeText(requireContext(), "Une erreur s’est produite au niveau du serveur", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(requireContext(), "Erreur est survenue", Toast.LENGTH_SHORT).show()
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
     * Load homes in order to get homeId to manage users with access
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
                    Toast.makeText(requireContext(), "Requête acceptée", Toast.LENGTH_SHORT).show()
                    houseId = homes.find { it.owner }?.houseId ?: -1
                    loadUsersWithAccess(houseId)
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

    /**
     * Load users with access
     */
    private fun loadUsersWithAccess(houseId : Int) {
        Api().get<List<UsersWithAccessData>>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users", ::loadUsersWithAccessSuccess,token)
    }

    /**
     * Handle 'users with access' loading success
     */
    private fun loadUsersWithAccessSuccess(responseCode: Int, loadedUsers: List<UsersWithAccessData>?) {
        mainScope.launch {
            withContext(Dispatchers.Main) {
        if (responseCode == 200 && loadedUsers != null) {
            usersWithAccess.clear()
            usersWithAccess.addAll(loadedUsers)
            usersWithAccessAdapter.notifyDataSetChanged()
            Toast.makeText(requireContext(), "La liste des utilisateurs ayant accès a bien été retournée", Toast.LENGTH_SHORT).show()
        }
        else if(responseCode == 500){
            Toast.makeText(requireContext(), "Une erreur s’est produite au niveau du serveur", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(requireContext(), "Erreur est survenue", Toast.LENGTH_SHORT).show()
        }
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
    private fun updateAccess(selectedUser: String) {
        val user = SendUserLogin(selectedUser)
        if (houseId != -1) {
            Api().post<SendUserLogin>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users", user, ::updateAccessSuccess, token)
        }
        else {
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
    private fun updateAccessSuccess(responseCode: Int) {
        mainScope.launch {
            withContext(Dispatchers.Main) {
                if (responseCode == 200) {
                    loadUsersWithAccess(houseId)
                    Toast.makeText(requireContext(), "Accès accordé", Toast.LENGTH_SHORT).show()
                }
                else if(responseCode == 400){
                    Toast.makeText(requireContext(), "Les données fournies sont incorrectes", Toast.LENGTH_SHORT).show()
                }
                else if (responseCode == 403) {
                    Toast.makeText(requireContext(), "Accès interdit (token invalide ou ne correspondant pas au propriétaire de la maison)", Toast.LENGTH_SHORT).show()
                }
                else if (responseCode == 500) {
                    Toast.makeText(requireContext(), "Une erreur s’est produite au niveau du serveur", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(requireContext(), "Erreur est survenue", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    /**
     * Remove user access to your house
     */
    fun removeUserAccess(selectedUser: String){
        val user = SendUserLogin(selectedUser)
        if (houseId != -1) {
            Api().delete<SendUserLogin>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users", user, ::removeUserAccessSuccess, token)
        }
        else {
            mainScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Aucune maison n'a été trouvée", Toast.LENGTH_SHORT).show()
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
                if (responseCode == 200) {
                    loadUsersWithAccess(houseId)
                    Toast.makeText(requireContext(), "Suppression réalisée", Toast.LENGTH_SHORT).show()
                }
                else if(responseCode == 400){
                    Toast.makeText(requireContext(), "Les données fournies sont incorrectes", Toast.LENGTH_SHORT).show()
                }
                else if (responseCode == 403) {
                    Toast.makeText(requireContext(), "Accès interdit (token invalide ou ne correspondant pas au propriétaire de la maison)", Toast.LENGTH_SHORT).show()
                }
                else if (responseCode == 500) {
                    Toast.makeText(requireContext(), "Une erreur s’est produite au niveau du serveur", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(requireContext(), "Erreur est survenue", Toast.LENGTH_SHORT).show()
                }

            }
        }
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
        val view = inflater.inflate(R.layout.fragment_users, container, false)
        usersAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, users)
        mainScope.launch {
            loadUsers()
            initializeSpinners()

            token = TokenStorage(requireContext()).read()
            loadHomes()
            usersWithAccessAdapter = UsersAdapter(requireContext(), usersWithAccess)
            usersWithAccessAdapter.setUserActionListener(object : UsersAdapter.OnUserActionListener  {
                override fun onRemoveUser(userLogin: String) {
                    removeUserAccess(userLogin)
                }
            })
            initializeUsersWithAccessList()
        }
        val btnAdd = view.findViewById<Button>(R.id.btnAddUser)
        btnAdd.setOnClickListener {
            val spinUsers = view.findViewById<Spinner>(R.id.spinUsers)
            val selectedUser = spinUsers?.selectedItem as? String
            updateAccess(selectedUser?:"")
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
         * @return A new instance of fragment UsersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UsersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}