package com.noureddinetaleb.polyhome.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.noureddinetaleb.polyhome.R
import com.noureddinetaleb.polyhome.adapter.DevicesAdapter
import com.noureddinetaleb.polyhome.api.Api
import com.noureddinetaleb.polyhome.data.DevicesData
import com.noureddinetaleb.polyhome.data.HomesData
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
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    private val homes = ArrayList<HomesData>()
    private lateinit var token: String
    private val mainScope = MainScope()
    private var houseId = -1
    private val usersWithAccess = ArrayList<UsersWithAccessData>()

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
                    val houseCount = view?.findViewById<TextView>(R.id.house_count)
                    houseCount?.text = homes.size.toString()
                    Toast.makeText(requireContext(), "Requête acceptée", Toast.LENGTH_SHORT).show()
                    houseId = homes.find { it.owner }?.houseId ?: -1
                    loadUsers(houseId)
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

    private fun loadUsers(houseId : Int) {
        Api().get<List<UsersWithAccessData>>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users", ::loadUsersSuccess,token)
    }


    private fun loadUsersSuccess(responseCode: Int, loadedUsers: List<UsersWithAccessData>?) {
        mainScope.launch {
            withContext(Dispatchers.Main) {
                if (responseCode == 200 && loadedUsers != null) {
                    usersWithAccess.clear()
                    usersWithAccess.addAll(loadedUsers)
                    val usersWithAccessCount= view?.findViewById<TextView>(R.id.user_count)
                    usersWithAccessCount?.text = usersWithAccess.size.toString()
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

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

        housesButton?.setOnClickListener {
            val housesFragment = HousesFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, housesFragment)
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

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}