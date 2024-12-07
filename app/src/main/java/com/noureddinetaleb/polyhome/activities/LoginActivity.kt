package com.noureddinetaleb.polyhome.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.noureddinetaleb.polyhome.R
import com.noureddinetaleb.polyhome.api.Api
import com.noureddinetaleb.polyhome.data.LoginData
import com.noureddinetaleb.polyhome.data.TokenData
import com.noureddinetaleb.polyhome.storage.TokenStorage
import com.noureddinetaleb.polyhome.storage.UsernameStorage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Login activity
 */
class LoginActivity : AppCompatActivity() {
    private val mainScope = MainScope()

    /**
     * Handle login request
     */
    private fun login() {
        val login = findViewById<EditText>(R.id.txtUserName).text.toString()
        val password = findViewById<EditText>(R.id.txtPassword).text.toString()
        val data = LoginData(login, password)
        saveUsername(login)
        Api().post<LoginData, TokenData>("https://polyhome.lesmoulinsdudev.com/api/users/auth", data, ::loginSuccess)
    }

    /**
     * Handle login response
     */
    private fun loginSuccess(responseCode: Int, token: TokenData?) {
        runOnUiThread {
            if (responseCode == 200 && token?.token != null) {
                saveToken(token.token)
                val intent = Intent(this, MainActivity::class.java)
                val username = findViewById<EditText>(R.id.txtUserName).text.toString()
                intent.putExtra("TOKEN", token.token)
                intent.putExtra("USERNAME", username)

                startActivity(intent)
                Toast.makeText(this, "Connexion réussie", Toast.LENGTH_SHORT).show()
            } else if (responseCode == 404){
                Toast.makeText(this, "Aucun utilisateur ne correspond aux identifiants donnés", Toast.LENGTH_SHORT).show()
            }
            else if(responseCode==500){
                Toast.makeText(this, " Une erreur s’est produite au niveau du serveur", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Erreur lors de la connexion", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Save token in the storage
     */
    private fun saveToken(token: String?) {
        val tokenStorage = TokenStorage(this)
        mainScope.launch {
            if (token != null) {
                tokenStorage.write(token)
            }
        }
    }

    private fun saveUsername(username: String?) {
        val usernameStorage = UsernameStorage(this)
        mainScope.launch {
            if (username != null) {
                usernameStorage.write(username)
            }
        }
    }


    /**
     *
     */
    private fun loadTokenAndUsername(){
        val tokenStorage = TokenStorage(this)
        val usernameStorage = UsernameStorage(this)

        mainScope.launch {
            val savedToken = tokenStorage.read()
            val savedUsername = usernameStorage.read()

            if (savedToken.isNotEmpty() && savedUsername.isNotEmpty()) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.putExtra("TOKEN", savedToken)
                intent.putExtra("USERNAME", savedUsername)

                startActivity(intent)
                finish()
            }
        }
    }



    /**
     * Load token when the activity is resumed
     */
    override fun onResume() {
        super.onResume()
        loadTokenAndUsername()
    }

    /**
     * Create the activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.btnConect).setOnClickListener {
            login()
        }
    }

    /**
     * Redirect to the register page on click
     */
    public fun registerNewAccount(view: View)
    {
        val intent = Intent(this, RegisterActivity::class.java);
        startActivity(intent);
    }
}