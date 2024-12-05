package com.noureddinetaleb.polyhome.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.noureddinetaleb.polyhome.R
import com.noureddinetaleb.polyhome.data.HomesData
import com.noureddinetaleb.polyhome.storage.TokenStorage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import com.noureddinetaleb.polyhome.api.Api

/**
 * Home activity
 */
class HomeActivity : AppCompatActivity() {
    private val mainScope = MainScope()
    private val homes = ArrayList<HomesData>()

    private fun loadHomes() {
        val token = intent.getStringExtra("TOKEN")
        Api().get<List<HomesData>>("https://polyhome.lesmoulinsdudev.com/api/houses", ::loadHomesSuccess, token)
    }

    private fun loadHomesSuccess(responseCode: Int, loadedHomes: List<HomesData>?) {
        runOnUiThread {
            if (responseCode == 200 && loadedHomes != null) {
                homes.clear()
                homes.addAll(loadedHomes)
                Toast.makeText(this, "Requête acceptée", Toast.LENGTH_SHORT).show()

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
     * Handle logout request
     */
    private fun logout() {
        val tokenStorage = TokenStorage(this)
        mainScope.launch {
            tokenStorage.write("")
            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Create the activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadHomes()
        findViewById<Button>(R.id.logOutBtn).setOnClickListener {
            logout()
        }
    }
}