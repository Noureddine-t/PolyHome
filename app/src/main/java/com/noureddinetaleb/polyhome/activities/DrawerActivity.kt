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
import com.noureddinetaleb.polyhome.data.HomesData
import com.noureddinetaleb.polyhome.fragments.HomesFragment
import com.noureddinetaleb.polyhome.fragments.MainFragment
import com.noureddinetaleb.polyhome.fragments.UsersFragment
import com.noureddinetaleb.polyhome.storage.TokenStorage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Drawer activity.
 *
 * @constructor Create empty Drawer activity.
 * @property drawerLayout The drawer layout for the activity.
 * @property mainScope The main scope for coroutines.
 * @property houseId The houseId to share between fragments.
 * @property homes The homes list to share between fragments.
 *@see NavigationView.OnNavigationItemSelectedListener
 *@see AppCompatActivity
 */
class DrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private val mainScope = MainScope()
    private var houseId: Int = -1
    private var homes = ArrayList<HomesData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)

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
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MainFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }

        val username = intent.getStringExtra("USERNAME") ?: ""
        val headerView = navigationView.getHeaderView(0)
        val txtUserName: TextView = headerView.findViewById(R.id.txtUserName)
        txtUserName.text = username
    }

    /**
     * Set houseId.
     * @param data The houseId.
     */
    fun setHouseId(data: Int) {
        houseId = data
    }

    /**
     * Get houseId.
     * @return houseId.
     */
    fun getHouseId(): Int {
        return houseId
    }

    /**
     * Set homes list.
     * @param data The homes list.
     */
    fun setHomesList(data: ArrayList<HomesData>) {
        homes = data
    }

    /**
     * Get homes list.
     * @return homes list.
     */
    fun getHomesList(): ArrayList<HomesData> {
        return homes
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val mainFragment = supportFragmentManager.findFragmentByTag(MainFragment::class.java.simpleName) as? MainFragment

        val fragment = when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MainFragment())
                .commit()

            R.id.nav_houses -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomesFragment()).commit()

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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /**
     * Handle logout.
     *
     * Redirect to login activity.
     *
     * Clear saved token and username.
     */
    private fun logout() {
        val tokenStorage = TokenStorage(this)
        val loginStorage = TokenStorage(this)
        mainScope.launch {
            tokenStorage.write("")
            loginStorage.write("")
            val intent = Intent(this@DrawerActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}