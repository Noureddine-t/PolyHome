package com.noureddinetaleb.polyhome.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.noureddinetaleb.polyhome.R
import com.noureddinetaleb.polyhome.api.Api
import com.noureddinetaleb.polyhome.data.DevicesData
import com.noureddinetaleb.polyhome.data.DevicesListData
import com.noureddinetaleb.polyhome.data.HomesData
import com.noureddinetaleb.polyhome.fragments.HomeFragment
import com.noureddinetaleb.polyhome.fragments.HousesFragment
import com.noureddinetaleb.polyhome.fragments.UsersFragment
import com.noureddinetaleb.polyhome.storage.TokenStorage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class DevicesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        token = intent.getStringExtra("TOKEN")?:""
        loadHomes()

        fab = findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.drawerLayout);
        *//*findViewById<Button>(R.id.logOutBtn).setOnClickListener {
            logout()
        }*//*
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
            R.id.nav_houses -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HousesFragment())
                .commit()
            R.id.nav_users -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, UsersFragment())
                .commit()
            R.id.nav_logout -> Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    /**
     * Create the activity
     */






    private val homes = ArrayList<HomesData>()
    private lateinit var token: String
    private val mainScope = MainScope()


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
        runOnUiThread {
            if (responseCode == 200 && loadedHomes != null) {
                homes.clear()
                homes.addAll(loadedHomes)
                Toast.makeText(this, "Requête acceptée", Toast.LENGTH_SHORT).show()
                loadDevices()
            }
            else if(responseCode == 400){
                Toast.makeText(this, "Les données fournies sont incorrectes", Toast.LENGTH_SHORT).show()
            }
            else if(responseCode==403){
                Toast.makeText(this, "Accès interdit (token invalide)", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Une erreur s’est produite au niveau du serveur", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Load devices
     */
    private fun loadDevices() {
        val houseId = homes.find { it.owner }?.houseId ?: -1
        Api().get<DevicesListData>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices", ::loadDevicesSuccess, token)
    }

    /**
     * Handle devices loading success
     */
    private fun loadDevicesSuccess(responseCode: Int, loadedDevices: DevicesListData?) {
        runOnUiThread {
            if (responseCode == 200 && loadedDevices != null) {
                Toast.makeText(this, "Requête acceptée", Toast.LENGTH_SHORT).show()
            }
            else if(responseCode == 400){
                Toast.makeText(this, "Les données fournies sont incorrectes", Toast.LENGTH_SHORT).show()
            }
            else if(responseCode==403){
                Toast.makeText(this, "Accès interdit (token invalide ou ne correspondant pas au propriétaire de la maison ou à un tiers ayant accès)", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Une erreur s’est produite au niveau du serveur", Toast.LENGTH_SHORT).show()
            }
        }
    }




    /*private fun logout() {
        val tokenStorage = TokenStorage(this)
        mainScope.launch {
            tokenStorage.write("")
            val intent = Intent(this@DevicesActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }*/


}