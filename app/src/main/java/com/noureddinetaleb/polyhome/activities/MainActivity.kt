package com.noureddinetaleb.polyhome.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.noureddinetaleb.polyhome.R
import com.noureddinetaleb.polyhome.fragments.HomeFragment
import com.noureddinetaleb.polyhome.fragments.HousesFragment
import com.noureddinetaleb.polyhome.fragments.UsersFragment
import com.noureddinetaleb.polyhome.storage.TokenStorage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private val mainScope = MainScope()


    /**
     * Create the activity for drawer layout
     * @param savedInstanceState the saved instance state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }

        val username = intent.getStringExtra("USERNAME") ?: ""
        val navView: NavigationView = findViewById(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        val txtUserName: TextView = headerView.findViewById(R.id.txtUserName)
        txtUserName.text = username
    }

    private var houseId: Int = -1

    /**
     * Set houseId
     * @param data the houseId
     */
    fun setHouseId(data: Int) {
        houseId = data
    }

    /**
     * Get houseId
     * @return houseId
     */
    fun getHouseId(): Int {
        return houseId
    }

    /**
     * Handle navigation item selection
     * @param item selected item
     * @return true if the item is selected
     *        false otherwise
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val homeFragment =
            supportFragmentManager.findFragmentByTag(HomeFragment::class.java.simpleName) as? HomeFragment

        val fragment = when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment())
                .commit()

            R.id.nav_houses -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HousesFragment()).commit()

            R.id.nav_users -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, UsersFragment()).commit()

            R.id.nav_logout -> {
                logout()
                Toast.makeText(this, "Déconnexion avec succès !", Toast.LENGTH_SHORT).show()
                return true
            }

            else -> null
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * Handle logout
     * Redirect to login activity
     * Clear token storage
     * Clear username storage
     * @param item selected item
     */
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /**
     * Handle logout
     * Redirect to login activity
     * Clear token storage
     * Clear username storage
     */
    private fun logout() {
        val tokenStorage = TokenStorage(this)
        mainScope.launch {
            tokenStorage.write("")
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}