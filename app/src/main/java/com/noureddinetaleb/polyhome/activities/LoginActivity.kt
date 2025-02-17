package com.noureddinetaleb.polyhome.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.noureddinetaleb.polyhome.R
import com.noureddinetaleb.polyhome.api.Api
import com.noureddinetaleb.polyhome.data.LoginData
import com.noureddinetaleb.polyhome.data.TokenData
import com.noureddinetaleb.polyhome.storage.TokenStorage
import com.noureddinetaleb.polyhome.storage.UsernameStorage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Login activity handling login requests.
 *
 * @property mainScope The main scope for coroutines
 */
class LoginActivity : AppCompatActivity() {
    private val mainScope = MainScope()

    /**
     * Handle login request.
     *
     * @see TokenData Class handling token data.
     * @see loginSuccess Function handling login response.
     * @see Api Class handling API requests.
     */
    private fun login() {
        val login = findViewById<EditText>(R.id.txtUserName).text.toString()
        val password = findViewById<EditText>(R.id.txtPassword).text.toString()
        val data = LoginData(login, password)
        saveUsername(login)
        Api().post<LoginData, TokenData>("https://polyhome.lesmoulinsdudev.com/api/users/auth", data, ::loginSuccess)
    }

    /**
     * Handle login response.
     *
     * @param responseCode The response code.
     * @param token The token data received.
     */
    private fun loginSuccess(responseCode: Int, token: TokenData?) {
        runOnUiThread {
            if (responseCode == 200 && token?.token != null) {
                saveToken(token.token)
                val intent = Intent(this, DrawerActivity::class.java)
                val username = findViewById<EditText>(R.id.txtUserName).text.toString()
                intent.putExtra("TOKEN", token.token)
                intent.putExtra("USERNAME", username)

                startActivity(intent)
                Toast.makeText(this, "Connexion réussie", Toast.LENGTH_SHORT).show()
            } else if (responseCode == 404) {
                Toast.makeText(this, "Aucun utilisateur ne correspond aux identifiants donnés", Toast.LENGTH_SHORT)
                    .show()
            } else if (responseCode == 500) {
                Toast.makeText(this, "L: Une erreur s’est produite au niveau du serveur", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "L: Erreur lors de la connexion", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Save token in the storage.
     *
     * @param token The user token to save.
     * @see TokenStorage Class handling token storage.
     */
    private fun saveToken(token: String?) {
        val tokenStorage = TokenStorage(this)
        mainScope.launch {
            if (token != null) {
                tokenStorage.write(token)
            }
        }
    }

    /**
     * Save username in the storage.
     *
     * @param username The username to save
     * @see UsernameStorage Class handling username storage.
     */
    private fun saveUsername(username: String?) {
        val usernameStorage = UsernameStorage(this)
        mainScope.launch {
            if (username != null) {
                usernameStorage.write(username)
            }
        }
    }


    /**
     * Load token and username from storage, if found redirect to main activity, otherwise do nothing.
     *
     * @see TokenStorage Class handling token storage.
     * @see UsernameStorage Class handling username storage.
     * @see DrawerActivity Class handling main activity.
     */
    private fun loadTokenAndUsername() {
        val tokenStorage = TokenStorage(this)
        val usernameStorage = UsernameStorage(this)

        mainScope.launch {
            val savedToken = tokenStorage.read()
            val savedUsername = usernameStorage.read()

            if (savedToken.isNotEmpty() && savedUsername.isNotEmpty()) {
                val intent = Intent(this@LoginActivity, DrawerActivity::class.java)
                intent.putExtra("TOKEN", savedToken)
                intent.putExtra("USERNAME", savedUsername)

                startActivity(intent)
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadTokenAndUsername()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Prevent Dark Mode to being forced on the app.

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.btnConect).setOnClickListener {
            login()
        }
    }

    /**
     * Redirect to the register page on click.
     *
     * @param view The view clicked.
     * @see RegisterActivity Class handling register activity.
     */
    fun registerNewAccount(view: View) {
        val intent = Intent(this, RegisterActivity::class.java);
        startActivity(intent);
    }
}